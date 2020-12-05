package io.github.kmakma.adventofcode.y2020

import io.github.kmakma.adventofcode.utils.Day

fun main() {
    Y2020Day05().solveAndPrint()
}

class Y2020Day05 : Day(2020, 5, "Binary Boarding") {
    private lateinit var inputLines: List<String>
    private lateinit var boardingPassIDs: List<Int>
    override fun initializeDay() {
        inputLines = inputInStringLines()
        boardingPassIDs = inputLines.map { calculateSeatID(it) }
    }

    override suspend fun solveTask1(): Any? {
        return boardingPassIDs.maxOrNull()
    }

    private fun calculateSeatID(line: String): Int {
        val row = line.substring(0, 7).toBinary('B', 'F').toInt(2)
        val col = line.substring(7).toBinary('R', 'L').toInt(2)
        return row * 8 + col
    }

    override suspend fun solveTask2(): Any? {
        val sortedIDs = boardingPassIDs.sorted()
        for (i in sortedIDs.indices) {
            if (sortedIDs[i] + 1 == sortedIDs[i + 1] - 1)
                return sortedIDs[i] + 1
        }
        return null
    }
}

private fun String.toBinary(one: Char, zero: Char? = null): String {
    return buildString(length) {
        this@toBinary.forEach { c ->
            when {
                c == one -> append('1')
                c == zero || zero == null -> append('0')
                else -> append(c)
            }
        }
    }
}
