package io.github.kmakma.adventofcode.y2019

class Y2019Day02 : Y2019Day(
    2,
    "Result (index = 0) for intcode program with set parameter:",
    "Parameter for intcode program with set target result:"
) {
    private lateinit var intcodeComputer: IntcodeComputer

    override fun solve() {
        intcodeComputer = IntcodeComputer.parse(getInput())
        resultTask1 = restoreProgramStatus()
        resultTask2 = completeGravityAssist()
    }

    private fun completeGravityAssist(): Int? {
        return intcodeComputer.calculateInputForResult(19690720)
    }

    private fun restoreProgramStatus(): Int {
        return intcodeComputer.run(12, 2).result()
    }

    override fun getInput(): String {
        return linesToList().first()
    }
}