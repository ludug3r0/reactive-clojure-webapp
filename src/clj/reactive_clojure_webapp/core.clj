(ns reactive-clojure-webapp.core
  (:require
    [compojure.core :refer :all]
    [compojure.route :as route]

    [taoensso.sente :as sente]
    [taoensso.timbre :as timbre :refer [infof debugf]]

    [org.httpkit.server :refer [run-server]]
    [taoensso.sente.server-adapters.http-kit :refer (sente-web-server-adapter)]

    [ring.middleware.keyword-params]
    [ring.middleware.params]
    [ring.middleware.reload :as reload]
    [ring.middleware.defaults]
    [ring.middleware.anti-forgery :refer [*anti-forgery-token*]]

    [reactive-clojure-webapp.handlers :as handlers]))

;;TODO #5: improve how we bind up these channels and handlers
(let [{:keys [ch-recv ajax-post-fn ajax-get-or-ws-handshake-fn]}
      (sente/make-channel-socket! sente-web-server-adapter {})]
  (def ring-ajax-post ajax-post-fn)
  (def ring-ajax-get-or-ws-handshake ajax-get-or-ws-handshake-fn)
  (sente/start-chsk-router! ch-recv handlers/event-msg-handler))


;;TODO #4: proper login logic
(defn login! [ring-request]
  (let [{:keys [session params]} ring-request
        {:keys [user-id]} params]
    ;; (debugf "Login request: %s" params)
    {:status 200 :session (assoc session :uid user-id)}))

(defn logout! [ring-request]
  (let [{:keys [session params]} ring-request]
    ;; (debugf "Logout request: %s" params)
    {:status 200 :session (dissoc session :uid)}))

(defroutes my-app-routes
           (GET "/" [] (-> (ring.util.response/resource-response "index.html" {:root "public"})
                           (ring.util.response/content-type "text/html")))
           (GET "/chsk" req (ring-ajax-get-or-ws-handshake req))
           (POST "/chsk" req (ring-ajax-post req))

           (GET "/csrf-token" req (-> (ring.util.response/response (pr-str {:csrf-token *anti-forgery-token*}))
                                      (ring.util.response/header "csrf-token" *anti-forgery-token*)
                                      (ring.util.response/content-type "application/edn")))
           (POST "/logout" req (logout! req))
           (POST "/login" req (login! req))

           (route/resources "/")
           (route/not-found "Page not found..."))

(def server-app
  (let [ring-defaults-config
        (-> ring.middleware.defaults/site-defaults
            (assoc-in [:security :anti-forgery] {:read-token (fn [req] (-> req :params :csrf-token))}))]
    (-> (reload/wrap-reload #'my-app-routes)
        (ring.middleware.defaults/wrap-defaults ring-defaults-config))))

;; entry point
;;TODO #6: add settings module
(defn -main [& args]
  (run-server server-app {:port 8080}))