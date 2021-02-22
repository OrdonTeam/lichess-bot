package io.github.ordonteam.lichess.expansion

import com.github.bhlangonijr.chesslib.Board
import com.github.bhlangonijr.chesslib.move.MoveList

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

private fun parseGameFromSan(san: String): List<Pair<String, String>> {
    val moveList = MoveList()
    moveList.loadFromSan(san)
    val board = Board()
    return moveList.map {
        val fen = board.fen
        board.doMove(it)
        fen to it.toString()
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
