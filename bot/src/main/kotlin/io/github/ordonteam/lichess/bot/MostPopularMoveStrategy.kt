package io.github.ordonteam.lichess.bot

import io.github.ordonteam.lichess.bot.BotStrategy.LichessGame

class MostPopularMoveStrategy(private val movesProvider: MovesProvider) : BotStrategy {

    override fun nextMove(lichessGame: LichessGame): String? {
        return movesProvider.getMovesForFen(lichessGame.fen).maxByOrNull { it.value.all }?.key
    }

    override fun chatLine(playerId: String, text: String) = Unit
}
