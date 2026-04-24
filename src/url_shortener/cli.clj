(ns url-shortener.cli
  (:require [url-shortener.client :as client]
            [clojure.string :as str]))

(defn read-line-nonempty [prompt]
  (loop []
    (print prompt)
    (flush)
    (let [input (str/trim (read-line))]
      (if (not (str/blank? input))
        input
        (do (println "Введите непустое значение.")
            (recur))))))

(defn menu-create []
  (let [url (read-line-nonempty "Введите обычный URL для сокращения:\n  > ")]
    (println "Отправка запроса...")
    (println "Ответ: " (client/create-short-url url))))

(defn menu-show []
  (let [short (read-line-nonempty "Введите короткий URL:\n  > ")]
    (println "Отправка запроса...")
    (println "Ответ: " (client/get-normal-url short))))

(defn menu-update []
  (println "Введите через пробел короткий URL и новый обычный URL:")
  (print "  > ")
  (flush)
  (let [input (str/trim (read-line))
        parts (str/split input #"\s+" 2)]
    (if (= 2 (count parts))
      (let [short (first parts)
            new-url (second parts)]
        (println "Отправка запроса...")
        (println "Ответ: " (client/update-url short new-url)))
      (println "Ошибка: нужно ввести два значения через пробел"))))

(defn menu-delete []
  (let [short (read-line-nonempty "Введите короткий URL для удаления:\n  > ")]
    (println "Отправка запроса...")
    (println "Ответ: " (client/delete-url short))))

(defn show-menu []
  (println "\n=== URL Shortener ===")
  (println "1. Создать")
  (println "2. Показать")
  (println "3. Изменить")
  (println "4. Удалить")
  (println "5. Выйти")
  (print "Выберите действие: ")
  (flush))

(defn run-cli []
  (loop []
    (show-menu)
    (let [choice (str/trim (read-line))]
      (case choice
        "1" (do (menu-create) (recur))
        "2" (do (menu-show) (recur))
        "3" (do (menu-update) (recur))
        "4" (do (menu-delete) (recur))
        "5" (println "До свидания!")
        (do (println "Неверный выбор. Попробуйте снова.") (recur))))))