package io.github.ordonteam.lichess.expansion

import io.github.ordonteam.lichess.bot.parseGameFromSan

internal fun expandGamesToMoves(lines: Sequence<String>): Sequence<Pair<Int, String>> {
    return lines
        .filter { it.startsWith("1.") }
        .map { removeEval(it) }
        .flatMap { parseGame(it) }
}

private fun parseGame(game: String): List<Pair<Int, String>> {
    val san = game.take(game.lastIndexOf(" "))
    val gameResult = game.drop(game.lastIndexOf(" ") + 1)
    val moves = parseGameFromSan(san)
    val gameResultString = parseGameResult(gameResult)
    return moves.map { (fen, move) ->
        fen.hashCode() to "$fen,$move,$gameResultString"
    }
}

private fun parseGameResult(gameResult: String): String {
    return when (gameResult) {
        "1-0" -> "1,0,0,0"
        "0-1" -> "0,1,0,0"
        "1/2-1/2" -> "0,0,1,0"
        "*" -> "0,0,0,1"
        else -> throw UnsupportedOperationException("Unable to parse game result: $gameResult")
    }
}
