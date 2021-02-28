package io.github.ordonteam.lichess.bot

import io.github.ordonteam.lichess.common.Counters
import java.io.File

class MovesFromFileProvider(private val numberOfFiles: Int,private val movesDirectory:String) : MovesProvider {

    override fun getMovesForFen(fen: String): Map<String, Counters> {
        val hashCodeMod = (fen.hashCode() % numberOfFiles + numberOfFiles) % numberOfFiles
        val movesFile = File(movesDirectory, hashCodeMod.toString())
        return movesFile.useLines { lines ->
            lines.filter { it.startsWith(fen) }.groupBy(
                keySelector = { it.split(",")[1] },
                valueTransform = { Counters.fromStrings(it.split(",").takeLast(4)) }
            ).mapValues { Counters.sumCounters(it.value) }
        }
    }
}