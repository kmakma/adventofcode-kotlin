package io.github.kmakma.adventofcode.y2019

import io.github.kmakma.adventofcode.utils.listInAllOrders
import io.github.kmakma.adventofcode.y2019.utils.ComputerVersion.JUMPS_COMPARISONS
import io.github.kmakma.adventofcode.y2019.utils.IntcodeComputer

class Y2019Day07 : Y2019Day(
    7,
    "t1",
    "t2"
) {
    private lateinit var intcodeComputer: IntcodeComputer

    override fun solve() {
        intcodeComputer = IntcodeComputer.parse(getInput(), JUMPS_COMPARISONS)
        resultTask1 = solveT1()
        resultTask2 = solveT2()
    }

    private fun solveT2(): Any? {
        return null
    }

    private fun solveT1(): Int {
        val listOfInputOrders = listInAllOrders((0..4).toList())
        var maxOutputSignal = 0
        for (inputs in listOfInputOrders) {
            val outputSignal = calculateOutputSignal(inputs)
            if (outputSignal > maxOutputSignal) {
                maxOutputSignal = outputSignal
            }
        }
        return maxOutputSignal
    }

    private fun calculateOutputSignal(inputs: List<Int>): Int {
        var output = 0
        for (input in inputs) {
            output = intcodeComputer.runInput(listOf(input, output)).last()
        }
        return output
    }

    override fun getInput(): List<String> {
        return firstCsvLineToList()
    }
}