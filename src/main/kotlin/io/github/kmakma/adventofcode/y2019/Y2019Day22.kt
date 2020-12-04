package io.github.kmakma.adventofcode.y2019

import io.github.kmakma.adventofcode.y2019.utils.CardShuffler

internal class Y2019Day22 : Y2019Day(22, "Slam Shuffle") {
    private lateinit var inputLines: List<String>
    override fun initializeDay() {
        inputLines = inputInStringLines()
    }

    override suspend fun solveTask1(): Int {
        with(CardShuffler(inputLines, 10007)) {
            shuffle()
            return indexOf(2019)
        }
    }

    override suspend fun solveTask2(): Any? {
        for (i in 10..100 step 10) {
            with(CardShuffler(inputLines, i)) {
                shuffle()
                println(deck.contentToString())
            }
        }
        return null
//        TODO("not implemented")
    }

}
