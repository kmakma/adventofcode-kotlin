package io.github.kmakma.adventofcode.y2020

import io.github.kmakma.adventofcode.utils.Day

suspend fun main() {
    Y2020Day13().solveAndPrint()
//    Y2020Day13().apply {
//        initializeDay()
//        solveTask2()
//    }
}

class Y2020Day13 : Day(2020, 13, "") {

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
        val lastIndex = inputList.lastIndex
        val departs = inputList.last().split(",").mapIndexedNotNull { index, busId ->
            if (busId == "x") null else BusTimestamp(basis = busId.toInt(), shift = lastIndex - index)
        }
        println("depart: check; $departs")
        var max = departs.maxOrNull()!!.value()
        println("max: check; $max")
        while (departs.notFinished()) {
            departs.forEach { current ->
                if (current.value() < max) {
                    max = current.adjustTo(max)
                }
            }
//            println("new max: $max")
        }
        println("new max: $max")
        return departs.first().value()
        TODO("Not yet implemented")
    }

    private fun lcmShifted(a: Long, b: Long) {
        var x = 1
        var y = 1
        var ax = a * x + 1
        var by = b * y
        while (ax != by) {
            if (ax < by) {
                x++
                ax = a * x + 1
            } else if (ax >= by) {
                y++
                by = b * y
            }
        }
        println("${x * a} ${b * y}")
    }
}

private fun List<BusTimestamp>.notFinished(): Boolean {
    var previous = first()
    forEach { current ->
        if (previous.value() != current.value()) return true
        previous = current
    }
    return false
}

private data class BusTimestamp(val basis: Int, var factor: Long = 7692307692307L, val shift: Int) :
    Comparable<BusTimestamp> {

    fun value() = basis * factor + shift

    override fun compareTo(other: BusTimestamp) = value().compareTo(other.value())

    fun adjustTo(target: Long): Long {
        if (value() < target) {
            val x = if ((target - shift) % basis == 0L) 0L else 1L
            factor = (target - shift) / basis + x
        }
        return value()
    }

}