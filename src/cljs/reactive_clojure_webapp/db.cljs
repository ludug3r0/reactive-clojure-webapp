(ns reactive-clojure-webapp.db)

(defn rand-guest
  ([] (rand-guest 4))
  ([n]
   (let [numbers (range 0 9)
         code (take n (repeatedly #(rand-nth numbers)))]
     (str "guest" (reduce str code)))))

(def default-db
  {:user (rand-guest)
   :server nil
   :chat {:loaded?         false
          :connected-users []
          :messages        []
          :input-message   ""}})
