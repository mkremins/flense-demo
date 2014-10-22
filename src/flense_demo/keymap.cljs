(ns flense-demo.keymap)

(def keymap
  {#{:backspace}                         :paredit/remove
   #{:down}                              :move/down
   #{:enter}                             :paredit/insert-outside
   #{:left}                              :move/left
   #{:open-square-bracket}               :paredit/wrap-square
   #{:right}                             :move/right
   #{:space}                             :paredit/insert-right
   #{:tab}                               :clojure/expand-template
   #{:up}                                :move/up
   #{:ctrl :open-square-bracket}         :paredit/make-square
   #{:ctrl :shift :nine}                 :paredit/make-round
   #{:ctrl :shift :open-square-bracket}  :paredit/make-curly
   #{:meta :c}                           :clipboard/copy
   #{:meta :v}                           :clipboard/paste
   #{:meta :x}                           :clipboard/cut
   #{:meta :y}                           :history/redo
   #{:meta :z}                           :history/undo
   #{:meta :ctrl :a}                     :paredit/join-left
   #{:meta :ctrl :k}                     :paredit/swap-left
   #{:meta :ctrl :l}                     :paredit/swap-right
   #{:meta :ctrl :left}                  :paredit/shrink-left
   #{:meta :ctrl :nine}                  :paredit/split-left
   #{:meta :ctrl :right}                 :paredit/shrink-right
   #{:meta :ctrl :s}                     :paredit/join-right
   #{:meta :ctrl :up}                    :paredit/splice
   #{:meta :ctrl :zero}                  :paredit/split-right
   #{:meta :shift :d}                    :clojure/jump-to-definition
   #{:meta :shift :k}                    :move/prev-placeholder
   #{:meta :shift :l}                    :move/next-placeholder
   #{:meta :shift :left}                 :paredit/grow-left
   #{:meta :shift :right}                :paredit/grow-right
   #{:meta :shift :up}                   :paredit/raise
   #{:shift :left}                       :move/prev
   #{:shift :nine}                       :paredit/wrap-round
   #{:shift :open-square-bracket}        :paredit/wrap-curly
   #{:shift :single-quote}               :paredit/wrap-quote
   #{:shift :right}                      :move/next
   #{:shift :space}                      :paredit/insert-left
   #{:shift :three}                      :clojure/toggle-dispatch})
