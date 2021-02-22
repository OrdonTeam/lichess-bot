package io.github.ordonteam.lichess.expansion

internal fun removeEval(it: String): String {
    var insideBrackets = false
    var wasSpace = false
    return it.filter {
        if (it == '{') {
            insideBrackets = true
        }
        val toReturn = !insideBrackets
        if (it == '}') {
            insideBrackets = false
        }
        if (wasSpace && it == ' ') {
            return@filter false
        }
        if (it == '?') {
            return@filter false
        }
        if (it == '!') {
            return@filter false
        }
        if (toReturn) {
            wasSpace = it == ' '
        }
        return@filter toReturn
    }
        .split(' ')
        .filter { it.isNotBlank() }
        .filter { !it.contains("...") }
        .joinToString(separator = " ")
}