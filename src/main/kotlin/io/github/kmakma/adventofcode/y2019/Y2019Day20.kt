package io.github.kmakma.adventofcode.y2019

import io.github.kmakma.adventofcode.y2019.utils.TorusGraph
import io.github.kmakma.adventofcode.y2019.utils.TorusGraphBuilder
import kotlin.time.ExperimentalTime

@ExperimentalTime
internal class Y2019Day20 : Y2019Day(20, "Donut Maze") {
    //    private lateinit var input: List<String>
    private lateinit var input: List<List<Char>>
//    private lateinit var donutMaze: TorusGraph

    override fun initializeDay() {
//        input = inputInStringLines()
        input = inputInCharLines()
//        donutMaze = TorusGraph.build(input)
//        TODO("not implemented")
    }

    override suspend fun solveTask1(): Int {
        val donutMaze = TorusGraphBuilder(input).buildTorusGraph()

        return donutMaze.shortestPathLength("AA", "ZZ")
        TODO("not implemented")
    }

    override suspend fun solveTask2(): Any? {
        return null
        TODO("not implemented")
    }

}
