package io.github.kmakma.adventofcode.y2019

import io.github.kmakma.adventofcode.y2019.utils.IntcodeComputerV2
import kotlin.time.ExperimentalTime

@ExperimentalTime
internal class Y2019Day19 : Y2019Day(19, "Tractor Beam") {
    private lateinit var intcodeProgram: List<Long>

    override fun initializeDay() {
        intcodeProgram = inputAsIntcodeProgram()
    }

    override suspend fun solveTask1(): Int {
        with(IntcodeComputerV2(intcodeProgram)) {
            for (y in 0L..49L) {
                for (x in 0L..49L) {
                    this.runWith(x)
                    this.runWith(y)
                    this.resetProgram()
                }
            }
            return this.output.count { it == 1L }
        }
    }

    override suspend fun solveTask2(): Long {
        with(IntcodeComputerV2(intcodeProgram)) {
            return findCoordinatesOfSantaSquare(this)
        }
    }

    private fun findCoordinatesOfSantaSquare(intPC: IntcodeComputerV2): Long {
        var lastX = 500L
        var lastY = 1000L
        // find first pull coordinates with y=100
        do {
            lastX++
        } while (intPC.runValues(lastX, lastY) != 1)
        // while on coordinates across diagonal of to -x, +y is without pull
        while (!(lastX >= 99L && intPC.runValues(lastX + 99, lastY - 99) == 1)) {
            when {
                intPC.runValues(lastX, lastY+1) == 1 -> lastY++
                intPC.runValues(lastX + 1, lastY + 1) == 1 -> {
                    lastX++
                    lastY++
                }
                intPC.runValues(lastX+1, lastY) == 1 -> lastX++
                else -> error("unexpected values at ($lastX, $lastY)")
            }
        }
        return lastX * 10000 + (lastY-99) // == coordinates
    }

}

private fun IntcodeComputerV2.runValues(x: Long, y: Long): Int {
    runWith(x)
    runWith(y)
    resetProgram()
    return nextOutput().toInt()
}
