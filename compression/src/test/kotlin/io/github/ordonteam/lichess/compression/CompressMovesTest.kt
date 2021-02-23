package io.github.ordonteam.lichess.compression

import org.fest.assertions.api.Assertions.assertThat
import org.junit.Test

class CompressMovesTest {

    @Test
    fun shouldCompressMoves() {
        val input = sequenceOf(
            "fen1,move1,1,1,0,0",
            "fen1,move1,1,0,1,0",
            "fen1,move1,0,1,0,1",
            "fen1,move1,0,0,1,1",
            "fen1,move2,1,0,0,0",
            "fen2,move1,1,0,0,0"
        )
        val expectedLines = listOf(
            "fen1,move1,2,2,2,2",
            "fen1,move2,1,0,0,0",
            "fen2,move1,1,0,0,0"
        )
        assertThat(compressMoves(input)).isEqualTo(expectedLines)
    }
}