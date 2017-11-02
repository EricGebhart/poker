(ns poker.core-test
  (:require [clojure.test :refer :all]
            [midje.sweet :refer :all]
            [poker.core :refer :all]))

(fact "the deck should have 52 distinct cards"
      (count (distinct (new-deck))) => 52)

(fact "decks should be shuffled and cut randomly"
      (= (new-deck) (new-deck)) => false)

(fact "new game"
      (new-game) => nil
      )


(fact "new game with players"
      (-> (new-game)
          create-players
          :players
          )  => nil)

(fact "new game with players"
      (-> (new-game)
          create-players
          )  => nil)

(def mygame (-> (new-game)
                create-players
                init-deal
                ))

(def first-player (first (keys (:players mygame))))

(def myhand (:hand (get (:players mygame) first-player)))

(def aplayer (first (:players mygame)))

(def ahand [{:suit :diamonds, :card [:eight 7], :showing? false}
            {:suit :diamonds, :card [:ace 13], :showing? false}
            {:suit :hearts, :card [:two 1], :showing? false}
            {:suit :clubs, :card [:queen 11], :showing? false}
            {:suit :diamonds, :card [:queen 11], :showing? false}] )

(def astraight [{:suit :diamonds, :card [:jack 12], :showing? false}
                {:suit :diamonds, :card [:ace 14], :showing? false}
                {:suit :hearts, :card [:king 13], :showing? false}
                {:suit :clubs, :card [:queen 11], :showing? false}
                {:suit :diamonds, :card [:ten 10], :showing? false}] )

(def straight-ace-low [{:suit :diamonds, :card [:jack 2], :showing? false}
                       {:suit :diamonds, :card [:ace 14], :showing? false}
                       {:suit :hearts, :card [:three 3], :showing? false}
                       {:suit :clubs, :card [:four 4], :showing? false}
                       {:suit :diamonds, :card [:five 5], :showing? false}] )

(def three-straights [{:suit :diamonds, :card [:jack 12], :showing? false}
                      {:suit :diamonds, :card [:ace 14], :showing? false}
                      {:suit :hearts, :card [:king 13], :showing? false}
                      {:suit :clubs, :card [:queen 11], :showing? false}
                      {:suit :clubs, :card [:eight 8], :showing? false}
                      {:suit :clubs, :card [:nine 9], :showing? false}
                      {:suit :diamonds, :card [:ten 10], :showing? false}] )

(def fullhouse [{:suit :diamonds, :card [:jack 12], :showing? false}
                {:suit :clubs,  :card [:jack 12], :showing? false}
                {:suit :hearts, :card [:jack 12], :showing? false}
                {:suit :clubs, :card [:queen 11], :showing? false}
                {:suit :clubs, :card [:queen 11], :showing? false}
                {:suit :clubs, :card [:nine 9], :showing? false}
                {:suit :diamonds, :card [:ten 10], :showing? false}] )

(def fullhouse-q-hi [{:suit :diamonds, :card [:jack 12], :showing? false}
                     {:suit :clubs,  :card [:jack 12], :showing? false}
                     {:suit :hearts, :card [:jack 12], :showing? false}
                     {:suit :clubs, :card [:queen 11], :showing? false}
                     {:suit :clubs, :card [:queen 11], :showing? false}
                     {:suit :clubs, :card [:queen 11], :showing? false}
                     {:suit :diamonds, :card [:ten 10], :showing? false}] )

(def three-of-kind-hand [{:suit :diamonds, :card [:jack 11], :showing? false}
                         {:suit :clubs,  :card [:jack 11], :showing? false}
                         {:suit :hearts, :card [:jack 11], :showing? false}
                         {:suit :clubs, :card [:queen 12], :showing? false}
                         {:suit :diamonds, :card [:ten 10], :showing? false}] )

(def four-of-kind-hand [{:suit :diamonds, :card [:jack 11], :showing? false}
                        {:suit :clubs,  :card [:jack 11], :showing? false}
                        {:suit :hearts, :card [:jack 11], :showing? false}
                        {:suit :clubs, :card [:jack 11], :showing? false}
                        {:suit :diamonds, :card [:ten 10], :showing? false}] )

