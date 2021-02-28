package io.github.ordonteam.lichess.bot

import com.github.bhlangonijr.chesslib.Board
import com.github.bhlangonijr.chesslib.move.MoveList

fun movesToFen(moves: String): String {
    return if (moves.isBlank()) {
        "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"
    } else {
        val moveList = MoveList()
        moveList.loadFromText(moves)
        moveList.fen
    }
}

fun parseGameFromSan(san: String): List<Pair<String, String>> {
    val moveList = MoveList()
    moveList.loadFromSan(san)
    val board = Board()
    return moveList.map {
        val fen = board.fen
        board.doMove(it)
        fen to it.toString()
    }
}
