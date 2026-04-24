(ns url-shortener.server
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.adapter.jetty :as jetty]
            [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.json :refer [wrap-json-response]]
            [url-shortener.db :as db]
            [clojure.string :as str]
            [cheshire.core :as json])
  (:import java.security.MessageDigest))

(defn generate-short-code [url]
  (let [hash (-> (MessageDigest/getInstance "SHA-1")
                 (.digest (.getBytes url))
                 (->> (map #(format "%02x" %)) (apply str)))]
    (subs hash 0 6)))

(defn handle-post [normal-url]
  (if (str/blank? normal-url)
    {:status 400 :body (json/generate-string {:error "Missing URL"})}
    (let [short-code (generate-short-code normal-url)]
      (try
        (db/save-url! short-code normal-url)
        {:status 200 :body short-code}
        (catch Exception e
          (if (db/url-exists? short-code)
            {:status 200 :body short-code}
            {:status 500 :body (json/generate-string {:error "DB error"})}))))))

(defn handle-get [short-code]
  (if-let [record (db/find-normal-url short-code)]
    {:status 200 :body (:normal_url record)}
    {:status 404 :body (json/generate-string {:error "Not found"})}))

(defn handle-put [short-code new-normal-url]
  (if (db/url-exists? short-code)
    (do
      (db/update-normal-url! short-code new-normal-url)
      {:status 200 :body "OK"})
    {:status 404 :body (json/generate-string {:error "Not found"})}))

(defn handle-delete [short-code]
  (if (db/url-exists? short-code)
    (do
      (db/delete-url! short-code)
      {:status 200 :body "OK"})
    {:status 404 :body (json/generate-string {:error "Not found"})}))

(defn parse-body [request]
  (let [body (slurp (:body request))]
    (try
      (json/parse-string body true)
      (catch Exception _ {}))))

(defroutes app-routes
  (POST "/normal-url" request
    (let [params (parse-body request)
          url (or (:url params) (get params "url"))]
      (handle-post url)))
  (GET "/:short-code" [short-code]
    (handle-get short-code))
  (PUT "/:short-code" request
    (let [params (parse-body request)
          new-url (or (:url params) (get params "url"))
          short-code (-> request :params :short-code)]
      (handle-put short-code new-url)))
  (DELETE "/:short-code" [short-code]
    (handle-delete short-code))
  (route/not-found (json/generate-string {:error "Not Found"})))

(def app
  (-> app-routes
      wrap-params
      wrap-json-response))

(defn start-server [port]
  (println "Starting server on port" port)
  (jetty/run-jetty app {:port (Integer/parseInt port) :join? false}))
