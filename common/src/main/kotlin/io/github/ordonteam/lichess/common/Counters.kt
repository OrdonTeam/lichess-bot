package io.github.ordonteam.lichess.common

data class Counters(
    val white: Int, val black: Int, val draw: Int, val ongoing: Int
) {
    val all = white + black + draw + ongoing

    fun add(other: Counters): Counters {
        return Counters(
            white = white + other.white,
            black = black + other.black,
            draw = draw + other.draw,
            ongoing = ongoing + other.ongoing
        )
    }

    companion object {
        val EMPTY_COUNTERS = Counters(
            white = 0, black = 0, draw = 0, ongoing = 0
        )

        fun fromStrings(strings: List<String>): Counters {
            return Counters(
                white = strings[0].toInt(),
                black = strings[1].toInt(),
                draw = strings[2].toInt(),
                ongoing = strings[3].toInt()
            )
        }

        fun sumCounters(counters: List<Counters>): Counters {
            return counters.fold(EMPTY_COUNTERS) { acc, counter -> acc.add(counter) }
        }
    }
}