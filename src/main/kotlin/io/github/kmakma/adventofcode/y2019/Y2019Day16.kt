package io.github.kmakma.adventofcode.y2019

import kotlin.math.abs
import kotlin.time.ExperimentalTime

@ExperimentalTime
internal class Y2019Day16 : Y2019Day(16, "lawed Frequency Transmission") {
    private lateinit var input: List<Int>
    private lateinit var patternArr: Array<List<Int>>

    override fun initializeDay() {
        input = firstLine().map(Character::getNumericValue)

        val basePattern = listOf(0, 1, 0, -1)
        patternArr = Array(input.size) { arrIndex ->
            val baseForIndex = basePattern.map { List(arrIndex + 1) { _ -> it } }.flatten() //limit to size
            val newPattern = mutableListOf<Int>()
            var bfiIterator = baseForIndex.listIterator(1)
            while (newPattern.size != input.size) {
                if (!bfiIterator.hasNext()) bfiIterator = baseForIndex.listIterator()
                newPattern.add(bfiIterator.next())
            }
            newPattern
        }
    }

    override suspend fun solveTask1(): String {
        return foo1()
    }

    override suspend fun solveTask2(): Any? {
//        TODO("not implemented")
        return null
    }

    private fun foo1(): String {
        var signal = input
        for (i in 1..100) {
            signal = signal.mapIndexed { index, _ ->
                applyPattern(index, signal)
            }
        }
        val sb = StringBuilder()
        signal.take(8).forEach { sb.append(it) }
        return sb.toString()
    }

    private fun applyPattern(index: Int, signal: List<Int>): Int {
        val pattern = patternArr[index]
        return signal
            .mapIndexed { i, value -> value * pattern[i] }
            .sum()
            .let { abs(it) % 10 }
    }

}
