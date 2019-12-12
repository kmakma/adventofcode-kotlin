package io.github.kmakma.adventofcode.y2019.utils

import io.github.kmakma.adventofcode.utils.lcm

internal class JupiterMoonSimulation private constructor(private val moons: Array<IntVector>) {
    private val velocities = Array(moons.size) { IntVector(0, 0, 0) }
    private var timePassed = 0L
    private val moonStartPos = moons.copyOf()

    companion object {
        fun calculateTotalEnergy(input: List<String>): Int {
            val simulation = buildSimulation(input)
            for (i in 1..1000) {
                simulation.doTimeStep()
            }
            return simulation.totalEnergy()
        }

        fun findRepetition(input: List<String>): Long {
            val simulation = buildSimulation(input)
            return simulation.findRepetitionRate()
        }

        private fun parseMoonString(moon: String): IntVector {
            return moon.substring(1, moon.length - 1)
                .split(", ")
                .map { it.substring(2).toInt() }
                .let { IntVector(it) }
        }

        private fun buildSimulation(input: List<String>): JupiterMoonSimulation {
            val moons = input.map { parseMoonString(it) }.toTypedArray()
            return JupiterMoonSimulation(moons)
        }
    }

    private fun doTimeStep() {
        // update velocity
        updateVelocity()
        // update position
        updatePosition()
        timePassed++
    }

    private fun updateVelocity() {
        for (i in moons.indices) { // iterate through lines/moons by index
            for (moon in moons) {
                // update velocity for current line/moon, by iterating over all moons
                velocities[i] += gravityVector(moons[i], moon)
            }
        }
    }

    private fun gravityVector(from: IntVector, toward: IntVector): IntVector {
        val difference = toward - from
        return difference.sign()
    }

    private fun updatePosition() {
        for (i in moons.indices) {
            moons[i] += velocities[i]
        }
    }

    private fun totalEnergy(): Int {
        return moons.mapIndexed { i, pos -> pos.manhattanDistance * velocities[i].manhattanDistance }.sum()
    }

    private fun findRepetitionRate(): Long {
        val repetitionSize = arrayOf(-1L, -1L, -1L)
        while (repetitionSize.any { it <= 0 }) {
            doTimeStep()
            // check and maybe set
            for (valueI in moons[0].indices) {
                if (repetitionSize[valueI] > 0) continue
                val checkRep = checkRepetition(valueI)
                if (checkRep > 0)
                    repetitionSize[valueI] = checkRep
            }
        }
        return repetitionSize.reduce { a, b -> lcm(a, b) }
    }

    private fun checkRepetition(valueI: Int): Long {
        // check weather value at valueI (with valueI=0=x, =1=y,..) is a repetition
        var isRep = true
        for (moonI in moons.indices) {
            if (velocities[moonI][valueI] != 0 || moons[moonI][valueI] != moonStartPos[moonI][valueI]) {
                isRep = false
                break
            }
        }
        return if (isRep) timePassed
        else -1L
    }
}