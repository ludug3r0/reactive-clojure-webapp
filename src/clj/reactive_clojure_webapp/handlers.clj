(ns reactive-clojure-webapp.handlers
  (:require [taoensso.timbre :as timbre :refer [warnf]]))

;; state
(defonce db (atom {:chat {:messages []}}))

;; helpers
(defn authenticated-user [event-msg]
  (get-in event-msg [:ring-req :session :uid]))

(defn connected-users [event-msg]
  (-> event-msg :connected-uids deref :any))


;; event handlers
(defmulti handle-event :id)

(defmethod handle-event :chat/broadcast [event-msg]
  ;; user must be logged in
  (when-let [user (authenticated-user event-msg)]
    (let [send-fn (:send-fn event-msg)
          message {:timestamp (quot (System/currentTimeMillis) 1000)
                   :nickname  user
                   :text      (:?data event-msg)}]
      (swap! db update-in [:chat :messages] conj message)
      (doseq [uid (connected-users event-msg)]
        (send-fn uid [:chat/message message])))))

(defmethod handle-event :chat/load [event-msg]
  (when-let [user (authenticated-user event-msg)]
    (let [chat-data {:messages (get-in @db [:chat :messages])
                     :users (connected-users event-msg)}]
      ((:send-fn event-msg) user [:chat/load chat-data]))))

(defn broadcast-user-list-change [event-msg]
  (let [send-fn (:send-fn event-msg)
        users (connected-users event-msg)]
    (doseq [uid (connected-users event-msg)]
      (send-fn uid [:chat/user-list-changed users]))))

(defmethod handle-event :chsk/uidport-open [event-msg]
  (broadcast-user-list-change event-msg))
(defmethod handle-event :chsk/uidport-close [event-msg]
  (broadcast-user-list-change event-msg))

(defmethod handle-event :default [event-msg]
  (let [event-id (:id event-msg)
        server-event-ids #{:chsk/ws-ping :chsk/bad-package :chsk/bad-event :chsk/uidport-open :chsk/uidport-close}]
    (when-not (contains? server-event-ids event-id)
      (warnf "UNREGISTERED EVENT HANDLER FOR: " event-msg))))

;; --------------------------



(defn event-msg-handler [{:keys [:?reply-fn] :as event-msg}]
  (handle-event event-msg)
  (when-let [reply-fn (:?reply-fn event-msg)]
    (reply-fn :ack)))