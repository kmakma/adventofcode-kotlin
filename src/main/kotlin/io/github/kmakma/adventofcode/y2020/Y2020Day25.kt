package io.github.kmakma.adventofcode.y2020

import io.github.kmakma.adventofcode.utils.Day

fun main() {
    Y2020Day25().solveAndPrint()
}

class Y2020Day25 : Day(2020, 25, "Combo Breaker") {
    private lateinit var publicKeys: List<Long>
    private val subjectNumber = 7
    private lateinit var inputLines: List<String>

    override fun initializeDay() {
        inputLines = inputInStringLines()
        publicKeys = inputLines.map { it.toLong() }
    }

    override suspend fun solveTask1(): Any {
        var value = 1L
        var loop = 0
        while (value != publicKeys[0]) {
            value = (value * subjectNumber) % 20201227
            loop++
        }
        var encryptionKey = 1L
        for (i in 1..loop) {
            encryptionKey = (encryptionKey * publicKeys[1]) % 20201227
        }
        return encryptionKey
    }

    override suspend fun solveTask2(): Any {
        return "no task"
    }
}