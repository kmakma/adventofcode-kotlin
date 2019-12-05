package io.github.kmakma.adventofcode.y2019

class Y2019Day05 : Y2019Day(
    5,
    "Diagnostic code for air conditioner (systemID=1):",
    "Diagnostic code for thermal radiator (systemID=5)"
) {
    private lateinit var inputString: String

    override fun solve() {
        inputString = getInput()
        resultTask1 = diagnosticsOnAirConditioner()
        resultTask2 = diagnosticsOnThermalRadiator()
    }

    private fun diagnosticsOnAirConditioner(): Int {
        val intcodeComputer = IntcodeComputer.parse(inputString, ComputerVersion.IO)
        return intcodeComputer.runForSystem(1).last()
    }

    private fun diagnosticsOnThermalRadiator(): Int {
        val intcodeComputer = IntcodeComputer.parse(inputString, ComputerVersion.JUMPS_COMPARISONS)
        return intcodeComputer.runForSystem(5).last()
    }

    override fun getInput(): String {
        return linesToList().first()
    }

}