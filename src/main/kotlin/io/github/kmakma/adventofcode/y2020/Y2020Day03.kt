package io.github.kmakma.adventofcode.y2020

import io.github.kmakma.adventofcode.utils.Day
import kotlin.time.ExperimentalTime

@ExperimentalTime
class Y2020Day03 : Day(2020, 3, "Toboggan Trajectory") {
    private lateinit var inputSlope: List<List<Char>>
    override fun initializeDay() {
        inputSlope = inputInCharLines()
    }

    override suspend fun solveTask1(): Any? {
        var trees = 0
        var right = 0
        for (line in inputSlope) {
            println(right)
            if (line[right] == '#') trees++
            right = normalizeRight(right + 3)
        }
        return trees
    }

    override suspend fun solveTask2(): Any? {
        var treesMultiplied = foo(1)
        treesMultiplied *= foo(3)
        treesMultiplied *= foo(5)
        treesMultiplied *= foo(7)
        treesMultiplied *= foo(1, 2)
        return treesMultiplied
    }

    private fun foo(rightMove: Int): Int {
        var trees = 0
        var right = 0
        for (line in inputSlope) {
            if (line[right] == '#') trees++
            right = normalizeRight(right + rightMove)
        }
        return trees
    }

    private fun foo(rightMove: Int, down: Int = 1): Int {
        var trees = 0
        var right = 0
        for (i in inputSlope.indices) {
            if (i % 2 == 0) {
                if (inputSlope[i][right] == '#') trees++
                right = normalizeRight(right + rightMove)
            }
        }
        return trees
    }

    private fun normalizeRight(oldRight: Int): Int {
        val width = inputSlope[0].size
        if (oldRight >= width) {
            return oldRight - width
        }
        return oldRight
    }
}

@ExperimentalTime
fun main() {
    Y2020Day03().solveAndPrint()
}