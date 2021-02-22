package io.github.ordonteam.lichess.expansion

import org.fest.assertions.api.Assertions.assertThat
import org.junit.Test
import java.io.File

class ExpansionTest {

    @Test
    fun shouldExpandGame() {
        File(ExpansionTest::class.java.getResource("/game.pgn").file).useLines { inputLines ->
            File(ExpansionTest::class.java.getResource("/moves").file).useLines { expectedLines ->
                assertThat(expandGamesToMoves(inputLines).map { it.second }.toList()).isEqualTo(expectedLines.toList())
            }
        }
    }
}