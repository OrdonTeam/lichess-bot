package io.github.ordonteam.lichess.bot

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
