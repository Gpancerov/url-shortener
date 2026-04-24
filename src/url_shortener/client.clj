(ns url-shortener.client
  (:require [clj-http.client :as http]
            [cheshire.core :as json]
            [clojure.string :as str]))

(def base-url "http://localhost:8080")

(defn create-short-url [normal-url]
  (try
    (let [response (http/post (str base-url "/normal-url")
                              {:body (json/generate-string {:url normal-url})
                               :content-type :json
                               :accept :json
                               :as :string})]
      (if (= 200 (:status response))
        (:body response)
        (str "Ошибка сервера: " (:status response))))
    (catch Exception e
      (str "Ошибка соединения: " (.getMessage e)))))

(defn get-normal-url [short-code]
  (try
    (let [response (http/get (str base-url "/" short-code)
                             {:accept :json
                              :as :string
                              :throw-exceptions false})]
      (if (= 200 (:status response))
        (:body response)
        (if (= 404 (:status response))
          "Ошибка"
          (str "HTTP ошибка: " (:status response)))))
    (catch Exception e
      (str "Ошибка: " (.getMessage e)))))

(defn update-url [short-code new-normal-url]
  (try
    (let [response (http/put (str base-url "/" short-code)
                             {:body (json/generate-string {:url new-normal-url})
                              :content-type :json
                              :accept :json
                              :as :string
                              :throw-exceptions false})]
      (if (= 200 (:status response))
        "OK"
        (if (= 404 (:status response))
          "Ошибка"
          (str "Ошибка: " (:status response)))))
    (catch Exception e
      (str "Ошибка: " (.getMessage e)))))

(defn delete-url [short-code]
  (try
    (let [response (http/delete (str base-url "/" short-code)
                                {:accept :json
                                 :as :string
                                 :throw-exceptions false})]
      (if (= 200 (:status response))
        "OK"
        (if (= 404 (:status response))
          "Ошибка"
          (str "Ошибка: " (:status response)))))
    (catch Exception e
      (str "Ошибка: " (.getMessage e)))))