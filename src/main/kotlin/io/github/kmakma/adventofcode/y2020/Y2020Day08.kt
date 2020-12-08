package io.github.kmakma.adventofcode.y2020

import io.github.kmakma.adventofcode.utils.Day

fun main() {
    Y2020Day08().solveAndPrint()
}

class Y2020Day08 : Day(2020, 8, "Handheld Halting") {

    private lateinit var inputList: List<String>
    override fun initializeDay() {
        inputList = inputInStringLines()
    }

    override suspend fun solveTask1(): Any? {
        return executeProgram(inputList)
    }

    private fun executeProgram(program: List<String>): Int {
        return programResult(program).first
    }

    private fun executeProgramWithCheck(program: List<String>): Int? {
        val pResult = programResult(program)
        if (pResult.second in program.indices) {
            return null
        }
        return pResult.first
    }

    private fun programResult(program: List<String>): Pair<Int, Int> {
        var acc = 0
        val executed = mutableSetOf<Int>()
        var i = 0
        while (!executed.contains(i) && i in program.indices) {
            val (op, off) = program[i].split(" ")
            executed.add(i)
            when (op) {
                "nop" -> i++
                "acc" -> {
                    i++
                    acc += off.toInt()
                }
                "jmp" -> i += off.toInt()
            }
        }
        return Pair(acc, i)
    }

    override suspend fun solveTask2(): Any? {
        for (i in inputList.indices) {
            val (op, off) = inputList[i].split(" ")
            var output: Int? = null
            when (op) {
                "nop" -> {
                    output = executeProgramWithCheck(inputList.toMutableList().apply { this[i] = "jmp $off" })
                }
                "jmp" -> {
                    output = executeProgramWithCheck(inputList.toMutableList().apply { this[i] = "nop $off" })
                }
            }
            if (output != null) {
                return output
            }
        }
        return null
    }
}