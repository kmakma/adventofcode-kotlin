package io.github.kmakma.adventofcode.utils

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import kotlin.time.Duration
import kotlin.time.Duration.Companion.ZERO
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

@ExperimentalTime
abstract class Day(
    private val year: Int,
    private val day: Int,
    protected val dayTitle: String
) : InputParser() {
    internal abstract fun initializeDay()

    internal abstract suspend fun solveTask1(): Any? // TODO return a pair of data, "TYPE"-enum (string, number, picture...) and data
    internal abstract suspend fun solveTask2(): Any?

    private var resultTask1: Any? = null
    private var resultTask2: Any? = null
    private var timeTotal: Duration = ZERO
    private var timeInit: Duration = ZERO
    private var timeTask1: Duration = ZERO
    private var timeTask2: Duration = ZERO

    override fun getFilePath(): String {
        return if (day < 10) {
            "input/$year/0$day.txt"
        } else {
            "input/$year/$day.txt"
        }
    }

    /**
     * solves the day and prints a simple representation of the result to the terminal.
     *
     * It's not recommended to run this and/or [solveAndResult] multiple times
     */
    fun solveAndPrint() { // TODO remove => only solve (=solveAndResult)
        runBlocking { solve() }
        printResults()
    }

    /**
     * solves the day and returns a [] result package. You can also get the result package using []
     *
     * It's not recommended to run this and/or [solveAndPrint] multiple times
     */
    suspend fun solveAndResult(): Nothing = coroutineScope {
        TODO("not implemented") // TODO return a "result package" -> times, results (type+data)
    }
    // TODO add bonus for tasks like y19d8 or ~d11 where you get a picture (bonus = parse picture for actual solution)

    private suspend fun solve(): Unit = coroutineScope {
        timeTotal = measureTime {
            timeInit = measureTime { initializeDay() }
            val deferredTimeTask1 = async { measureTime { resultTask1 = solveTask1() } }
            val deferredTimeTask2 = async { measureTime { resultTask2 = solveTask2() } }
            timeTask1 = deferredTimeTask1.await()
            timeTask2 = deferredTimeTask2.await()
        }
    }

    private fun printResults(printTime: Boolean = true) { // TODO (re)move and add special dayResult printer/parser class/file
        println(" ~~~ Year $year - Day $day ~~~")
        println("  Task 1:")
        if (resultTask1 != null) {
            println("$resultTask1")
        } else {
            println("    task 1 not solved yet")
        }
        println("  Task 2:")
        if (resultTask2 != null) {
            println("$resultTask2")
        } else {
            println("    task 2 not solved yet")
        }
        if (printTime) {
            println("Time Measurements (in ns); total: $timeTotal, init: $timeInit, task1: $timeTask1, task2: $timeTask2")
        }
    }
}