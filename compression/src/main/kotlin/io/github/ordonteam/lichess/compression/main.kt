package io.github.ordonteam.lichess.compression

import java.io.File
import java.time.LocalTime
import java.util.concurrent.atomic.AtomicInteger

fun main() {
    compressMoveFiles("/home/ordon/Documents/moves")
}

fun compressMoveFiles(movesDirectoryPath: String) {
    val movesDirectory = File(movesDirectoryPath)
    val numberOfFilesProcessed = AtomicInteger(0)
    movesDirectory.listFiles()!!.forEach { movesFile ->
        println("${LocalTime.now()} Number of files compressed ${numberOfFilesProcessed.getAndIncrement()}")
        val compressedLines = movesFile.useLines { lines ->
            compressMoves(lines)
        }
        val compressedFile = File(movesDirectory, movesFile.name + "-compressed")
        compressedFile.writer().use { writer ->
            compressedLines.forEach {
                writer.write(it + "\n")
            }
        }
        movesFile.delete()
        compressedFile.renameTo(movesFile)
    }
}

