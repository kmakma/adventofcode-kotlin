package io.github.kmakma.adventofcode.y2019

import io.github.kmakma.adventofcode.utils.else0
import kotlin.math.abs
import kotlin.time.ExperimentalTime

@ExperimentalTime
internal class Y2019Day16 : Y2019Day(16, "Flawed Frequency Transmission") {
    private lateinit var basePattern: List<Int>
    private lateinit var input: List<Int>
    private val realInputSizeFactor = 10000

    override fun initializeDay() {
        basePattern = listOf(0, 1, 0, -1)
        input = firstLine().map(Character::getNumericValue)
    }

    override suspend fun solveTask1(): String {
        return cleanUpSignal()
    }

    override suspend fun solveTask2(): String {
        return cleanUpRealSignal()
    }

    private fun cleanUpSignal(): String {
        val signal = phaseSignal(input)
        return signalToString(signal)
    }

    private fun cleanUpRealSignal(): String {
        val offset = input.take(7).reduce { a, b -> a * 10 + b }
        if (offset < input.size * realInputSizeFactor / 2) error("solution not applicable")
        val shortOffset = offset % input.size
        val size = input.size * realInputSizeFactor - offset // r.I.S.F. = 10000
        val signal = MutableList(size) { index -> input[(index + shortOffset) % input.size] }
        // no pattern application necessary. Pattern for all indices would result in an upper triangular matrix filled with 1
        for (phase in 1..100) {
            for (index in (size - 2) downTo 0) {
                signal[index] = abs(signal[index] + signal.getOrElse(index + 1, ::else0)) % 10
            }
        }
        return signalToString(signal)
    }

    private fun signalToString(signal: List<Int>): String {
        val sb = StringBuilder()
        signal.take(8).forEach { sb.append(it) }
        return sb.toString()
    }

    private fun phaseSignal(input: List<Int>): List<Int> {
        var signal = input
        for (i in 1..100) {
            signal = signal.mapIndexed { index, _ -> applyPattern(index, signal) }
        }
        return signal
    }


    private fun applyPattern(patternForIndex: Int, signal: List<Int>): Int {
        return signal
            .mapIndexed { index, value -> value * pattern(patternForIndex, index) }
            .sum()
            .let { abs(it) % 10 }
    }

    private fun pattern(calculatingIndex: Int, factorIndex: Int): Int {
        val patternIndex = ((factorIndex + 1) / (calculatingIndex + 1)) % 4
        return basePattern[patternIndex]
    }
}
