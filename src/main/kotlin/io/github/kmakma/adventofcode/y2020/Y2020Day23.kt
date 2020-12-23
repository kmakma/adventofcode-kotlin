package io.github.kmakma.adventofcode.y2020

import io.github.kmakma.adventofcode.utils.Day
import java.util.*

fun main() {
    Y2020Day23().solveAndPrint()
}

class Y2020Day23 : Day(2020, 23, "Crab Cups") {

    private lateinit var inputLine: List<Int>
    private var min: Int = -1

    override fun initializeDay() {
        inputLine = inputInOneLine().map { Character.getNumericValue(it) }
        min = inputLine.minOrNull() ?: error("no min value")
    }

    override suspend fun solveTask1(): Any {
        val cups = inputLine.toMutableList()
        var current = 0
        for (i in 1..100) {
            playRound(cups, current)
            current = (current + 1) % cups.size
        }
        Collections.rotate(cups, -cups.indexOf(1))
        cups.removeFirst()
        return cups.joinToString("")
    }

    override suspend fun solveTask2(): Any? {
        return null
        val cups = inputLine.toMutableList()
        var last = cups.maxOrNull() ?: error("no max value")
        while (cups.size < 1000000) {
            cups.add(++last)
        }
        var current = 0
        for (i in 1..10000000) {
            playRound(cups, current)
            current = (current + 1) % cups.size
        }
        Collections.rotate(cups, -cups.indexOf(1))
        cups.removeFirst()
        return Pair(cups[0], cups[1])
    }

    private fun playRound(cups: MutableList<Int>, currentIndex: Int) {
        val current = cups[currentIndex]
        var dest = current

        val nextThree = pickUp(cups, currentIndex)
        var indexNextDest: Int = -1

        while (indexNextDest < 0) {
            dest = if (dest - 1 < min) cups.maxOrNull() ?: error("no max value") else dest - 1
            indexNextDest = cups.indexOf(dest)
            if (indexNextDest >= 0) {
                cups.addAll(indexNextDest + 1, nextThree)
            }
        }
        Collections.rotate(cups, currentIndex - cups.indexOf(current))
    }

    private fun pickUp(cups: MutableList<Int>, index: Int): List<Int> {
        val rot = if (index + 3 > cups.lastIndex) {
            cups.lastIndex - (index + 3)
        } else {
            0
        }
        Collections.rotate(cups, rot)
        return listOf(
            cups.removeAt(index + rot + 1),
            cups.removeAt(index + rot + 1),
            cups.removeAt(index + rot + 1)
        )
    }
}