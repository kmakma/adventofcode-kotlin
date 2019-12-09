package io.github.kmakma.adventofcode.y2019

import io.github.kmakma.adventofcode.y2019.utils.IntcodeComputer
import kotlinx.coroutines.runBlocking

class Y2019Day09 : Y2019Day(9, "T1", "T2") {

    private lateinit var intcodeProgram: List<Long>
    override fun solve() {
        intcodeProgram = getInput()
        resultTask1 = solveT1()
        resultTask2 = solveT2()
    }

    private fun solveT1(): Long {
        val computer = IntcodeComputer.Builder(intcodeProgram).input(listOf(1L)).build()
        var output = 0L
        runBlocking { output = computer.run().output().last() }
        return output
    }

    private fun solveT2():Long {
        val computer=IntcodeComputer.Builder(intcodeProgram).input(listOf(2L)).build()
        var output = 0L
        runBlocking { output = computer.run().output().last() }
        return output
    }

    override fun getInput(): List<Long> {
        return inputAsIntcodeProgram()
    }
}