package io.github.ordonteam.lichess.compression

import io.github.ordonteam.lichess.common.Counters

internal fun compressMoves(lines: Sequence<String>): List<String> {
    val emptyCounters = Counters.EMPTY_COUNTERS
    val fenToMoveToCounter = mutableMapOf<String, MutableMap<String, Counters>>()
    lines.map { it.split(",") }.forEach { it ->
        val fen = it[0]
        val move = it[1]
        val counters = Counters.fromStrings(it.takeLast(4))
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
