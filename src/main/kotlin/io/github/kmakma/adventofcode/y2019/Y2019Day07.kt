package io.github.kmakma.adventofcode.y2019

import io.github.kmakma.adventofcode.utils.allOrders
import io.github.kmakma.adventofcode.y2019.utils.ComputerNetwork
import io.github.kmakma.adventofcode.y2019.utils.ComputerNetwork.NetworkMode
import io.github.kmakma.adventofcode.y2019.utils.ComputerNetwork.NetworkMode.LOOP
import io.github.kmakma.adventofcode.y2019.utils.ComputerNetwork.NetworkMode.SINGLE
import io.github.kmakma.adventofcode.y2019.utils.IntcodeComputer
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking

class Y2019Day07 : Y2019Day(
    7,
    "t1",
    "t2"
) {
    private lateinit var initialProgram: List<Long>

    override fun solve() {
        runBlocking { solveB() }
    }

    private suspend fun solveB() = coroutineScope {
        initialProgram = inputAsIntcodeProgram()
        resultTask1 = solveT1()
        resultTask2 = solveT2()
    }

    private suspend fun solveT2(): Long {
        val listOfBaseValues = (5L..9L).toList().allOrders()
        var maxOutputSignal = 0L
        for (inputs in listOfBaseValues) {
            val outputSignal = calculateOutputSignal(inputs, LOOP)
            if (outputSignal > maxOutputSignal) {
                maxOutputSignal = outputSignal
            }
        }
        return maxOutputSignal
    }

    private suspend fun solveT1(): Long {
        val listOfInputOrders = (0L..4L).toList().allOrders()
        var maxOutputSignal = 0L
        for (inputs in listOfInputOrders) {
            val outputSignal = calculateOutputSignal(inputs, SINGLE)
            if (outputSignal > maxOutputSignal) {
                maxOutputSignal = outputSignal
            }
        }
        return maxOutputSignal
    }

    private suspend fun calculateOutputSignal(baseValues: List<Long>, computerNetworkMode: NetworkMode): Long {
        var output = 0L
        val computerNetwork = ComputerNetwork
            .buildEnvironment(baseValues.size, initialProgram, 0, baseValues, computerNetworkMode)
        val resultA = computerNetwork.run().result

        for (input in baseValues) {
            val computer = IntcodeComputer.Builder(initialProgram).input(listOf<Long>(input, output)).build()
            computer.run()
            output = computer.output().last()
        }
        return resultA
//        return output
    }

    override fun getInput(): List<String> {
        return firstCsvLineToList()
    }
}