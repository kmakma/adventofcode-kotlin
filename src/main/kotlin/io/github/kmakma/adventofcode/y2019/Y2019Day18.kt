package io.github.kmakma.adventofcode.y2019

import io.github.kmakma.adventofcode.y2019.utils.UndergroundVaultGraph

internal class Y2019Day18 : Y2019Day(18, "Many-Worlds Interpretation") {
    private lateinit var inputLines: List<String>

    override fun initializeDay() {
//        inputLines = inputInStringLines()
        UndergroundVaultGraph.build(inputInCharLines())
//        UndergroundVault(inputLines)//.build()
    }

    override suspend fun solveTask1(): Any? {
//        TODO("not implemented")
        return null
    }

    override suspend fun solveTask2(): Any? {
//        TODO("not implemented")
        return null
    }

}
