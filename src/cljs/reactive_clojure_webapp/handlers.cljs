(ns reactive-clojure-webapp.handlers
    (:require [re-frame.core :as re-frame]
              [reactive-clojure-webapp.db :as db]))

(re-frame/register-handler
 :initialize-db
 (fn  [_ _]
   db/default-db))

(re-frame/register-handler
  :send-message
  re-frame/debug
  (fn [db [_ text]]
    (-> db
        (update-in [:messages] conj {:nickname "Misterioso" :text text :timestamp 000000000}))))
