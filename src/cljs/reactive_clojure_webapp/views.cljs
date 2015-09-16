(ns reactive-clojure-webapp.views
  (:require [re-frame.core :as re-frame]
            [re-com.core :as re-com]
            [goog.string :as gstring]
            [cljs.core :as c]))


(defn user-line [user]
  [:li (:nickname user)])

(defn user-list-panel []
  (let [users [{:nickname "Rafael"} {:nickname "Aquiles"}]]
    [:ul {:style {:list-style-type "none"}}
     (map user-line users)]))

(defn message-line [{:keys [timestamp nickname text]}]
  [:p (gstring/format "[%s] <%s> %s" timestamp nickname text)])

(defn message-panel []
  (let [messages [{:nickname "Rafael" :text "E aê, Aquiles?" :timestamp 1442362189}
                  {:nickname "Aquiles" :text "Opa!!" :timestamp 1442362192}]]
    [re-com/v-box
     :children (map message-line messages)]))

(defn chat-panel []
  [re-com/v-box
   :height "100%"
   :children [[re-com/gap :size "2em"]
              [re-com/h-box
               :height "100%"
               :width "700px"
               :children [[message-panel]
                          [re-com/gap :size "1"]
                          [user-list-panel]]]
              [re-com/input-text
               :model ""
               :width "100%"
               :on-change #()]]])

(defn main-panel []
  [re-com/h-box
   :justify :center
   :children [[chat-panel]]])
