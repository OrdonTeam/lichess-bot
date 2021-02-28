package io.github.ordonteam.lichess.bot

import io.github.ordonteam.lichess.common.Counters

interface MovesProvider {

    fun getMovesForFen(fen: String): Map<String, Counters>
}
