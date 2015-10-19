(ns flense-demo.sidebar
  (:require [flense.actions.clipboard :as clip]
            [flense.actions.clojure :as clojure]
            [flense.actions.completions :as completions]
            [flense.actions.history :as hist]
            [flense.actions.text :as text]
            [flense.model :as m]
            [flense-demo.keymap :refer [keymap]]
            [om.core :as om]
            [om-tools.core :refer-macros [defcomponent]]
            [om-tools.dom :as dom]
            [xyzzy.core :as z]))

(defn movement-keybinds [loc]
  [["Down" (cond
             (m/stringlike? loc)
               "Begin editing text inside this form"
             (completions/has-completions? loc)
               "Select the next completion"
             :else
               "Select this form's first child")
           #{:down}]
   ["Up" (cond
           (and (m/stringlike? loc) (:editing? (z/node loc)))
             "Finish editing text inside this form"
           (completions/has-completions? loc)
             "Select the previous completion"
           :else
             "Select this form's parent")
         #{:up}]
   ["Left" "Select the form to the left, or wrap around" #{:left}]
   ["Right" "Select the form to the right, or wrap around" #{:right}]])

(defn structural-keybinds [loc]
  [["Backspace" (cond
                  (and (m/stringlike? loc) (:editing? (z/node loc)))
                    "Delete the selected text"
                  (or (not (m/atom? loc)) (m/placeholder? loc))
                    "Delete this form"
                  :else
                    "Delete the last character of this form")
                #{:backspace}]
   ["Shift+Backspace" "Delete this form" #{:shift :backspace}]
   ["Space" "Insert placeholder to the right" #{:space}]
   ["Shift+Space" "Insert placeholder to the left" #{:shift :space}]
   ["(" "Wrap this form in a sequence" #{:shift :nine}]
   ["[" "Wrap this form in a vector" #{:open-square-bracket}]
   ["{" "Wrap this form in a map" #{:shift :open-square-bracket}]
   ["\"" "Wrap this form in a string" #{:shift :single-quote}]])

(defn semantic-keybinds [loc]
  [["Tab" "Use selected completion" #{:tab}]])

(defn history-keybinds [loc]
  [["Cmd+Z" "Undo most recently performed action" #{:meta :z}]
   ["Cmd+Y" "Redo most recently undone action" #{:meta :y}]])

(defn clipboard-keybinds [loc]
  [["Cmd+C" "Copy this form" #{:meta :c}]
   ["Cmd+X" "Cut this form" #{:meta :x}]
   ["Cmd+V" "Paste most recently copied form" #{:meta :v}]])

(def available-keybinds
  [["Movement" movement-keybinds]
   ["Structural" structural-keybinds]
   ["Semantic" semantic-keybinds]
   ["History" history-keybinds]
   ["Clipboard" clipboard-keybinds]])

(defcomponent sidebar [document owner]
  (render [_]
    (dom/div
      (for [[title keybinds] available-keybinds]
        (dom/section
          (dom/h3 title)
          (dom/table
            (dom/tbody
              (for [[keyname desc keyset] (keybinds document)
                    :let [enabled? ((keymap keyset) document)]]
                (dom/tr #js {:className (if enabled? "enabled" "disabled")}
                  (dom/td keyname)
                  (dom/td desc))))))))))
