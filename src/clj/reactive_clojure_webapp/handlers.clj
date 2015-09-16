(ns reactive-clojure-webapp.handlers
  )

;; event handlers

(defmulti handle-event :id)

(defmethod handle-event :chat/broadcast [event-msg]
  ;; user must be logged in
  (when (get-in event-msg [:ring-req :session :uid])
    (doseq [uid (-> event-msg :connected-uids deref :any)]
      ((:send-fn event-msg) uid [:chat/message {:timestamp (quot (System/currentTimeMillis) 1000)
                                                :nickname uid
                                                :text (:?data event-msg)}]))))

(defmethod handle-event :default [event-msg]
  (let [event-id (:id event-msg)
        server-event-ids #{:chsk/ws-ping :chsk/bad-package :chsk/bad-event :chsk/uidport-open :chsk/uidport-close}]
    (when-not (contains? server-event-ids event-id)
      (prn "UNREGISTERED EVENT HANDLER FOR: " event-msg))))

;; --------------------------



(defn event-msg-handler [{:keys [:?reply-fn] :as event-msg}]
  (handle-event event-msg)
  (when-let [reply-fn (:?reply-fn event-msg)]
    (reply-fn :ack)))