(ns reactive-clojure-webapp.core
    (:require [reagent.core :as reagent]
              [re-frame.core :as re-frame]
              [reactive-clojure-webapp.handlers]
              [reactive-clojure-webapp.subs]
              [reactive-clojure-webapp.views :as views]))

(defn mount-root []
  (reagent/render [views/main-panel]
                  (.getElementById js/document "app")))

(defn ^:export init [] 
  (re-frame/dispatch-sync [:initialize-db])
  (re-frame/dispatch [:init-server])
  (mount-root))
