(ns poker.deck)

(def suits #{:hearts :spades :clubs :diamonds})

(def cards
  (zipmap [:two :three :four :five
           :six :seven :eight :nine :ten :jack :queen :king :ace]
          (iterate inc 2)))

(defn cut [deck]
  (let [r (+ 20 (rand-int 10))
        front (take r deck)
        back (take-last (- 52 r) deck)]
    (concat back front)))

(defn new-deck
  "Returns one deck containing `n` 52-card decks, shuffled into random
  order."
  []
  (->> (for [suit suits, card cards]
         {:suit suit :card card :showing? false})
       shuffle
       cut
       shuffle))



(defn foo
  "I don't do a whole lot."
  []
  (let [deck (new-deck)
        count (count deck)
        ]
    ;;    (println deck)
    (print count)))
