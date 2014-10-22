(ns flense-demo.app
  (:require [cljs.core.async :as async]
            [flense.actions :refer [actions]]
            [flense.actions.history :as hist]
            flense.actions.clipboard
            flense.actions.clojure
            flense.actions.movement
            flense.actions.paredit
            [flense.editor :refer [editor-view]]
            [flense.model :as model]
            [flense-demo.keymap :refer [keymap]]
            [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [phalanges.core :as phalanges]))

(defonce edit-chan (async/chan))

(defonce app-state
  (atom {:path [0]
         :tree {:children [(model/form->tree
                             '(defn greet [name] (str "Hello, " name "!")))]}}))

(defn bound-action [ev]
  (-> ev phalanges/key-set keymap (@actions)))

(defn handle-key [ev]
  (when-let [action (bound-action ev)]
    (.preventDefault ev)
    (async/put! edit-chan action)))

(defn fully-selected? [input]
  (and (= (.-selectionStart input) 0)
       (= (.-selectionEnd input) (count (.-value input)))))

(defn propagate-keypress? [ev form]
  (when-let [action (bound-action ev)]
    (if (model/stringlike? form)
      ;; prevent all keybinds except those that end editing
      (#{:move/up :paredit/insert-outside} (:name action))
      ;; prevent delete keybind unless text fully selected
      (or (not= (:name action) :flense/remove)
          (fully-selected? (.-target ev))))))

(defn init []
  (hist/push-state! @app-state)
  (om/root editor-view app-state
    {:target (.getElementById js/document "editor")
     :opts {:edit-chan edit-chan
            :propagate-keypress? propagate-keypress?}
     :tx-listen (fn [{:keys [new-state tag] :or {tag #{}}}]
                  (when-not (tag :history) (hist/push-state! new-state)))})
  (.addEventListener js/window "keydown" handle-key))

(init)
