(ns reactive-clojure-webapp.server
  (:require-macros
    [cljs.core.async.macros :as asyncm :refer (go go-loop)])
  (:require [re-frame.core :as re-frame]
            [cljs.core.async :as async :refer (<! >! put! chan)]
            [taoensso.sente :as sente :refer [cb-success?]]
            [taoensso.encore :as encore :refer [tracef infof debugf warnf]]))

(defn logged-into-server? [state]
  (and (:open? state)
       (:requested-reconnect? state)
       (:uid state)))

(re-frame/register-handler
  :server/connect-to-server
  (fn [db _]
    (sente/ajax-call
      "/csrf-token"
      {:method :get}
      (fn [ajax-resp]
        (if (= (:?status ajax-resp) 200)
          (let [{:keys [ch-recv state] :as server} (sente/make-channel-socket! "/chsk")]
            (re-frame/dispatch [:server/connected-to-server server])
            (go-loop [pushed-message (<! ch-recv)]
                     (let [server-v (:event pushed-message)]
                       (if (= :chsk/recv (first server-v))
                         (re-frame/dispatch (second server-v)))
                       (recur (<! ch-recv))))
            (add-watch state :server-state-watcher
                       (fn [k r o n]
                         (if (logged-into-server? n)
                           (re-frame/dispatch [:server/logged-into-server]))
                         ))
            ))))
    db))

(re-frame/register-handler
  :server/connected-to-server
  (fn [db [_ server]]
    (re-frame/dispatch [:server/log-into-server])
    (assoc-in db [:server] server)))

(re-frame/register-handler
  :server/log-into-server
  (fn [db _]
    (sente/ajax-call
      "/login"
      {:method :post
       :params {:user-id    (get-in db [:user])
                :csrf-token (-> db
                                :server
                                :state
                                deref
                                :csrf-token)}}
      (fn [ajax-resp]
        (let [login-successful? (= (:?status ajax-resp) 200)]
          (if-not login-successful?
            (warnf "Login failed")
            (do
              (sente/chsk-reconnect! (get-in db [:server :chsk])))))))
    db))

(re-frame/register-handler
  :server/send-event-to-server
  (fn [db [_ server-v]]
    ((get-in db [:server :send-fn]) server-v
      1000
      (fn [edn-reply]
        (when-not (cb-success? edn-reply)
          (warnf (pr-str edn-reply " - " server-v))
          (re-frame/dispatch [:server/send-event-to-server server-v]))))
    db))

(defn dispatch [v]
  (re-frame/dispatch [:server/send-event-to-server v]))

(re-frame/register-handler
  :server/logged-into-server
  (fn [db _]
    (dispatch [:chat/load])
    db))


