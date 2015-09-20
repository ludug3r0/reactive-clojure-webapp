(ns reactive-clojure-webapp.handlers
    (:require [re-frame.core :as re-frame]
              [reactive-clojure-webapp.db :as db]
              [reactive-clojure-webapp.server :as server]))

(re-frame/register-handler
 :initialize-db
 (fn  [_ _]
   db/default-db))

(re-frame/register-handler
  :set-input-message
  (fn [db [_ text]]
    (assoc-in db [:chat :input-message] text)))

(re-frame/register-handler
  :send-message
  (fn [db _]
    (server/dispatch [:chat/broadcast (get-in db [:chat :input-message] )])
    db))

(re-frame/register-handler
  :chat/load
  (fn [db [_ {:keys [messages users]}]]
    (-> db
        (assoc-in [:chat :loaded?] true)
        (assoc-in [:chat :messages] messages)
        (assoc-in [:chat :users] users))))

(re-frame/register-handler
  :chat/message
  (fn [db [_ {:keys [nickname text timestamp]}]]
    (update-in db [:chat :messages] conj {:timestamp timestamp :nickname nickname :text text})))

(re-frame/register-handler
  :log-into-server
  (fn [db _]
    (re-frame/dispatch [:server/log-into-server (get-in db [:chat :nickname])])
    db))

(re-frame/register-handler
  :init-server
  (fn [db _]
    (re-frame/dispatch [:server/connect-to-server])
    db))
