(ns reactive-clojure-webapp.views
    (:require [re-frame.core :as re-frame]
              [re-com.core :as re-com]
              [goog.string :as gstring]
              [cljs.core :as c]))

(defn user-line [user]
  [:li (:nickname user)])

(defn user-list-panel []
  (let [users [{:nickname "Rafael"} {:nickname "Aquiles"}]]
    [:ul
     (map user-line users)]))

(defn message-line [{:keys [timestamp nickname text]}]
  [:p (gstring/format "[%s] <%s> %s" timestamp nickname text)])

(defn message-panel []
  (let [messages [{:nickname "Rafael" :text "E aê, Aquiles?" :timestamp 1442362189}
                  {:nickname "Aquiles" :text "Opa!!" :timestamp 1442362192}]]
    [:div
     (map message-line messages)]))

(defn chat-panel []
  (fn []
    [re-com/h-box
     :height "100%"
     :children [[message-panel] [user-list-panel]]]))
