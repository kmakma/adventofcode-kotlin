package io.github.kmakma.adventofcode.y2020

import io.github.kmakma.adventofcode.utils.Day

fun main() {
    Y2020Day06().solveAndPrint()
}

class Y2020Day06 : Day(2020, 6, "") {
    private lateinit var inputLines: List<List<Char>>
    override fun initializeDay() {
        inputLines = inputInCharLines()
    }

    override suspend fun solveTask1(): Any? {
        var currentSet = mutableSetOf<Char>()
        var sum = 0
        for (line in inputLines) {
            if (line.isEmpty()) {
                println(currentSet.size)
                sum += currentSet.size
                currentSet = mutableSetOf()
            } else {
                currentSet.addAll(line)
            }
        }
        sum += currentSet.size
        return sum
    }

    override suspend fun solveTask2(): Any? {
        var currentList = mutableListOf<Char>()
        var sum = 0
        var counter = 0
        for (line in inputLines) {
            if (line.isEmpty()) {
                for (c in 'a'..'z') {
                    if (currentList.count { it == c } == counter) sum++
                }
                currentList = mutableListOf()
                counter = 0
            } else {
                currentList.addAll(line)
                counter++
            }
        }
        for (c in 'a'..'z') {
            if (currentList.count { it == c } == counter) sum++
        }
        return sum
    }

}