(def two-pair-hand [{:suit :diamonds, :card [:jack 11], :showing? false}
                    {:suit :clubs,  :card [:jack 11], :showing? false}
                    {:suit :hearts, :card [:queen 12], :showing? false}
                    {:suit :clubs, :card [:queen 12], :showing? false}
                    {:suit :diamonds, :card [:ten 10], :showing? false}])

(def pair-hand [{:suit :diamonds, :card [:jack 11], :showing? false}
                {:suit :clubs,  :card [:jack 11], :showing? false}
                {:suit :hearts, :card [:queen 12], :showing? false}
                {:suit :clubs, :card [:kind 13], :showing? false}
                {:suit :diamonds, :card [:ten 10], :showing? false}] )

(def nothing-hand [{:suit :diamonds, :card [:jack 11], :showing? false}
                   {:suit :clubs,  :card [:eight 8], :showing? false}
                   {:suit :hearts, :card [:queen 12], :showing? false}
                   {:suit :clubs, :card [:king 13], :showing? false}
                   {:suit :diamonds, :card [:ten 10], :showing? false}] )

(def flush-hand [{:suit :diamonds, :card [:jack 11], :showing? false}
                 {:suit :diamonds, :card [:eight 8], :showing? false}
                 {:suit :diamonds, :card [:queen 12], :showing? false}
                 {:suit :diamonds, :card [:king 13], :showing? false}
                 {:suit :diamonds, :card [:ten 10], :showing? false}] )

(def straight-flush-hand [{:suit :diamonds, :card [:jack 11], :showing? false}
                          {:suit :diamonds, :card [:nine 9], :showing? false}
                          {:suit :diamonds, :card [:queen 12], :showing? false}
                          {:suit :diamonds, :card [:king 13], :showing? false}
                          {:suit :diamonds, :card [:ten 10], :showing? false}] )

(fact "straight returns nil if no straight"
      (straight ahand) => nil)

(fact "straight finds a straight"
      (straight astraight) =>  '{:hand [{:card [:jack 12], :showing? false, :suit :diamonds}
                                        {:card [:ace 14], :showing? false, :suit :diamonds}
                                        {:card [:king 13], :showing? false, :suit :hearts}
                                        {:card [:queen 11], :showing? false, :suit :clubs}
                                        {:card [:ten 10], :showing? false, :suit :diamonds}],
                                 :high 14,
                                 :ranks (10 11 12 13 14),
                                 :score :straight})

(fact "straight can find the ace low straight"
      (straight straight-ace-low) => '{:hand [{:card [:jack 2], :showing? false, :suit :diamonds}
                                              {:card [:ace 14], :showing? false, :suit :diamonds}
                                              {:card [:three 3], :showing? false, :suit :hearts}
                                              {:card [:four 4], :showing? false, :suit :clubs}
                                              {:card [:five 5], :showing? false, :suit :diamonds}],
                                       :high 5,
                                       :ranks (14 2 3 4 5),
                                       :score :straight})

(fact "straight finds the best straight"
      (straight three-straights) => '{:hand [{:card [:jack 12], :showing? false, :suit :diamonds}
                                             {:card [:ace 14], :showing? false, :suit :diamonds}
                                             {:card [:king 13], :showing? false, :suit :hearts}
                                             {:card [:queen 11], :showing? false, :suit :clubs}
                                             {:card [:eight 8], :showing? false, :suit :clubs}
                                             {:card [:nine 9], :showing? false, :suit :clubs}
                                             {:card [:ten 10], :showing? false, :suit :diamonds}],
                                      :high 14,
                                      :ranks (10 11 12 13 14),
                                      :score :straight})

(fact "fullhouse is 3 of one and two of another"
      (is-fullhouse? fullhouse) => '{:hand [{:card [:jack 12], :showing? false, :suit :diamonds}
                                            {:card [:jack 12], :showing? false, :suit :clubs}
                                            {:card [:jack 12], :showing? false, :suit :hearts}
                                            {:card [:queen 11], :showing? false, :suit :clubs}
                                            {:card [:queen 11], :showing? false, :suit :clubs}
                                            {:card [:nine 9], :showing? false, :suit :clubs}
                                            {:card [:ten 10], :showing? false, :suit :diamonds}],
                                     :high 12,
                                     :ranks (11 11 12 12 12),
                                     :score :fullhouse})

