package io.github.kmakma.adventofcode.y2019

fun listInAllOrders(a: List<Int>): List<List<Int>> {

    return allOrders(arrayOf<Int>(), a.toMutableList())
}

private fun allOrders(ordered: Array<Int>, unordered: List<Int>): List<List<Int>> {
    if (unordered.size == 1) {
        return listOf(listOf(*ordered, unordered.first()))
    }
    return unordered.flatMap { allOrders(arrayOf(*ordered, it), unordered.minus(it)) }
}