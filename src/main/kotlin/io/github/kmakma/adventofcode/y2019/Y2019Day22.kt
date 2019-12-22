package io.github.kmakma.adventofcode.y2019

import io.github.kmakma.adventofcode.y2019.utils.CardShuffler
import kotlin.time.ExperimentalTime

@ExperimentalTime
internal class Y2019Day22 : Y2019Day(22, "Slam Shuffle") {
    private lateinit var inputLines: List<String>
    override fun initializeDay() {
        inputLines = inputInStringLines()
    }

    override suspend fun solveTask1(): Int {
        with(CardShuffler(inputLines)) {
            shuffle()
            return indexOf(2019)
        }
    }

    override suspend fun solveTask2(): Any? {
        return null
//        TODO("not implemented")
    }

}
