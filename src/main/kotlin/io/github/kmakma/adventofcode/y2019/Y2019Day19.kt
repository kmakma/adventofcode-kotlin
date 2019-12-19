package io.github.kmakma.adventofcode.y2019

import io.github.kmakma.adventofcode.y2019.utils.IntcodeComputerV2
import kotlin.time.ExperimentalTime

@ExperimentalTime
internal class Y2019Day19 : Y2019Day(19, "Tractor Beam") {
    private lateinit var intcodeProgram: List<Long>

    override fun initializeDay() {
        intcodeProgram = inputAsIntcodeProgram()
    }

    override suspend fun solveTask1(): Any? {
        with(IntcodeComputerV2(intcodeProgram)) {
            for (y in 0L..49L) {
                for (x in 0L..49L) {
                    this.run()
                    this.runWith(x)
                    this.runWith(y)
                    this.resetProgram()
//                    print(this.nextOutput())
                }
//                println()
            }
            return this.output.count { it == 1L }
        }
    }

    override suspend fun solveTask2(): Any? {
        return foo()
    }


    private fun foo(): Long {
        with(IntcodeComputerV2(intcodeProgram)) {
            with(findDiagonal(this)) {
                return (this.first) * 10000 + (this.second)
            }
        }
    }

    private fun findDiagonal(intPC: IntcodeComputerV2): Pair<Long, Long> {
        var lastX = 0L
        var lastY = 100L
        do {
            lastX++
        } while (intPC.runValues(lastX, lastY) != 1)



        while (true) {
            if (lastX >= 99L && intPC.runValues(lastX - 99, lastY + 99) == 1) {
                return Pair(lastX-99, lastY)
            }
            when {
                intPC.runValues(lastX + 1, lastY) == 1 -> lastX++
                intPC.runValues(lastX + 1, lastY + 1) == 1 -> {
                    lastX++
                    lastY++
                }
                intPC.runValues(lastX, lastY + 1) == 1 -> lastY++
                else -> error("unexpected values at ($lastX, $lastY)")
            }
        }
    }

}

private fun IntcodeComputerV2.runValues(x: Long, y: Long): Int {
    runWith(x)
    runWith(y)
    resetProgram()
    return nextOutput().toInt()
}
