package io.github.kmakma.adventofcode.y2020

import io.github.kmakma.adventofcode.utils.Day

fun main() {
    Y2020Day01().solveAndPrint()
}

class Y2020Day01 : Day(2020, 1, "Report Repair") {
    private lateinit var inputList: List<Int>

    override fun initializeDay() {
        inputList = inputInStringLines().map { it.toInt() }
    }

    override suspend fun solveTask1(): Any? {
        for (first in inputList) {
            for (second in inputList) {
                if (first + second == 2020) {
                    return first * second
                }
            }
        }
        return null
    }

    override suspend fun solveTask2(): Any? {
        for (first in inputList) {
            for (second in inputList) {
                for (third in inputList) {
                    if (first + second + third == 2020) {
                        return first * second * third
                    }
                }
            }
        }
        return null
    }

}
