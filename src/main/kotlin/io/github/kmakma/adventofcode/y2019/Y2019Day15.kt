package io.github.kmakma.adventofcode.y2019

import io.github.kmakma.adventofcode.y2019.utils.RepairDroid
import kotlin.time.ExperimentalTime

@ExperimentalTime
internal class Y2019Day15 : Y2019Day(15, "Oxygen System") {
    private lateinit var intcodeProgram: List<Long>
    override fun initializeDay() {
        intcodeProgram = inputAsIntcodeProgram()
    }

    override suspend fun solveTask1(): Any? {
        RepairDroid(intcodeProgram).activate()
        return null
    }

    override suspend fun solveTask2(): Any? {
        return null
    }
}