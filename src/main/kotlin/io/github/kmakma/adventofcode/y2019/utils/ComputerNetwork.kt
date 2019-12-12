package io.github.kmakma.adventofcode.y2019.utils

import io.github.kmakma.adventofcode.y2019.utils.ComputerNetwork.Companion.buildEnvironment
import io.github.kmakma.adventofcode.y2019.utils.ComputerNetwork.NetworkMode.LOOP
import io.github.kmakma.adventofcode.y2019.utils.ComputerNetwork.NetworkMode.SINGLE
import io.github.kmakma.adventofcode.y2019.utils.ComputerStatus.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

/**
 * Network of [IntcodeComputer]s, for larger/linked computations.
 *
 * Build with [buildEnvironment]
 */
@ExperimentalCoroutinesApi
internal class ComputerNetwork private constructor(private val intcodeComputers: List<IntcodeComputer>) {
    private var networkStatus = IDLE

    @ExperimentalCoroutinesApi
    internal suspend fun run(): ComputerNetwork {
        when (networkStatus) {
            IDLE -> networkStatus = RUNNING
            RUNNING, TERMINATING -> error("ComputerNetwork is already running!")
            TERMINATED -> return this@ComputerNetwork
        }
        runComputers()
        networkStatus = TERMINATED
        return this@ComputerNetwork
    }

    private suspend fun runComputers() = coroutineScope {
        for (i in intcodeComputers.indices) { // TODO make list of deferred
            launch { intcodeComputers[i].run() }
        }
    }

    internal fun lastOutput(): List<Long> {
        when (networkStatus) {
            IDLE -> error("Output not possible with a ComputerNetwork that didn't run yet!")
            RUNNING -> error("Output not possible on running ComputerNetwork")
            TERMINATING -> error("Output not possible on terminating ComputerNetwork")
            TERMINATED -> { // proceed
            }
        }
        return intcodeComputers.last().output()
    }

    /**
     * Modes for [ComputerNetwork]; [SINGLE], [LOOP]
     */
    internal enum class NetworkMode {
        /**
         * IN - Comp - IO - Comp - .... - Comp - OUT
         */
        SINGLE,
        /**
         * IO1 - Comp1 - IO2 - Comp2 - IO2 -  ... - CompN - IO1
         */
        LOOP
    }

    companion object {
        /**
         * Build a [ComputerNetwork] with [size] x [IntcodeComputer]s.
         *
         * Computer in/outputs are linked via [ComputerIO] with a single initial value for each and
         * one input value [firstInput] for the first one. Initial values are prrovided by [ioBaseValues],
         * therefore [ioBaseValues] must not be bigger than [size] (but might be smaller).
         *
         * Depending on the [mode], differently linked, compare [NetworkMode].
         */
        internal fun buildEnvironment(
            size: Int,
            initialComputerProgram: List<Long>,
            firstInput: Long?,
            ioBaseValues: List<Long>,
            mode: NetworkMode
        ): ComputerNetwork {
            val computerIOs = buildComputerIOs(size, ioBaseValues, firstInput, mode)
            val computers = buildComputers(size, initialComputerProgram, computerIOs, mode)
            return ComputerNetwork(computers)
        }

        private fun buildComputerIOs(
            number: Int,
            ioBaseValues: List<Long>,
            firstInput: Long?,
            mode: NetworkMode
        ): List<ComputerIO> {
            if (ioBaseValues.size > number) {
                error("more base input values than computers/IOs")
            }
            val computerIOs = mutableListOf<ComputerIO>()
            // first computerIO
            if (firstInput == null) {
                computerIOs.add(ComputerIO(listOf(ioBaseValues[0])))
            } else {
                computerIOs.add(ComputerIO(listOf(ioBaseValues[0], firstInput)))
            }
            // remaining computerIOs
            val lastIndex = when (mode) {
                SINGLE -> number
                LOOP -> number - 1
            }
            for (i in 1..lastIndex) {
                if (i in ioBaseValues.indices) {
                    computerIOs.add(ComputerIO(listOf(ioBaseValues[i])))
                } else {
                    computerIOs.add(ComputerIO())
                }
            }
            return computerIOs
        }

        private fun buildComputers(
            number: Int,
            initialComputerProgram: List<Long>,
            computerIOs: List<ComputerIO>,
            mode: NetworkMode
        ): List<IntcodeComputer> {
            val computers = mutableListOf<IntcodeComputer>()
            val builder = IntcodeComputer.Builder(initialComputerProgram)
            val itEnd = when (mode) {
                SINGLE -> number
                LOOP -> number - 1
            }
            // build computers, except last one if mode is LOOP
            for (i in 0 until itEnd) {
                computers.add(builder.input(computerIOs[i]).output(computerIOs[i + 1]).build())
            }
            // if LOOP, than shall be true: lastComputer.output == firstComputer.input
            if (mode == LOOP) {
                computers.add(builder.input(computerIOs[number - 1]).output(computerIOs.first()).build())
            }
            return computers
        }
    }
}