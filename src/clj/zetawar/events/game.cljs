(ns zetawar.events.game
  (:require
   [datascript.core :as d]
   [taoensso.timbre :as log]
   [zetawar.app :as app]
   [zetawar.game :as game]
   [zetawar.router :as router]))

;; TODO: notify players of game actions
;;   example: notify zetawar.players/apply-action zetawar.actions/move ...

(defmethod router/handle-event ::move-unit
  [{:as handler-ctx :keys [db]} [_ from-q from-r to-q to-r]]
  {:tx (game/move-tx db (app/current-game db) from-q from-r to-q to-r)})

;; TODO: attack calculation and tx creation should be separated splits so that
;; results of attack calculation can be sent to players
(defmethod router/handle-event ::attack-unit
  [{:as handler-ctx :keys [db]} [_ attacker-q attacker-r target-q target-r]]
  {:tx (game/attack-tx db (app/current-game db)
                       attacker-q attacker-r
                       target-q target-r)})

(defmethod router/handle-event ::repair-unit
  [{:as handler-ctx :keys [db]} [_ q r]]
  (let [[q r] (app/selected-hex db)]
    {:tx (game/repair-tx db (app/current-game db) q r)}))

(defmethod router/handle-event ::capture-base
  [{:as handler-ctx :keys [db]} [_ q r]]
  {:tx (game/capture-tx db (app/current-game db) q r)})

(defmethod router/handle-event ::build-unit
  [{:as handler-ctx :keys [db]} [_ q r unit-type-id]]
  {:tx (game/build-tx db (app/current-game db) q r unit-type-id)})