(fact "get the frequency of cards"
      (hand-card-freq ahand)
      =>
      '([11 2] [13 1] [7 1] [1 1]))

(fact "is it a flush"
      (is-flush? ahand) => false
      (is-flush? flush-hand) => true
      (flush? ahand) => nil

      (flush? flush-hand) => '{:hand ({:card [:jack 11], :showing? false, :suit :diamonds}
                                      {:card [:eight 8], :showing? false, :suit :diamonds}
                                      {:card [:queen 12], :showing? false, :suit :diamonds}
                                      {:card [:king 13], :showing? false, :suit :diamonds}
                                      {:card [:ten 10], :showing? false, :suit :diamonds}),
                               :high 13,
                               :ranks (8 10 11 12 13),
                               :score :flush}

      (straight-flush flush-hand) => nil

      (straight-flush straight-flush-hand)
      =>
      '{:cards
        ({:card [:jack 11], :showing? false, :suit :diamonds}
         {:card [:nine 9], :showing? false, :suit :diamonds}
         {:card [:queen 12], :showing? false, :suit :diamonds}
         {:card [:king 13], :showing? false, :suit :diamonds}
         {:card [:ten 10], :showing? false, :suit :diamonds}),
        :hand ({:card [:jack 11], :showing? false, :suit :diamonds}
               {:card [:nine 9], :showing? false, :suit :diamonds}
               {:card [:queen 12], :showing? false, :suit :diamonds}
               {:card [:king 13], :showing? false, :suit :diamonds}
               {:card [:ten 10], :showing? false, :suit :diamonds}),
        :high 13,
        :ranks (9 10 11 12 13),
        :score :straight-flush})

(fact "is it a fullhouse"
      (is-fullhouse? ahand) => nil
      (is-fullhouse? fullhouse) => '{:hand [{:card [:jack 12], :showing? false, :suit :diamonds}
                                            {:card [:jack 12], :showing? false, :suit :clubs}
                                            {:card [:jack 12], :showing? false, :suit :hearts}
                                            {:card [:queen 11], :showing? false, :suit :clubs}
                                            {:card [:queen 11], :showing? false, :suit :clubs}
                                            {:card [:nine 9], :showing? false, :suit :clubs}
                                            {:card [:ten 10], :showing? false, :suit :diamonds}],
                                     :high 12,
                                     :ranks (11 11 12 12 12),
                                     :score :fullhouse}

      (is-fullhouse? fullhouse-q-hi) => '{:hand [{:card [:jack 12], :showing? false, :suit :diamonds}
                                                 {:card [:jack 12], :showing? false, :suit :clubs}
                                                 {:card [:jack 12], :showing? false, :suit :hearts}
                                                 {:card [:queen 11], :showing? false, :suit :clubs}
                                                 {:card [:queen 11], :showing? false, :suit :clubs}
                                                 {:card [:queen 11], :showing? false, :suit :clubs}
                                                 {:card [:ten 10], :showing? false, :suit :diamonds}],
                                          :high 12,
                                          :ranks (11 11 12 12 12),
                                          :score :fullhouse})

(fact "is it four of a kind"
      (is-four-of-a-kind? ahand) => nil
      (is-four-of-a-kind? four-of-kind-hand)
      =>
      '{:hand [{:card [:jack 11], :showing? false, :suit :diamonds}
               {:card [:jack 11], :showing? false, :suit :clubs}
               {:card [:jack 11], :showing? false, :suit :hearts}
               {:card [:jack 11], :showing? false, :suit :clubs}
               {:card [:ten 10], :showing? false, :suit :diamonds}],
        :high 11,
        :ranks (11 11 11 11),
        :score :fourkind})

