(ns reactive-clojure-webapp.subs
    (:require-macros [reagent.ratom :refer [reaction]])
    (:require [re-frame.core :as re-frame]))

(re-frame/register-sub
 :connected-users
 (fn [db]
   (reaction (:connected-users @db))))

(re-frame/register-sub
  :messages
  (fn [db]
    (reaction (:messages @db))))

(re-frame/register-sub
  :input-message
  (fn [db]
    (reaction (:input-message @db))))
