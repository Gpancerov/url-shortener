(ns url-shortener.db
  (:require [clojure.java.jdbc :as jdbc]))

(def db-spec
  {:classname   "org.sqlite.JDBC"
   :subprotocol "sqlite"
   :subname     "url_shortener.db"})

(defn init-db []
  (jdbc/execute! db-spec
                 ["CREATE TABLE IF NOT EXISTS urls (
       id INTEGER PRIMARY KEY AUTOINCREMENT,
       short_code TEXT UNIQUE NOT NULL,
       normal_url TEXT NOT NULL,
       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
     )"]))

(defn save-url! [short-code normal-url]
  (jdbc/insert! db-spec :urls {:short_code short-code :normal_url normal-url}))

(defn find-normal-url [short-code]
  (first (jdbc/query db-spec
                     ["SELECT normal_url FROM urls WHERE short_code = ?" short-code])))

(defn update-normal-url! [short-code new-normal-url]
  (jdbc/update! db-spec :urls
                {:normal_url new-normal-url}
                ["short_code = ?" short-code]))

(defn delete-url! [short-code]
  (jdbc/delete! db-spec :urls ["short_code = ?" short-code]))

(defn url-exists? [short-code]
  (pos? (first (vals (first (jdbc/query db-spec
                                        ["SELECT COUNT(*) as cnt FROM urls WHERE short_code = ?" short-code]))))))
