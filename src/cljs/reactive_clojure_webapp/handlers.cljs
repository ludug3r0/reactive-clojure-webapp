(ns reactive-clojure-webapp.handlers
    (:require [re-frame.core :as re-frame]
              [reactive-clojure-webapp.db :as db]))

(re-frame/register-handler
 :initialize-db
 (fn  [_ _]
   db/default-db))
