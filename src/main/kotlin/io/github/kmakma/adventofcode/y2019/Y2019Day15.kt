package io.github.kmakma.adventofcode.y2019

import io.github.kmakma.adventofcode.y2019.utils.RepairDroid
import kotlin.time.ExperimentalTime

@ExperimentalTime
internal class Y2019Day15 : Y2019Day(15, "Oxygen System") {
    private lateinit var repairDroid:RepairDroid

    override fun initializeDay() {
        repairDroid = RepairDroid(inputAsIntcodeProgram()).apply { activate() }
    }

    override suspend fun solveTask1(): Int {
        return repairDroid.distanceToO2System()
    }

    override suspend fun solveTask2(): Int {
        return repairDroid.timeToFillWithO2()
    }
}