package io.github.kmakma.adventofcode.y2019

import io.github.kmakma.adventofcode.y2019.utils.VacuumRobot

internal class Y2019Day17 : Y2019Day(17, "Set and Forget") {
    private lateinit var intcodeProgram: List<Long>
    private lateinit var manualTask2: List<List<Char>>
    override fun initializeDay() {
        intcodeProgram = inputAsIntcodeProgram()
        manualTask2 = listOf(
            listOf('A', ',', 'B', ',', 'A', ',', 'B', ',', 'C', ',', 'C', ',', 'B', ',', 'A', ',', 'B', ',', 'C'),
            listOf('L', ',', '4', ',', 'R', ',', '8', ',', 'L', ',', '6', ',', 'L', ',', '1', '0'),
            listOf('L', ',', '6', ',', 'R', ',', '8', ',', 'R', ',', '1', '0', ',', 'L', ',', '6', ',', 'L', ',', '6'),
            listOf('L', ',', '4', ',', 'L', ',', '4', ',', 'L', ',', '1', '0')
        )
    }

    override suspend fun solveTask1(): Int {
        with(VacuumRobot(intcodeProgram)) {
            return sumOfAlignmentParameters()
        }
    }

    override suspend fun solveTask2(): Long {
        with(VacuumRobot(intcodeProgram)) {
            return countDust(manualTask2)
        }
    }

}
