(ns flense-demo.sidebar
  (:require [flense.actions.completions :as completions]
            [flense.model :as m]
            [flense-demo.keymap :refer [keymap]]
            [om-tools.core :refer-macros [defcomponent]]
            [om-tools.dom :as dom]))

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
        "Select next completion"
      :else
        "Select this form's first child")
    #{:down}]
   [(cond
      (m/editing? loc)
        "Finish editing text inside this form"
      (completions/has-completions? loc)
        "Select previous completion"
      :else
        "Select this form's parent")
    #{:up}]
   [(if (m/editing? loc)
      "Move cursor left"
      "Select this form's previous sibling")
    #{:left}]
   [(if (m/editing? loc)
      "Move cursor right"
      "Select this form's next sibling")
    #{:right}]])

(defn structural-keybinds [loc]
  [[(cond
      (m/editing? loc)
        "Delete selected text"
      (or (not (m/atom? loc)) (m/placeholder? loc))
        "Delete this form"
      :else
        "Delete last character of this form")
    #{:backspace}]
   ["Delete this form" #{:shift :backspace}]
   ["Insert placeholder as next sibling" #{:space}]
   ["Insert placeholder as previous sibling" #{:shift :space}]])

(defn wrapper-keybinds [loc]
  [["Wrap this form in a list" #{:shift :nine}]
   ["Wrap this form in a vector" #{:open-square-bracket}]
   ["Wrap this form in a map" #{:shift :open-square-bracket}]
   ["Wrap this form in a string" #{:shift :single-quote}]])

(defn semantic-keybinds [loc]
  [["Insert selected completion" #{:tab}]])

(defn history-keybinds [loc]
  [["Undo last action" #{:meta :z}]
   ["Redo last undo" #{:meta :y}]])

(defn clipboard-keybinds [loc]
  [["Copy this form" #{:meta :c}]
   ["Cut this form" #{:meta :x}]
   ["Paste copied form" #{:meta :v}]])

(def available-keybinds
  [movement-keybinds
   structural-keybinds
   wrapper-keybinds
   semantic-keybinds
   history-keybinds
   clipboard-keybinds])

(defcomponent sidebar [document owner]
  (render [_]
    (dom/table
      (for [keybinds available-keybinds]
        (dom/tbody
          (for [[desc keyset] (keybinds document)
                :let [enabled? (try (boolean ((keymap keyset) document))
                                 (catch :default _ false))]]
            (dom/tr {:class (if enabled? "enabled" "disabled")}
              (dom/td (keyset->keyname keyset))
              (dom/td desc))))))))
