package io.github.kmakma.adventofcode.y2020

import io.github.kmakma.adventofcode.utils.Day
import io.github.kmakma.adventofcode.utils.lcm

fun main() {
    Y2020Day13().solveAndPrint()
}

class Y2020Day13 : Day(2020, 13, "Shuttle Search") {

    private lateinit var busses: List<Int>
    private lateinit var inputList: List<String>


    override fun initializeDay() {
        inputList = inputInStringLines()
        busses = inputList.last().split(",").mapNotNull { it.toIntOrNull() }
    }

    override suspend fun solveTask1(): Any? {
        val departure = inputList.first().toInt()
        val deps = busses.map { it to it * (departure / it + 1) }.toMap()
        val targetTime = deps.values.minOrNull() ?: 0
        val busId = deps.filter { (_, value) -> value == targetTime }.keys.first()
        return busId * (targetTime - departure)
    }

    override suspend fun solveTask2(): Any? {
        val departs = inputList.last().split(",").mapIndexedNotNull { index, busId ->
            if (busId == "x") null else Pair(index, busId.toLong())
        }
        // solution by Oggiva: https://www.reddit.com/r/adventofcode/comments/kc4njx/2020_day_13_solutions/gfnno67/?context=3
        var step = departs.first().second
        var nextBus = 1
        var timestamp = 0L
        do {
            timestamp += step
            if ((timestamp + departs[nextBus].first) % departs[nextBus].second == 0L)
                step = lcm(step, departs[nextBus++].second)
        } while (nextBus < departs.size)
        return timestamp
    }

}