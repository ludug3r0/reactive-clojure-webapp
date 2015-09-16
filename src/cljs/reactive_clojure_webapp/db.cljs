(ns reactive-clojure-webapp.db)

(def default-db
  {:connected-users [{:nickname "Rafael"} {:nickname "Aquiles"}]
   :messages [{:nickname "Rafael" :text "E aê, Aquiles?" :timestamp 1442362189}
              {:nickname "Aquiles" :text "Opa!!" :timestamp 1442362192}]
   :input-message ""
   :nickname "guest1234"})
