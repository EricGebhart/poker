(ns poker.core
  (:require [poker.deck :as deck]
            [clojure.string :as str]))

(def empty-hand [])
(def empty-discard [])

(defn new-player
  "returns a player"
  [name]
  {(gensym) {:name name
             :hand empty-hand}})

(defn player-hand-count [player]
  (count (:hand player)))

(defn player-show-hand [player]
  (print (:hand player)))

(defn new-game
  "Returns a data representation of a new card game)"
  [& {:keys [cards] :or {cards 5}}]
  {:deck (deck/new-deck)
   :discard empty-discard
   :players {}
   :card-count cards
   :dealer empty-hand})

(defn create-players [game]
  (println "Player Name ? Return to quit")
  (merge game
         {:players
          (into {}
                (map new-player
                     (take-while not-empty
                                 (repeatedly read-line))))}))

(defn give-card [game player-id card]
  ;;{:pre [-> game :players (get player-id) :hand count (< 5)]}
  (let [players (:players game)
        player (get players player-id)
        player-hand (conj (:hand player) card)]
    (merge player {:hand player-hand})))

(defn deal [game player-id count]
  (let [top-card (first (:deck game))
        deck (rest (:deck game))
        player (give-card game player-id top-card)]
    (merge game {:deck deck
                 :players (merge (:players game)
                                 {player-id player})})))

(defn init-deal [game]
  (reduce #(deal %1 %2 1)
          game
          (->> (keys (:players game))
               (repeat (:card-count game))
               flatten)))

(def hand-rankings {:royalflush 1000,
                    :straightflush 900,
                    :fourkind 800,
                    :fullhouse 700,
                    :flush 600,
                    :straight 500,
                    :threekind 400,
                    :twopair 300,
                    :onepair 200,
                    :highcard 100})

(defn player-hand [player]
  (:hand player))

(defn sorted-hand [hand]
  (-> (map :card hand)
      (map second)
      sort))

(defn hand-card-freq [hand]
  (->> (map :card hand)
       (map second)
       frequencies
       (sort-by first >)
       (sort-by second >)))

(defn final-score-record [score high ranks hand]
  {:score score :high high :ranks ranks :hand hand}
  )
(defn top-suit-count [hand]
  (-> (map :suit hand)
      frequencies
      first))

(defn is-flush? [hand]
  (-> hand
      top-suit-count
      second
      (>= 5)))

