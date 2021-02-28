package io.github.ordonteam.lichess.bot

import io.github.ordonteam.lichess.bot.BotStrategy.LichessGame

class MostPopularMoveStrategy(private val movesProvider: MovesProvider) : BotStrategy {

    override fun nextMove(lichessGame: LichessGame): String? {
        val movesForFen = movesProvider.getMovesForFen(lichessGame.fen).map { it.key to it.value }
        if (movesForFen.isEmpty()) {
            return null
        }
        val maxCount = movesForFen.maxByOrNull { it.second.all }!!.second.all
        return movesForFen.filter { it.second.all * 10 > maxCount }.random().first
    }

    override fun chatLine(playerId: String, text: String) = Unit
}
