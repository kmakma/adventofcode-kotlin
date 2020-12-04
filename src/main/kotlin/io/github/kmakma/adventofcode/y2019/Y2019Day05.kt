package io.github.kmakma.adventofcode.y2019

import io.github.kmakma.adventofcode.y2019.utils.IntcodeComputer
import kotlinx.coroutines.ExperimentalCoroutinesApi

internal class Y2019Day05 : Y2019Day(5, "Sunny with a Chance of Asteroids") {
    private lateinit var intcodeProgram: List<Long>

    override fun initializeDay() {
        intcodeProgram = inputAsIntcodeProgram()
    }

    @ExperimentalCoroutinesApi
    override suspend fun solveTask1(): Long {
        val intcodeComputer = IntcodeComputer.Builder(intcodeProgram).input(listOf(1L)).build()
        return intcodeComputer.run().output().last()
    }

    override suspend fun solveTask2(): Long {
        val intcodeComputer = IntcodeComputer.Builder(intcodeProgram).input(listOf(5L)).build()
        return intcodeComputer.run().output().last()
    }

}