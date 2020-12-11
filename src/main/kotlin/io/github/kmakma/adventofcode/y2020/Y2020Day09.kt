package io.github.kmakma.adventofcode.y2020

import io.github.kmakma.adventofcode.utils.Day

fun main() {
    Y2020Day09().solveAndPrint()
}

class Y2020Day09 : Day(2020, 9, "Encoding Error") {
    private lateinit var inputList: List<Long>

    override fun initializeDay() {
        inputList = inputInStringLines().map { it.toLong() }
    }

    override suspend fun solveTask1(): Any? {
        var currentIndex = 25
        while (currentIndex <= inputList.lastIndex) {
            if (validNumber(currentIndex)) {
                currentIndex++
            } else {
                return inputList[currentIndex]
            }
        }
        return null
    }

    private fun validNumber(index: Int): Boolean {
        for (i in (index - 25) until (index - 1)) {
            for (j in (i + 1) until index) {
                if (inputList[index] == inputList[i] + inputList[j]) return true
            }
        }
        return false
    }

    override suspend fun solveTask2(): Any? {
        var currentIndex = 25
        var invalidIndex = 0
        while (currentIndex <= inputList.lastIndex && invalidIndex == 0) {
            if (validNumber(currentIndex)) {
                currentIndex++
            } else {
                invalidIndex = currentIndex
            }
        }
        val contiguousSet = findContiguousSet(invalidIndex)
        if (contiguousSet == null || contiguousSet.size < 2) return false
        return contiguousSet.maxOrNull()!! + contiguousSet.minOrNull()!!
    }

    private fun findContiguousSet(index: Int): List<Long>? {
        val required = inputList[index]
        for (i in 0 until inputList.lastIndex) {
            var sum = inputList[i]
            for (j in (i + 1)..inputList.lastIndex) {
                sum += inputList[j]
                if (sum == required) {
                    return inputList.subList(i, j + 1)
                } else if (sum > required) {
                    break
                }
            }
        }
        return null
    }
}