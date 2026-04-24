(ns url-shortener.core
  (:require [url-shortener.db :as db]
            [url-shortener.server :as server]
            [url-shortener.cli :as cli])
  (:gen-class))

(defn -main
  "Если передан аргумент порта -> запустить сервер.
   Иначе -> запустить клиент (меню)."
  [& args]
  (db/init-db)
  (if (first args)
    (do
      (println "Запуск сервера на порту" (first args))
      (server/start-server (first args))
      ;; сервер работает, но чтобы не завершался сразу
      (println "Сервер запущен. Нажмите Ctrl+C для остановки.")
      (while true (Thread/sleep 1000)))
    (do
      (println "Клиент URL Shortener")
      (cli/run-cli))))