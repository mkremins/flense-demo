(ns flense-demo.app
  (:require [flense.editor :as flense]
            [flense.model :as model]
            [flense-demo.keymap :refer [keymap]]
            [flense-demo.sidebar :refer [sidebar]]
            [om.core :as om]
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

(def legal-atom-char?
  (let [uppers (map (comp js/String.fromCharCode (partial + 65)) (range 26))
        lowers (map (comp js/String.fromCharCode (partial + 97)) (range 26))
        digits (map str (range 10))
        puncts [\. \! \? \$ \% \& \+ \- \* \/ \= \< \> \_ \: \' \\ \|]]
    (set (concat uppers lowers digits puncts))))

(defn- handle-keypress [ev]
  (let [c (phalanges/key-char ev)]
    (when (or (legal-atom-char? c) (model/editing? @app-state))
      (.preventDefault ev)
      (perform! (partial flense.actions.text/insert c)))))

(defn init []
  (enable-console-print!)
  (om/root flense/editor app-state
    {:target (js/document.getElementById "editor")})
  (om/root sidebar app-state
    {:target (js/document.getElementById "sidebar")})
  (js/window.addEventListener "keydown" handle-keydown)
  (js/window.addEventListener "keypress" handle-keypress))

(init)
