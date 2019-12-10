package io.github.kmakma.adventofcode.utils

/**
 * Returns lists with elements of the list ordered in all possible ways
 */
fun <E> List<E>.allOrders(): List<List<E>> {
    return allOrders(unordered = this)
}

private fun <E> allOrders(ordered: List<E> = listOf(), unordered: List<E>): List<List<E>> {
    if (unordered.size == 1) {
        return listOf(ordered + unordered.first())
    }
    return unordered.flatMap { allOrders(ordered + it, unordered - it) }
}

/**
 * greatest common divisor
 *
 * tip: with a fraction x/y divide by gdc/gdc to get lowest form of x/y
 */
fun gcd(x: Int, y: Int): Int {
    return if (y != 0) {
        gcd(y, x % y)
    } else {
        x
    }
}
