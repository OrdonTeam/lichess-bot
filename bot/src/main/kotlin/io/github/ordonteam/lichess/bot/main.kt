package io.github.ordonteam.lichess.bot

fun main(args: Array<String>) {
    val lichessToken = args[0]
    val botId = args[1]
    val numberOfFiles = 10_000
    val movesDirectory = "/home/ordon/Documents/lichess/moves"
    run(lichessToken, botId, numberOfFiles, movesDirectory)
}

private fun run(lichessToken: String, botId: String, numberOfFiles: Int, movesDirectory: String) {
    try {
        val movesProvider = MovesFromFileProvider(numberOfFiles, movesDirectory)
        LichessBot(lichessToken, botId, MostPopularMoveStrategy(movesProvider)).runBot()
    } catch (e: Throwable) {
        run(lichessToken, botId, numberOfFiles, movesDirectory)
    }
}