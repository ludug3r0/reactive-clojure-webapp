(ns reactive-clojure-webapp.views
  (:require [re-frame.core :as re-frame]
            [re-com.core :as re-com]
            [goog.string :as gstring]
            [cljs.core :as c]
            [reagent.core :as r]
            [taoensso.encore :as encore :refer [tracef infof debugf warnf]]))

(defn user-line [user]
  ^{:key user} [:li user])

(defn user-list-panel []
  (let [users (re-frame/subscribe [:connected-users])]
    (fn []
      [:ul {:style {:list-style-type "none"}}
       (map user-line @users)])))

(defn message-line [{:keys [timestamp nickname text] :as message}]
  ^{:key message} [re-com/label :label (gstring/format "[%s] <%s> %s" timestamp nickname text)])

(defn message-panel []
  (let [messages (re-frame/subscribe [:messages])]
    (fn []
      [re-com/v-box
       :children (map message-line (take-last 5 @messages))])))

(defn chat-panel []
  (let [input-message (re-frame/subscribe [:input-message])]
    (fn []
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
                   :model input-message
                   :width "100%"
                   :on-change #(when (not-empty %)
                                (re-frame/dispatch [:set-input-message %])
                                (re-frame/dispatch [:send-message])
                                (re-frame/dispatch [:set-input-message ""]))]]])))

(defn loading []
  [re-com/v-box
   :children [[re-com/gap :size "2em"]
              [re-com/label :label "Loading..."]]])

(defn main-panel []
  (let [chat-loaded? (re-frame/subscribe [:chat-loaded?])]
    (fn []
      [re-com/h-box
       :justify :center
       :children [(if @chat-loaded? [chat-panel] [loading])]])))

