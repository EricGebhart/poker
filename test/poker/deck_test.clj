(ns poker.deck-test
  (:require [poker.deck :refer :all]
            [midje.sweet :as midje]))

(fact "the deck should have 52 distinct cards"
      (count (distinct (new))) => 52)

(fact "decks should be shuffled and cut randomly"
      (= (new) (new)) => false)