(fact "is it 3 of a kind"
      (is-three-of-a-kind? ahand) => nil
      (is-three-of-a-kind? two-pair-hand) => nil

      (is-three-of-a-kind? fullhouse) => '{:hand [{:card [:jack 12], :showing? false, :suit :diamonds}
                                                  {:card [:jack 12], :showing? false, :suit :clubs}
                                                  {:card [:jack 12], :showing? false, :suit :hearts}
                                                  {:card [:queen 11], :showing? false, :suit :clubs}
                                                  {:card [:queen 11], :showing? false, :suit :clubs}
                                                  {:card [:nine 9], :showing? false, :suit :clubs}
                                                  {:card [:ten 10], :showing? false, :suit :diamonds}],
                                           :high 12,
                                           :ranks (12 12 12),
                                           :score :threekind}

      (is-three-of-a-kind? fullhouse-q-hi) => '{:hand [{:card [:jack 12], :showing? false, :suit :diamonds}
                                                       {:card [:jack 12], :showing? false, :suit :clubs}
                                                       {:card [:jack 12], :showing? false, :suit :hearts}
                                                       {:card [:queen 11], :showing? false, :suit :clubs}
                                                       {:card [:queen 11], :showing? false, :suit :clubs}
                                                       {:card [:queen 11], :showing? false, :suit :clubs}
                                                       {:card [:ten 10], :showing? false, :suit :diamonds}],
                                                :high 12,
                                                :ranks (12 12 12),
                                                :score :threekind}

      (is-three-of-a-kind? three-of-kind-hand)
      =>
      '{:hand [{:card [:jack 11], :showing? false, :suit :diamonds}
               {:card [:jack 11], :showing? false, :suit :clubs}
               {:card [:jack 11], :showing? false, :suit :hearts}
               {:card [:queen 12], :showing? false, :suit :clubs}
               {:card [:ten 10], :showing? false, :suit :diamonds}],
        :high 11,
        :ranks (11 11 11),
        :score :threekind})


(fact "is is it a pair?"
      (is-pair? ahand) => '{:hand [{:card [:eight 7], :showing? false, :suit :diamonds}
                                   {:card [:ace 13], :showing? false, :suit :diamonds}
                                   {:card [:two 1], :showing? false, :suit :hearts}
                                   {:card [:queen 11], :showing? false, :suit :clubs}
                                   {:card [:queen 11], :showing? false, :suit :diamonds}],
                            :high 11
                            :ranks (11 11),
                            :score :onepair}

      (is-pair? fullhouse) => nil
      (is-pair? two-pair-hand) => nil

      (is-two-pair? ahand) => '{:hand [{:card [:jack 11], :showing? false, :suit :diamonds}
                                       {:card [:jack 11], :showing? false, :suit :clubs}
                                       {:card [:queen 12], :showing? false, :suit :hearts}
                                       {:card [:queen 12], :showing? false, :suit :clubs}
                                       {:card [:ten 10], :showing? false, :suit :diamonds}],
                                :high 12,
                                :ranks (12 12),
                                :score :onepair}

      (is-two-pair? fullhouse) => nil

      (is-two-pair? two-pair-hand) => '{:hand [{:card [:jack 11], :showing? false, :suit :diamonds}
                                               {:card [:jack 11], :showing? false, :suit :clubs}
                                               {:card [:queen 12], :showing? false, :suit :hearts}
                                               {:card [:queen 12], :showing? false, :suit :clubs}
                                               {:card [:ten 10], :showing? false, :suit :diamonds}],
                                        :high 12,
                                        :ranks (11 11 12 12),
                                        :score :threekind})

(fact "what is the high value"
      (is-high? ahand) => '{:hand [{:card [:eight 7], :showing? false, :suit :diamonds}
                                   {:card [:ace 13], :showing? false, :suit :diamonds}
                                   {:card [:two 1], :showing? false, :suit :hearts}
                                   {:card [:queen 11], :showing? false, :suit :clubs}
                                   {:card [:queen 11], :showing? false, :suit :diamonds}],
                            :high 11,
                            :ranks 11,
                            :score :highcard})

(fact "new game with players"
      (-> (new-game)
          create-players
          init-deal)  => nil)

(defn fake-player [hand]
  [:foo {:name :fake-name :hand hand}])

(tabular "we should be able to get the best score for a hand"
         (facts "give a bunch of hands to be scored"
                (->  (fake-player ?hand)
                     new-score-player
                     :final
                     :score) => ?result)

         ?hand               ?result
         ahand               :onepair
         fullhouse           :fullhouse
         fullhouse-q-hi      :fullhouse
         straight-ace-low    :straight
         straight-flush-hand :straight-flush
         flush-hand          :flush
         four-of-kind-hand   :fourkind
         three-of-kind-hand  :threekind
         two-pair-hand       :twopair
         pair-hand           :onepair
         nothing-hand        :highcard
         astraight           :straight
         three-straights     :straight)

(deftest a-test
  (testing "FIXME, I fail."
    (is (= 0 1))))
