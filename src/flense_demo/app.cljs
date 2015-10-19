(ns flense-demo.app
  (:require [flense.actions.text :as text]
            [flense.editor :as flense]
            [flense.model :as model]
            [flense-demo.keymap :refer [keymap]]
            [flense-demo.sidebar :refer [sidebar]]
            [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [phalanges.core :as phalanges]))

(defonce app-state
  (atom (model/forms->document
          '[(defn greet [name] (str "Hello, " name "!"))])))

(defn perform! [action]
  (swap! app-state (flense/perform action)))

(defn- handle-keydown [ev]
  (let [keyset (phalanges/key-set ev)]
    (when-let [action (keymap keyset)]
      (.preventDefault ev)
      (perform! action))))

(def legal-char?
  (let [uppers (map (comp js/String.fromCharCode (partial + 65)) (range 26))
        lowers (map (comp js/String.fromCharCode (partial + 97)) (range 26))
        digits (map str (range 10))
        puncts [\. \! \? \$ \% \& \+ \- \* \/ \= \< \> \_ \: \' \\ \|]]
    (set (concat uppers lowers digits puncts))))

(defn- handle-keypress [ev]
  (let [c (phalanges/key-char ev)]
    (when (legal-char? c)
      (.preventDefault ev)
      (perform! (partial text/insert c)))))

(defn init []
  (om/root flense/editor app-state
    {:target (.getElementById js/document "editor")})
  ;(om/root sidebar app-state
  ;  {:target (.getElementById js/document "sidebar")})
  (.addEventListener js/window "keydown" handle-keydown)
  (.addEventListener js/window "keypress" handle-keypress))

(init)
