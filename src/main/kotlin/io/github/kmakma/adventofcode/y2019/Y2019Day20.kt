package io.github.kmakma.adventofcode.y2019

import io.github.kmakma.adventofcode.y2019.utils.TorusGraphBuilder

internal class Y2019Day20 : Y2019Day(20, "Donut Maze") {

    private lateinit var torusGraphBuilder: TorusGraphBuilder

    override fun initializeDay() {
        torusGraphBuilder = TorusGraphBuilder(inputInCharLines())
    }

    override suspend fun solveTask1(): Int {
        val donutMaze = torusGraphBuilder.buildTorusGraph()
        return donutMaze.shortestPathLength()
    }

    override suspend fun solveTask2(): Int {
        val donutMaze = torusGraphBuilder.buildRecTorusGraph()
        return donutMaze.shortestPathLength()
    }

}
