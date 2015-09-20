(ns reactive-clojure-webapp.db)

(def default-db
  {:user "guest1234"
   :server nil
   :chat {:connected-users []
          :messages        []
          :input-message   ""}})
