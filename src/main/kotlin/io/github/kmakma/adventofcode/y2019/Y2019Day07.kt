package io.github.kmakma.adventofcode.y2019

import io.github.kmakma.adventofcode.utils.allOrders
import io.github.kmakma.adventofcode.y2019.utils.DeprecatingIntcodeComputer
import io.github.kmakma.adventofcode.y2019.utils.IntcodeComputer
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking

class Y2019Day07 : Y2019Day(
    7,
    "t1",
    "t2"
) {
    private lateinit var initialProgram: List<Int>

    override fun solve() {
        runBlocking { solveB() }
    }

    private suspend fun solveB() = coroutineScope {
        initialProgram = getInput().map { it.toInt() }
        resultTask1 = solveT1()
        resultTask2 = solveT2()
    }

    private fun solveT2(): Int {

        return 0
    }

    private suspend fun solveT1(): Int {
        val listOfInputOrders = (0..4).toList().allOrders()
        var maxOutputSignal = 0
        for (inputs in listOfInputOrders) {
            val outputSignal = calculateOutputSignal(inputs)
            if (outputSignal > maxOutputSignal) {
                maxOutputSignal = outputSignal
            }
        }
        return maxOutputSignal
    }

    private suspend fun calculateOutputSignal(inputs: List<Int>): Int {
        var output = 0
        for (input in inputs) {
            println("starting deprecated comp with input: ($input, $output)")
            DeprecatingIntcodeComputer(initialProgram).runInput(listOf(input, output))
            println("done.")

            val computer = IntcodeComputer.Builder(initialProgram).input(listOf(input, output)).build()
            println("starting new comp with input: ($input, $output)")
            computer.run()
            println("done.")
            output = computer.output().last()
        }
        return output
    }

    override fun getInput(): List<String> {
        return firstCsvLineToList()
    }
}