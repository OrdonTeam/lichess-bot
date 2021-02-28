package io.github.ordonteam.lichess.bot

import io.github.ordonteam.lichess.bot.BotStrategy.*
import io.github.ordonteam.lichess.bot.LichessApi.LichessGameEvent
import io.github.ordonteam.lichess.bot.LichessApi.LichessGlobalEvent
import io.reactivex.Observable

class LichessBot(lichessToken: String, private val botStrategy: BotStrategy) {

    private val lichessApi = LichessApi(lichessToken)

    fun runBot() {
        lichessApi.lichessGlobalEvents()
            .map { println(it); it }
            .publish { allEvents ->
                Observable.merge(
                    acceptChallenges(allEvents),
                    playGames(allEvents),
                )
            }
            .blockingSubscribe()
    }

    private fun acceptChallenges(allEvents: Observable<LichessGlobalEvent>): Observable<Unit> {
        return allEvents.ofType(LichessGlobalEvent.Challenge::class.java)
            .map { lichessApi.acceptChallenge(it.id) }
            .map { Unit }
    }

    private fun playGames(allEvents: Observable<LichessGlobalEvent>): Observable<Unit> {
        return allEvents.ofType(LichessGlobalEvent.GameStart::class.java)
            .flatMap { game ->
                lichessApi.lichessGameEvents(game.id)
                    .map { println(it); it }
                    .scan(LichessGameDetailsOrNull()) { acc, event -> play(acc, event, game.id) }
                    .takeUntil(allEvents
                        .ofType(LichessGlobalEvent.GameFinish::class.java)
                        .filter { it.id == game.id })
            }
            .map { Unit }
    }

    private data class LichessGameDetailsOrNull(val lichessGameDetails: LichessGameDetails? = null)

    private fun play(
        lichessGameDetails: LichessGameDetailsOrNull,
        event: LichessGameEvent,
        gameId: String
    ): LichessGameDetailsOrNull {
        if (lichessGameDetails.lichessGameDetails == null) {
            if (event is LichessGameEvent.GameFull) {
                return play(getLichessGameDetails(gameId, event), event).orNull()
            }
            return lichessGameDetails
        }
        return play(lichessGameDetails.lichessGameDetails, event).orNull()
    }

    private fun play(
        lichessGameDetails: LichessGameDetails,
        event: LichessGameEvent
    ): LichessGameDetails {
        doMove(lichessGameDetails, event)
        return lichessGameDetails
    }

    private fun doMove(lichessGameDetails: LichessGameDetails, event: LichessGameEvent) {
        val moves = getMoves(event) ?: return
        val fen = movesToFen(moves)
        val nextSide = if (fen.split(" ")[1] == "w") Side.WHITE else Side.BLACK
        if (nextSide == lichessGameDetails.botSide) {
            val nextMove = botStrategy.nextMove(LichessGame(lichessGameDetails, fen))
            if (nextMove != null) {
                lichessApi.makeMove(lichessGameDetails.gameId, nextMove)
            } else {
                lichessApi.resign(lichessGameDetails.gameId)
            }
        }
    }

    private fun getLichessGameDetails(id: String, event: LichessGameEvent.GameFull): LichessGameDetails {
        return LichessGameDetails(
            gameId = id,
            opponentId = if (event.whiteId == "ordonteam") event.blackId else event.whiteId,
            botSide = if (event.whiteId == "ordonteam") Side.WHITE else Side.BLACK
        )
    }

    private fun getMoves(event: LichessGameEvent): String? {
        return when (event) {
            is LichessGameEvent.GameFull -> event.moves
            is LichessGameEvent.GameState -> event.moves
            else -> null
        }
    }

    private fun LichessGameDetails.orNull(): LichessGameDetailsOrNull {
        return LichessGameDetailsOrNull(this)
    }
}


