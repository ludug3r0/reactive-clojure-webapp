(ns reactive-clojure-webapp.subs
    (:require-macros [reagent.ratom :refer [reaction]])
    (:require [re-frame.core :as re-frame]
              [taoensso.encore :as encore :refer [tracef infof debugf warnf]]))

(re-frame/register-sub
 :connected-users
 (fn [db]
   (reaction (get-in @db [:chat :users]))))

(re-frame/register-sub
  :messages
  (fn [db]
    (reaction (get-in @db [:chat :messages]))))

(re-frame/register-sub
  :input-message
  (fn [db]
    (reaction (get-in @db [:chat :input-message]))))

(re-frame/register-sub
  :chat-loaded?
  (fn [db]
    (reaction (get-in @db [:chat :loaded?]))))
