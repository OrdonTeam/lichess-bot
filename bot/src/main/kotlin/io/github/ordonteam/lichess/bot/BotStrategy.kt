package io.github.ordonteam.lichess.bot

interface BotStrategy {

    fun nextMove(lichessGame: LichessGame): String?

    fun chatLine(playerId: String, text: String)

    data class LichessGame(val gameDetails: LichessGameDetails, val fen: String)

    data class LichessGameDetails(val gameId: String, val opponentId: String, val botSide: Side)

    enum class Side { WHITE, BLACK }
}