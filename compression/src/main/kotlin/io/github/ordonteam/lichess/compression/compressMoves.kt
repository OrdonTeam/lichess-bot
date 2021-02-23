package io.github.ordonteam.lichess.compression

internal fun compressMoves(lines: Sequence<String>): List<String> {
    val emptyCounters = Counters(0, 0, 0, 0)
    val fenToMoveToCounter = mutableMapOf<String, MutableMap<String, Counters>>()
    lines.map { it.split(",") }.forEach { it ->
        val fen = it[0]
        val move = it[1]
        val counters = Counters(it[2].toInt(), it[3].toInt(), it[4].toInt(), it[5].toInt())
        val moveToCounters = fenToMoveToCounter[fen] ?: mutableMapOf()
        val currentCounters = moveToCounters[move] ?: emptyCounters
        moveToCounters[move] = currentCounters.add(counters)
        fenToMoveToCounter[fen] = moveToCounters
    }
    return fenToMoveToCounter.flatMap { (fen, moveToCounter) ->
        moveToCounter.map { (move, counter) ->
            "$fen,$move,${counter.white},${counter.draw},${counter.draw},${counter.ongoing}"
        }
    }
}

private data class Counters(val white: Int, val black: Int, val draw: Int, val ongoing: Int) {
    fun add(other: Counters): Counters {
        return Counters(white + other.white, black + other.black, draw + other.draw, ongoing + other.ongoing)
    }
}
