package io.github.ordonteam.lichess.expansion

import java.io.File
import java.time.LocalTime
import java.util.concurrent.atomic.AtomicLong

fun main() {
    val inFile = "/home/ordon/Documents/lichess/lichess_db_standard_rated_2021-01.pgn"
    val movesDirectory = "/home/ordon/Documents/lichess/moves"
    val numberOfFiles = 100_000
    expandFileToMoves(inFile, movesDirectory, numberOfFiles)
}

fun expandFileToMoves(inFile: String, movesDirectoryPath: String, numberOfFiles: Int) {
    File(inFile).useLines { pgnLines ->
        DistributedMovesWriter(movesDirectoryPath, numberOfFiles).use { out ->
            logEveryNGameInPgn(pgnLines).let(::expandGamesToMoves).forEach(out::write)
        }
    }
}

private fun logEveryNGameInPgn(pgnLines: Sequence<String>): Sequence<String> {
    val count = AtomicLong(0)
    return pgnLines.map {
        it.also {
            if (it.startsWith("1.")) {
                val current = count.getAndIncrement()
                if (current % 1_000_000 == 0L) {
                    println("${LocalTime.now()} Number of game parsed $current")
                }
            }
        }
    }
}
