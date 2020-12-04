package io.github.kmakma.adventofcode.y2019

import io.github.kmakma.adventofcode.y2019.utils.BreakoutGame
import io.github.kmakma.adventofcode.y2019.utils.IntcodeComputer
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
internal class Y2019Day13 : Y2019Day(13, "Care Package") {
    private lateinit var intcodeProgram: List<Long>
    override fun initializeDay() {
        intcodeProgram = inputAsIntcodeProgram()
    }

    override suspend fun solveTask1(): Int {
        return IntcodeComputer(intcodeProgram)
            .run()
            .output()
            .filterIndexed { index, _ -> index % 3 == 2 }
            .count { it == 2L }
    }

    override suspend fun solveTask2(): Int {
        return BreakoutGame(intcodeProgram).win().score
//        return BreakoutGame(intcodeProgram).play().score
    }
}