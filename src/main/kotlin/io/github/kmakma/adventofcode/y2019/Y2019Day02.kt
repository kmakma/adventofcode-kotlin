package io.github.kmakma.adventofcode.y2019

import io.github.kmakma.adventofcode.y2019.utils.DeprecatingIntcodeComputer

class Y2019Day02 : Y2019Day(
    2,
    "Result (index = 0) for intcode program with set parameter:",
    "Parameter for intcode program with set target result:"
) {
    private lateinit var deprecatingIntcodeComputer: DeprecatingIntcodeComputer

    override fun solve() {
        deprecatingIntcodeComputer = DeprecatingIntcodeComputer.parse(getInput())
        resultTask1 = restoreProgramStatus()
        resultTask2 = completeGravityAssist()
    }

    private fun completeGravityAssist(): Int? {
        return deprecatingIntcodeComputer.calculateInputForResult(19690720)
    }

    private fun restoreProgramStatus(): Int {
        return deprecatingIntcodeComputer.run(noun = 12, verb = 2).result()
    }

    override fun getInput(): String {
        return linesToList().first()
    }
}