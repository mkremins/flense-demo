(ns flense-demo.sidebar
  (:require [flense.actions.clipboard :as clip]
            [flense.actions.clojure :as clojure]
            [flense.actions.history :as hist]
            [flense.actions.text :as text]
            [flense.model :as m]
            [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [xyzzy.core :as z]))

(defn movement-keybinds [loc]
  [["Down" (if (m/stringlike-loc? loc)
             "Begin editing text inside this form"
             "Select this form's first child")
           (text/down loc)]
   ["Up" (if (and (m/stringlike-loc? loc) (:editing? (z/node loc)))
           "Finish editing text inside this form"
           "Select this form's parent")
         (text/up loc)]
   ["Left" "Select the form to the left, or wrap around" (z/left-or-wrap loc)]
   ["Right" "Select the form to the right, or wrap around" (z/right-or-wrap loc)]])

(defn structural-keybinds [loc]
  [["Backspace" (if (or (not (m/atom-loc? loc)) (m/placeholder-loc? loc))
                  "Delete this form"
                  "Delete the last character of this form")
                true]
   ["Shift+Backspace" "Delete this form" true]
   ["Space" "Insert placeholder to the right" true]
   ["Shift+Space" "Insert placeholder to the left" true]
   ["Shift+9" "Wrap this form in a sequence" true]
   ["Open-bracket" "Wrap this form in a vector" true]
   ["Shift+Open-bracket" "Wrap this form in a map" true]
   ["Shift+Single-quote" "Wrap this form in a string" (m/atom-loc? loc)]])

(defn semantic-keybinds [loc]
  [["Tab" "Expand template" (clojure/expand-template loc)]])

(defn history-keybinds [loc]
  [["Cmd+Z" "Undo most recently performed action" (hist/undo loc)]
   ["Cmd+Y" "Redo most recently undone action" (hist/redo loc)]])

(defn clipboard-keybinds [loc]
  [["Cmd+C" "Copy this form" true]
   ["Cmd+X" "Cut this form" true]
   ["Cmd+V" "Paste most recently copied form" (clip/paste loc)]])

(def available-keybinds
  [["Movement" movement-keybinds]
   ["Structural" structural-keybinds]
   ["Semantic" semantic-keybinds]
   ["History" history-keybinds]
   ["Clipboard" clipboard-keybinds]])

(defn sidebar [document owner]
  (reify om/IRender
    (render [_]
      (apply dom/div nil
        (for [[title keybinds] available-keybinds]
          (dom/section nil
            (dom/h3 nil title)
            (dom/table nil
              (apply dom/tbody nil
                (for [[key desc enabled?] (keybinds document)]
                  (dom/tr #js {:className (if enabled? "enabled" "disabled")}
                    (dom/td nil key)
                    (dom/td nil desc)))))))))))
