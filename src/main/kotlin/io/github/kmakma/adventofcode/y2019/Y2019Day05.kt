package io.github.kmakma.adventofcode.y2019

class Y2019Day05 : Y2019Day(
    5,
    "Diagnostic code for air conditioner (systemID=1):",
    "Diagnostic code for thermal radiator (systemID=5)"
) {

    private lateinit var intcodeComputer: IntcodeComputer


    override fun solve() {
        intcodeComputer = IntcodeComputer.parse(getInput(), ComputerVesion.IO)
        resultTask1 = intcodeComputer.runForSystem(1).last()
//        intcodeComputer = IntcodeComputer.parse(getInput(), ComputerVesion.JUMP_COMPARISONS)
//        println("task2" + intcodeComputer.runForSystem(5).last())
//        TODO("not implemented")
    }

    override fun getInput(): String {
        return linesToList().first()
    }

}