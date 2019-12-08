package io.github.kmakma.adventofcode.y2019.utils

/**
 * Network of [IntcodeComputer]s, for larger/linked computations.
 *
 * Build with [buildEnvironment]
 */
internal class ComputerNetwork private constructor(
    val intcodeComputerList: List<IntcodeComputer>,
    val computerIOList: List<ComputerIO>
) {


    /**
     * Modes for [ComputerNetwork]; [SINGLE], [LOOP]
     */
    internal enum class NetworkModes {
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
         * Depending on the [modes], differently linked, compare [NetworkModes].
         */
        internal fun buildEnvironment(
            size: Int,
            initialComputerProgram: List<Int>,
            firstInput: Int,
            ioBaseValues: List<Int>,
            modes: NetworkModes
        ) {//: ComputerNetwork {

        }
    }
}