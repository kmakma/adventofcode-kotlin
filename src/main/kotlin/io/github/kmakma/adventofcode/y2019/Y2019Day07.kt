package io.github.kmakma.adventofcode.y2019

import io.github.kmakma.adventofcode.utils.allPermutations
import io.github.kmakma.adventofcode.y2019.utils.ComputerNetwork
import io.github.kmakma.adventofcode.y2019.utils.ComputerNetwork.NetworkMode
import io.github.kmakma.adventofcode.y2019.utils.ComputerNetwork.NetworkMode.LOOP
import io.github.kmakma.adventofcode.y2019.utils.ComputerNetwork.NetworkMode.SINGLE
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import kotlin.system.measureTimeMillis

@ExperimentalCoroutinesApi
internal class Y2019Day07 : Y2019Day(
    7,
    "t1"
) {
    private lateinit var initialProgram: List<Long>

    override fun solve() {
        runBlocking { solveB() }
    }

    private suspend fun solveB() = coroutineScope {
        val timeTotal = measureTimeMillis {
            val timeInit = measureTimeMillis { initialProgram = inputAsIntcodeProgram() }
            val timeTask1 = async { measureTimeMillis { resultTask1 = solveT1() } }
            val timeTask2 = async { measureTimeMillis { resultTask2 = solveT2() } }
            println("timeInit:${timeInit},timeTask1:${timeTask1.await()}, timeTask2:${timeTask2.await()}")
        }
        println("timeTotal:$timeTotal")
    }

    private suspend fun solveT1(): Long {
        val listOfBaseValues = (0L..4L).toList().allPermutations()
        return calculateMaxOutputSignal(listOfBaseValues, SINGLE)
    }

    private suspend fun solveT2(): Long {
        val listOfBaseValues = (5L..9L).toList().allPermutations()
        return calculateMaxOutputSignal(listOfBaseValues, LOOP)
    }

    private suspend fun calculateMaxOutputSignal(listOfBaseValues: List<List<Long>>, mode: NetworkMode): Long {
        var maxOutput = 0L
        for (inputs in listOfBaseValues) {
            val output = calculateFinalOutput(inputs, mode)
            if (output > maxOutput) {
                maxOutput = output
            }
        }
        return maxOutput
    }

    private suspend fun calculateFinalOutput(baseValues: List<Long>, computerNetworkMode: NetworkMode): Long {
        val computerNetwork = ComputerNetwork
            .buildEnvironment(baseValues.size, initialProgram, 0, baseValues, computerNetworkMode)
        return computerNetwork.run().lastOutput().last() // FIXME await for run
    }

    override fun getInput(): List<String> {
        return firstCsvLineToList()
    }
}