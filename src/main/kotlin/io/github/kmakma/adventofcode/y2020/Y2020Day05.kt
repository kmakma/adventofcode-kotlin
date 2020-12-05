package io.github.kmakma.adventofcode.y2020

import io.github.kmakma.adventofcode.utils.Day

fun main() {
    Y2020Day05().solveAndPrint()
}

class Y2020Day05 : Day(2020, 5, "") {
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
        val rowString = line.substring(0, 7)
        val colString = line.substring(7)
        var rowStart = 0
        var rowEnd = 127
        for (rowSpecifier in rowString) {
            val modifier = (rowEnd - rowStart + 1) / 2
            when (rowSpecifier) {
                'F' -> rowEnd -= modifier
                'B' -> rowStart += modifier
            }
        }
        var colStart = 0
        var colEnd = 7
        for (colSpecifier in colString) {
            val modifier = (colEnd - colStart) / 2 + 1
            when (colSpecifier) {
                'L' -> colEnd -= modifier
                'R' -> colStart += modifier
            }
        }
        return rowStart * 8 + colStart
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
