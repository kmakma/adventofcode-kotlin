package io.github.kmakma.adventofcode.y2019

import io.github.kmakma.adventofcode.y2019.utils.IntcodeComputer
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlin.time.ExperimentalTime

@ExperimentalTime
@ExperimentalCoroutinesApi
internal class Y2019Day09 : Y2019Day(9, "Sensor Boost") {
    private lateinit var computerBuilder: IntcodeComputer.Builder

    override fun initializeDay() {
        computerBuilder = IntcodeComputer.Builder(inputAsIntcodeProgram())
    }


    // fixme!! closed before finishing?

    override suspend fun solveTask1(): Long {
        val computer = computerBuilder.input(listOf(1L)).build()
        return computer.run().output().last()
    }

    override suspend fun solveTask2(): Long {
        val computer = computerBuilder.input(listOf(2L)).build()
        return computer.run().output().last()
    }
}