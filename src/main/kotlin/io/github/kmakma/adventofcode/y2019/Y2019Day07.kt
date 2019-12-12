package io.github.kmakma.adventofcode.y2019

import io.github.kmakma.adventofcode.utils.allPermutations
import io.github.kmakma.adventofcode.y2019.utils.ComputerNetwork
import io.github.kmakma.adventofcode.y2019.utils.ComputerNetwork.NetworkMode
import io.github.kmakma.adventofcode.y2019.utils.ComputerNetwork.NetworkMode.LOOP
import io.github.kmakma.adventofcode.y2019.utils.ComputerNetwork.NetworkMode.SINGLE
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlin.time.ExperimentalTime

@ExperimentalTime
@ExperimentalCoroutinesApi
internal class Y2019Day07 : Y2019Day(7, "Amplification Circuit") {
    private lateinit var initialProgram: List<Long>

    override fun initializeDay() {
        initialProgram = inputAsIntcodeProgram()
    }

    override suspend fun solveTask1(): Long {
        val listOfBaseValues = (0L..4L).toList().allPermutations()
        return calculateMaxOutputSignal(listOfBaseValues, SINGLE)
    }

    override suspend fun solveTask2(): Any? {
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
        return computerNetwork.run().lastOutput().last()
    }
}