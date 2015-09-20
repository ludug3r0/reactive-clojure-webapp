(ns reactive-clojure-webapp.subs
    (:require-macros [reagent.ratom :refer [reaction]])
    (:require [re-frame.core :as re-frame]))

(re-frame/register-sub
 :connected-users
 (fn [db]
   (reaction (get-in @db [:chat :connected-users]))))

(re-frame/register-sub
  :messages
  (fn [db]
    (reaction (get-in @db [:chat :messages]))))

(re-frame/register-sub
  :input-message
  (fn [db]
    (reaction (get-in @db [:chat :input-message]))))
