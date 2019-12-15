package io.github.kmakma.adventofcode.utils

/**
 * Returns lists with elements of the list ordered in all possible ways
 */
fun <E> List<E>.allPermutations(): List<List<E>> {
    return allPermutations(unordered = this)
}


private fun <E> allPermutations(ordered: List<E> = emptyList(), unordered: List<E>): List<List<E>> {
    if (unordered.size == 1) {
        return listOf(ordered + unordered.first())
    }
    return unordered.flatMap { allPermutations(ordered + it, unordered - it) }
}

/**
 * greatest common divisor
 *
 * tip: with a fraction x/y divide by gcd/gcd to get lowest form of x/y
 */
fun gcd(a: Int, b: Int): Int {
    return if (b == 0) a
    else gcd(b, a % b)
}

/**
 * greatest common divisor
 *
 * tip: with a fraction x/y divide by gcd/gcd to get lowest form of x/y
 */
fun gcd(a: Long, b: Long): Long {
    return if (b == 0L) a
    else gcd(b, a % b)
}

/**
 * lowest common multiple
 */
fun lcm(a: Long, b: Long): Long {
    return (a * b) / gcd(a, b)
}