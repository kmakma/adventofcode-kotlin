package io.github.kmakma.adventofcode.y2020

import io.github.kmakma.adventofcode.utils.Day

fun main() {
    Y2020Day10().solveAndPrint()
}

class Y2020Day10 : Day(2020, 10, "Adapter Array") {
    private lateinit var sortedInput: List<Int>

    override fun initializeDay() {
        sortedInput = inputInStringLines().map { it.toInt() }.sorted()
    }

    override suspend fun solveTask1(): Any? {
        val extendedInput = sortedInput.toMutableList().apply {
            this.add(0, 0)
            this.add(this.last() + 3)
        }
        val joltDiffs = arrayOf(0, 0, 0, 0)
        var current = extendedInput.removeFirst()
        while (extendedInput.isNotEmpty()) {
            val next = extendedInput.removeFirst()
            joltDiffs[next - current]++
            current = next
        }
        return joltDiffs[1] * joltDiffs[3]
    }

    override suspend fun solveTask2(): Any? {
        var current = sortedInput.last()
        val inputMap = mutableMapOf<Int, Long>()
        sortedInput.forEach { inputMap[it] = 0 }
        inputMap[0] = 0
        inputMap[current + 3] = 1
        while (current >= 0) {
            val currentValue = inputMap[current]
            if (currentValue != null) {
                inputMap[current] = currentValue +
                        inputMap.getOrDefault(current + 1, 0) +
                        inputMap.getOrDefault(current + 2, 0) +
                        inputMap.getOrDefault(current + 3, 0)
            }
            current--
        }
        return inputMap[0]
    }
}