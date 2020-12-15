package io.github.kmakma.adventofcode.y2020

import io.github.kmakma.adventofcode.utils.Day

fun main() {
    Y2020Day15().solveAndPrint()
}

class Y2020Day15 : Day(2020, 15, "Rambunctious Recitation") {

    private lateinit var input: List<Int>

    override fun initializeDay() {
        input = inputInOneLine().split(",").map { it.toInt() }


    }

    override suspend fun solveTask1(): Any? {
        val spoken = input.toMutableList()
        while (spoken.size < 2020) {
            val scndLastIndex = spoken.scndLastIndexOf(spoken.last())
            if (scndLastIndex >= 0) {
                spoken.add(spoken.lastIndex - scndLastIndex)
            } else {
                spoken.add(0)
            }
        }
        return spoken[2019]
    }

    override suspend fun solveTask2(): Any? {
        val spoken = input.mapIndexed { index, value -> value to mutableListOf(index) }.toMap().toMutableMap()
        var value = input.last()
        var index = input.lastIndex
        while (index < 30000000 - 1) {
            index++
            val indices = spoken[value]
            value = when {
                indices == null -> throw IllegalStateException()
                indices.size <= 1 -> 0
                else -> indices.last() - indices[indices.size - 2]
            }
            spoken.putIfAbsent(value, mutableListOf(index))?.add(index)
        }
        return value
    }
}

private fun <E> MutableList<E>.scndLastIndexOf(o: E): Int {
    for (i in size - 2 downTo 0) {
        if (o == get(i)) return i
    }
    return -1
}
