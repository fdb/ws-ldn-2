(ns ^:figwheel-always ws-ldn-2.day1.ui.core
  (:require-macros
   [reagent.ratom :refer [reaction]]
   [cljs.core.async.macros :refer [go go-loop]]
   [cljs-log.core :refer [debug info warn]])
  (:require
   [ws-ldn-2.day1.ui.state :as state]
   [ws-ldn-2.day1.ui.router :as router]
   [ws-ldn-2.day1.ui.nav :as nav]
   [ws-ldn-2.day1.ui.views :as views]
   [reagent.core :as r]
   [cljs.core.async :as a]
   [thi.ng.validate.core :as v]))

(defn aget-in
  "Looks up dotted path (as str) in JS object."
  [obj path]
  (loop [obj obj, path (clojure.string/split path ".")]
    (if path
      (recur (aget obj (first path)) (next path))
      obj)))

(def routes
  [{:id        :home
    :match     ["home"]
    :component #'views/home
    :comp2     "ws_ldn_2.day1.ui.views.home"
    :label     "Home"}
   {:id        :query-edit
    :match     ["query"]
    :component #'views/query-editor
    :label     "Query editor"}])

(defn view-wrapper
  [route]
  (let [route @route]
    (debug (aget-in js/window (:comp2 route)))
    [:div
     [nav/nav-bar routes route]
     [(:component route) route]]))

(defn main-panel
  "Application main component."
  []
  (let [route (reaction (:curr-route @state/app-state))]
    (fn []
      (if @route
        [view-wrapper route]
        [:div "Initializing..."]))))

(defn start-router
  []
  (router/start!
   routes
   nil
   (router/route-for-id routes :home)
   state/nav-change
   (constantly nil)))

(defn main
  "Application main entry point, kicks off React component lifecycle."
  []
  (start-router)
  (r/render-component [main-panel] (.-body js/document)))

(defn on-js-reload
  [] (debug :reloaded))


(main)