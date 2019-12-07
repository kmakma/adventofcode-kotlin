package io.github.kmakma.adventofcode.y2019

import io.github.kmakma.adventofcode.y2019.utils.ComputerVersion
import io.github.kmakma.adventofcode.y2019.utils.IntcodeComputer

class Y2019Day02 : Y2019Day(
    2,
    "Result (index = 0) for intcode program with set parameter:",
    "Parameter for intcode program with set target result:"
) {
    private lateinit var intcodeComputer: IntcodeComputer

    override fun solve() {
        intcodeComputer = IntcodeComputer.parse(getInput(), ComputerVersion.BASIC)
        resultTask1 = restoreProgramStatus()
        resultTask2 = completeGravityAssist()
    }

    private fun completeGravityAssist(): Int? {
        return intcodeComputer.calculateInputForResult(19690720)
    }

    private fun restoreProgramStatus(): Int {
        return intcodeComputer.run(noun = 12, verb = 2).result()
    }

    override fun getInput(): String {
        return linesToList().first()
    }
}