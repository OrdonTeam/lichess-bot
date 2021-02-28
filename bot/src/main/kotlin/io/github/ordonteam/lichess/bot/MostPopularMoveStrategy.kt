package io.github.ordonteam.lichess.bot

import io.github.ordonteam.lichess.bot.BotStrategy.LichessGame

class MostPopularMoveStrategy(private val movesProvider: MovesProvider) : BotStrategy {
    override fun nextMove(lichessGame: LichessGame): String? {
        return nextMoveFromDisk(lichessGame.fen)
    }

    private fun nextMoveFromDisk(fen: String): String? {
        return movesProvider.getMovesForFen(fen).maxByOrNull { it.value.all }?.key
    }
}
