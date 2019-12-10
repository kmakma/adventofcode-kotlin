package io.github.kmakma.adventofcode.utils

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import kotlin.system.measureTimeMillis

abstract class Day(
    private val year: Int,
    private val day: Int,
    protected val dayTitle: String
) : InputParser() {
    abstract fun initializeDay()

    abstract suspend fun solveTask1(): Any?
    abstract suspend fun solveTask2(): Any?

    var resultTask1: Any? = null
        protected set
    var resultTask2: Any? = null
        protected set
    var timeTotal: Long = -1
        private set
    var timeInit: Long = -1
        private set
    var timeTask1: Long = -1
        private set
    var timeTask2: Long = -1
        private set

    override fun getFilePath(): String {
        return if (day < 10) {
            "input/$year/0$day.txt"
        } else {
            "input/$year/$day.txt"
        }
    }

    abstract fun solve()
    suspend fun solveNew(): Unit = coroutineScope {
        timeTotal = measureTimeMillis {
            timeInit = measureTimeMillis { initializeDay() }
            val deferredTimeTask1 = async { measureTimeMillis { resultTask1 = solveTask1() } }
            val deferredTimeTask2 = async { measureTimeMillis { resultTask2 = solveTask2() } }
            timeTask1 = deferredTimeTask1.await()
            timeTask2 = deferredTimeTask2.await()
        }
    }

    fun solveAndPrint() {
        runBlocking { solveNew() }
        printResults()
    }

    private fun printResults() {
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
    }
}