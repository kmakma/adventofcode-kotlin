package io.github.kmakma.adventofcode.y2019

import io.github.kmakma.adventofcode.y2019.utils.DeprecatingIntcodeComputer

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
        val intcodeComputer = DeprecatingIntcodeComputer.parse(inputString)
        return intcodeComputer.runInput(listOf(1)).last()
    }

    private fun diagnosticsOnThermalRadiator(): Int {
        val intcodeComputer = DeprecatingIntcodeComputer.parse(inputString)
        return intcodeComputer.runInput(listOf(5)).last()
    }

    override fun getInput(): String {
        return linesToList().first()
    }

}