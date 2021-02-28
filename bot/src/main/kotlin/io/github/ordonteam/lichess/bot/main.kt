package io.github.ordonteam.lichess.bot

fun main(args: Array<String>) {
    val lichessToken = args[0]
    run(lichessToken)
}

private fun run(lichessToken: String) {
    try {
        LichessBot(lichessToken, MoveFromDiskBotStrategy()).runBot()
    } catch (e: Throwable) {
        run(lichessToken)
    }
}