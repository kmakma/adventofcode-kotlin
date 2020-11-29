package io.github.kmakma.adventofcode.y2019

import io.github.kmakma.adventofcode.y2019.utils.JupiterMoonSimulation
import kotlin.time.ExperimentalTime

@ExperimentalTime
internal class Y2019Day12 : Y2019Day(12, "The N-Body Problem") {
    private lateinit var moons: List<String>
    override fun initializeDay() {
        moons = inputInStringLines()
    }

    override suspend fun solveTask1(): Any? {
        return JupiterMoonSimulation.calculateTotalEnergy(moons)
    }

    override suspend fun solveTask2(): Any? {
        return JupiterMoonSimulation.findRepetition(moons)
    }
}