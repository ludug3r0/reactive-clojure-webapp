(ns reactive-clojure-webapp.handlers
  (:require [taoensso.timbre :as timbre :refer [warnf]]))

;; state
(defonce db (atom {:chat {:messages []
                          :users    []}}))

;; helpers
(defn authenticated-user [event-msg]
  (get-in event-msg [:ring-req :session :uid]))


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
      (doseq [uid (-> event-msg :connected-uids deref :any)]
        (send-fn uid [:chat/message message])))))

(defmethod handle-event :chat/load [event-msg]
  (prn "Received")
  (when-let [user (authenticated-user event-msg)]
    ((:send-fn event-msg) user [:chat/load (:chat @db)])))

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