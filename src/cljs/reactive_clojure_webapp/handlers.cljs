(ns reactive-clojure-webapp.handlers
    (:require [re-frame.core :as re-frame]
              [reactive-clojure-webapp.db :as db]))

(re-frame/register-handler
 :initialize-db
 (fn  [_ _]
   db/default-db))

(re-frame/register-handler
  :set-input-message
  (fn [db [_ text]]
    (assoc-in db [:input-message] text)))

(re-frame/register-handler
  :send-message
  re-frame/debug
  (fn [db _]
    (update-in db [:messages] conj {:nickname "Misterioso" :text (:input-message db) :timestamp 000000000})))
