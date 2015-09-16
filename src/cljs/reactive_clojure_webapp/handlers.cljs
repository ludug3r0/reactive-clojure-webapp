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
    (assoc-in db [:input-message] text)))

(re-frame/register-handler
  :send-message
  (fn [db _]
    (server/dispatch [:chat/broadcast (:input-message db)])
    db))

(re-frame/register-handler
  :chat/message
  (fn [db [_ {:keys [nickname text timestamp]}]]
    (update-in db [:messages] conj {:timestamp timestamp :nickname nickname :text text})))

(re-frame/register-handler
  :init-server
  (fn [db _]
    (re-frame/dispatch [:connect-to-server (:nickname db)])
    db))
