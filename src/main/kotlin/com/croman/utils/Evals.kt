package com.croman.utils

/**
 * Evaluates `expression` (returning its result) if `condition` is `true`, otherwise returns `null`.
 */
fun <T> evalIf(condition: Boolean, expression: () -> T): T? {
    return if (condition) expression() else null
}