package io.github.ordonteam.lichess.bot

import io.github.ordonteam.lichess.bot.BotStrategy.LichessGame
import io.github.ordonteam.lichess.common.Counters
import java.io.File

class MoveFromDiskBotStrategy : BotStrategy {
    override fun nextMove(lichessGame: LichessGame): String? {
        return nextMoveFromDisk(lichessGame.fen)
    }

    private fun nextMoveFromDisk(fen: String): String? {
        val hashCodeMod = (fen.hashCode() % 10_000 + 10_000) % 10_000
        val movesDirectory = File("/home/ordon/Documents/lichess/moves")
        val movesFile = File(movesDirectory, hashCodeMod.toString())
        return movesFile.useLines { lines ->
            lines.filter { it.startsWith(fen) }.groupBy(
                keySelector = { it.split(" ").takeLast(6).first() },
                valueTransform = { Counters.fromStrings(it.split(" ").takeLast(4)) }
            ).mapValues { Counters.sumCounters(it.value) }.maxByOrNull { it.value.all }?.key
        }
    }
}