(defn flush? [hand]
  (let [suit-count (top-suit-count hand)
        suit (first suit-count)
        hand (filter #(= suit (:suit %)) hand)
        high-ranks (->>  (map #(second (:card %)) hand)
                         sort
                         wrap-high-card
                         (partition 5 1)   ; get list of sequences of 5.
                         last)]
    (when (= 5 (count high-ranks))
      (final-score-record :flush (last high-ranks) high-ranks hand))))

(defn is-fullhouse? [hand]
  (let [f (hand-card-freq hand)]
    (when (and (= (second (first f)) 3)
               (>= (second (second f)) 2))
      (final-score-record :fullhouse (ffirst f)
                          (into (repeat 3 (ffirst f))
                                (repeat 2 (first (second f))))
                          hand))))

(defn is-four-of-a-kind? [hand]
  (let [f (hand-card-freq hand)]
    (when (= (second (first f)) 4)
      (final-score-record :fourkind (ffirst f) (repeat 4 (ffirst f)) hand)))

  )

(defn is-three-of-a-kind? [hand]
  (let [f (hand-card-freq hand)]
    (when (= (second (first f)) 3)
      (final-score-record :threekind (ffirst f) (repeat 3 (ffirst f)) hand))))

(defn is-two-pair? [hand]
  (let [f (hand-card-freq hand)]
    (when (and (= (second (first f)) 2)
               (= (second (second f)) 2))
      (final-score-record :twopair (ffirst f)
                          (into (repeat 2 (ffirst f)) (repeat 2 (first (second f))))
                          hand))))

(defn is-pair? [hand]
  (let [f (hand-card-freq hand)]
    (when (= (second (first f)) 2)
      (final-score-record :onepair (ffirst f) (repeat 2 (ffirst f)) hand))))

(defn is-high? [hand]
  (let [f (hand-card-freq hand)]
    (final-score-record :highcard (ffirst f) (ffirst f) hand)))

(defn is-straight? [hand]
  (let [card-values (sort < (vals deck/cards))
        hand-cards (map :card hand)
        hand-scores (sort < (map second hand-cards))
        hand (if (and (= (first hand-scores)
                         (first card-values))
                      (= (last hand-scores)
                         (last card-values)))
               (cons (dec (first card-values)) (take 4 hand-scores))
               hand-scores)]
    (every? #{1} (map - (rest hand) hand))))


;; List of lists of rank values that qualify as straights

(def ace-low-straight (list '(14 2 3 4 5)))

(def straightlist
  (into
   ace-low-straight
   (partition 5 1 (sort (map second deck/cards)))))

(defn straight-filter [cards]
  (some #(= cards %) straightlist))

(defn wrap-high-card  [cards]
  (conj cards (last cards)))

(defn straight [hand]
  "Returns the high card and best straight, else false;
  Doesn't care how many cards there are."
  (let [high-straight (->>  (map #(second (:card %)) hand)
                            distinct
                            sort
                            wrap-high-card
                            (partition 5 1)   ; get list of sequences of 5.
                            (filter straight-filter)  ;get the straights
                            last)] ; get the highest straight
    (when (not-empty high-straight)
      (final-score-record :straight (last high-straight) high-straight hand)
      )))

(defn straight-flush [hand]
  (let [suit-count (top-suit-count hand)
        suit (first suit-count)
        hand (filter #(= suit (:suit %)) hand)
        straight (when  (> (second suit-count) 4)
                   (straight hand))]
    (when straight
      (merge straight
             {:score :straightflush :cards hand}))))

(defn assign-rank-value [final-score-record]
  (let [score (:score final-score-record)
        high (:high final-score-record)
        hand-rank (+ high (score hand-rankings))]
    (merge final-score-record {:hand-rank hand-rank})))

(defn new-score-player [player]
  (let [player-data (second player)
        hand (:hand player-data)
        score (first
               (remove nil?
                       [(straight-flush hand)
                        (is-four-of-a-kind? hand)
                        (is-fullhouse? hand)
                        (flush? hand)
                        (straight hand)
                        (is-three-of-a-kind? hand)
                        (is-two-pair? hand)
                        (is-pair? hand)
                        (is-high? hand)]))]
    {:name (:name player-data) :final (assign-rank-value score)}))



(defn score-player [player]
  (let [player-data (val player)
        hand (:hand player-data)
        score (cond
                (and (is-straight? hand) (is-flush? hand)) 1000
                (is-four-of-a-kind? hand) 900
                (is-fullhouse? hand) 800
                (is-flush? hand) 700
                (is-straight? hand) 600
                (is-three-of-a-kind? hand) 500
                (is-two-pair? hand) 400
                (is-pair? hand) 300
                :else 200)]
    [(:name player-data) (+ (is-high? hand) score)]))

(defn pretty-score [player-final-score-record]
  (let [name (:name player-final-score-record)
        hand-data (:final player-final-score-record)
        score (:score hand-data)
        high (:high hand-data)
        hand-rank (:hand-rank hand-data)]
    (str/join " " ["Name:" name "hand:" score "high:" high "rank:" hand-rank])))

(defn show-score [m]
  (map pretty-score
       (sort-by #(get-in % [:final :hand-rank]) > m)))

(defn score-game [game]
  (map new-score-player (:players game)))

(defn play []
  (-> (new-game :cards 5)
      create-players
      init-deal
      score-game
      show-score
      ))
