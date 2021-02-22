package io.github.ordonteam.lichess.expansion

import java.io.File
import java.io.OutputStreamWriter

class DistributedMovesWriter(movesDirectory: String, private val numberOfFiles: Int) : AutoCloseable {

    private val writers: List<OutputStreamWriter> = (0 until numberOfFiles).map {
        File(movesDirectory, it.toString()).writer()
    }

    fun write(moveRow: Pair<Int, String>) {
        val hashCodeMod = (moveRow.first % numberOfFiles + numberOfFiles) % numberOfFiles
        writers[hashCodeMod].write(moveRow.second)
    }

    override fun close() {
        writers.forEach {
            it.close()
        }
    }
}