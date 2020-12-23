package io.github.kmakma.adventofcode.y2020

import io.github.kmakma.adventofcode.utils.Day
import io.github.kmakma.adventofcode.utils.product

fun main() {
    Y2020Day23().solveAndPrint()
}

class Y2020Day23 : Day(2020, 23, "Crab Cups") {

    private lateinit var inputLine: List<Int>

    override fun initializeDay() {
        inputLine = inputInOneLine().map { Character.getNumericValue(it) }
    }

    override suspend fun solveTask1(): Any {
        val cups = buildMap()
        playGame(cups, 100)
        return listAfter1(cups).joinToString("")
    }

    override suspend fun solveTask2(): Any {
        val cups = buildMap(1000000)
        playGame(cups, 10000000)
        return listAfter1(cups, 2).product()
    }

    private fun buildMap(minSize: Int = 0): MutableMap<Int, Int> {
        val map = mutableMapOf<Int, Int>()
        val first = inputLine.first()
        var last = first
        var max = first
        for (current in inputLine) {
            map[last] = current
            last = current
            if (current > max) max = current
        }
        while (map.size < minSize - 1) { // -1 because of the last entry
            map[last] = ++max
            last = max
        }
        map[last] = first
        return map
    }

    private fun playGame(cups: MutableMap<Int, Int>, rounds: Int) {
        var current = inputLine.first()
        for (i in 1..rounds) {
            current = playRound(cups, current)
        }
    }

    private fun playRound(cups: MutableMap<Int, Int>, current: Int): Int {
        val nextThree = removeNextThree(cups, current)
        var dest = current - 1
        while (!cups.containsKey(dest)) {
            if ((--dest) < 1) dest = cups.keys.maxOrNull() ?: error("no max value")
        }
        insertList(cups, dest, nextThree)
        return cups[current]!!
    }

    private fun removeNextThree(cups: MutableMap<Int, Int>, current: Int): List<Int> {
        val result = mutableListOf<Int>()
        for (i in 1..3) {
            val next = cups[current]!!
            val temp = cups.remove(next)!!
            cups[current] = temp
            result.add(next)
        }
        return result
    }

    private fun insertList(cups: MutableMap<Int, Int>, dest: Int, list: List<Int>) {
        val last = cups[dest]!!
        var current = dest
        for (next in list) {
            cups[current] = next
            current = next
        }
        cups[current] = last
    }

    private fun listAfter1(cups: MutableMap<Int, Int>, limit: Int = 0): List<Int> {
        val result = mutableListOf<Int>()
        var current: Int = cups[1]!!
        while (current != 1 && (limit < 1 || result.size < limit)) {
            result.add(current)
            current = cups[current]!!
        }
        return result
    }
}