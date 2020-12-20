package io.github.kmakma.adventofcode.utils

import java.math.BigInteger

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

fun generateStringPermutations(lists: List<List<String>>, maxStringLength: Int = 0): List<String> {
    val result = mutableListOf<String>()
    stringPermutations(lists, result, 0, "", maxStringLength)
    return result
}

private fun stringPermutations(
    lists: List<List<String>>,
    result: MutableList<String>,
    depth: Int,
    current: String,
    maxLength: Int
) {
    if (depth == lists.size || (maxLength > 0 && current.length >= maxLength)) {
        result.add(current)
        return
    }
    if (lists[depth].isEmpty()) {
        stringPermutations(lists, result, depth + 1, current, maxLength)
        return
    }
    for (s in lists[depth]) {
        stringPermutations(lists, result, depth + 1, current + s, maxLength)
    }
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

/**
 * to be used for methods like [List.getOrElse], if a `0` is required as result. Example:
 *
 * `list.getOrElse(index, ::else0)`
 */
@Suppress("UNUSED_PARAMETER")
fun else0(x: Int) = 0

/**
 * planned as a faster alternative to y2019.d16.t2...
 * But an index of around 523k created a tiny little 1E411 (which didn't even took that long)
 */
@Suppress("unused")
object StupidBigAssNumberCreator {
    private val factorList = mutableListOf(BigInteger("100"))
    internal fun getFactor(index: Int): BigInteger {
        if (factorList.lastIndex < index) {
            factorList.add(index, getFactor(index - 1) * (100L + index).toBigInteger() / (index + 1).toBigInteger())
        }
        return factorList[index]
    }
}

fun Iterable<Int>.product(): Long {
    var product = 1L
    for (element in this) {
        product *= element
    }
    return product
}
