(ns flense-demo.app
  (:require [cljs.core.async :as async]
            [flense.actions :refer [default-actions]]
            [flense.actions.history :as hist]
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
  (-> ev phalanges/key-set keymap default-actions))

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
      (contains? (:tags (meta action)) :end-text-editing)
      ;; prevent delete keybind unless text fully selected
      (or (not (contains? (:tags (meta action)) :remove))
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
