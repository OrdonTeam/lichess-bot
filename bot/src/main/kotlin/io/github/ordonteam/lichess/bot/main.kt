package io.github.ordonteam.lichess.bot

fun main(args: Array<String>) {
    val lichessToken = args[0]
    val botId = args[1]
    run(lichessToken, botId)
}

private fun run(lichessToken: String, botId: String) {
    try {
        LichessBot(lichessToken, botId, MoveFromDiskBotStrategy()).runBot()
    } catch (e: Throwable) {
        run(lichessToken, botId)
    }
}