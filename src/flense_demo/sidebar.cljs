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

(def special-keynames
  {:alt "⌥"
   :backspace "⌫"
   :close-square-bracket "]"
   :ctrl "⌃"
   :down "↓"
   :left "←"
   :meta "⌘"
   :open-square-bracket "["
   :right "→"
   :single-quote "'"
   :shift "⇧"
   :up "↑"})

(def special-shift-combos
  {#{:shift :close-square-bracket} "}"
   #{:shift :nine} "("
   #{:shift :open-square-bracket} "{"
   #{:shift :single-quote} "\""
   #{:shift :zero} ")"})

(defn key->keyname [key]
  (or (special-keynames key)
      (clojure.string/capitalize (name key))))

(defn keyset->keyname [keyset]
  (or (special-shift-combos keyset)
      (->> keyset
           (sort-by #(case % :meta 0 :ctrl 1 :shift 2 :alt 3 4))
           (map key->keyname)
           clojure.string/join)))

(defn movement-keybinds [loc]
  [[(cond
      (and (m/stringlike? loc) (not (m/editing? loc)))
        "Begin editing text inside this form"
      (completions/has-completions? loc)
        "Select the next completion"
      :else
        "Select this form's first child")
    #{:down}]
   [(cond
      (m/editing? loc)
        "Finish editing text inside this form"
      (completions/has-completions? loc)
        "Select the previous completion"
      :else
        "Select this form's parent")
    #{:up}]
   ["Select this form's previous sibling, or wrap around" #{:left}]
   ["Select this form's next sibling, or wrap around" #{:right}]])

(defn structural-keybinds [loc]
  [[(cond
      (m/editing? loc)
        "Delete the selected text"
      (or (not (m/atom? loc)) (m/placeholder? loc))
        "Delete this form"
      :else
        "Delete the last character of this form")
    #{:backspace}]
   ["Delete this form" #{:shift :backspace}]
   ["Insert placeholder to the right" #{:space}]
   ["Insert placeholder to the left" #{:shift :space}]
   ["Wrap this form in a sequence" #{:shift :nine}]
   ["Wrap this form in a vector" #{:open-square-bracket}]
   ["Wrap this form in a map" #{:shift :open-square-bracket}]
   ["Wrap this form in a string" #{:shift :single-quote}]])

(defn semantic-keybinds [loc]
  [["Use selected completion" #{:tab}]])

(defn history-keybinds [loc]
  [["Undo most recently performed action" #{:meta :z}]
   ["Redo most recently undone action" #{:meta :y}]])

(defn clipboard-keybinds [loc]
  [["Copy this form" #{:meta :c}]
   ["Cut this form" #{:meta :x}]
   ["Paste most recently copied form" #{:meta :v}]])

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
              (for [[desc keyset] (keybinds document)
                    :let [enabled? (try (boolean ((keymap keyset) document))
                                     (catch :default _ false))]]
                (dom/tr #js {:className (if enabled? "enabled" "disabled")}
                  (dom/td (keyset->keyname keyset))
                  (dom/td desc))))))))))
