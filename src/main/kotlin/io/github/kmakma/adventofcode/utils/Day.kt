package io.github.kmakma.adventofcode.utils

import io.github.kmakma.adventofcode.utils.TimeUnit.*
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import java.util.*
import kotlin.math.absoluteValue
import kotlin.system.measureNanoTime

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
    private var timeTotal: Long = 0
    private var timeInit: Long = 0
    private var timeTask1: Long = 0
    private var timeTask2: Long = 0

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

    // TODO add option to not run async
    private suspend fun solve(): Unit = coroutineScope {
        timeTotal = measureNanoTime {
            timeInit = measureNanoTime { initializeDay() }
            val deferredTimeTask1 = async {
                measureNanoTime {
                    resultTask1 = try {
                        solveTask1()
                    } catch (e: NotImplementedError) {
                        null
                    }
                }
            }
            val deferredTimeTask2 = async {
                measureNanoTime {
                    resultTask2 = try {
                        solveTask2()
                    } catch (e: NotImplementedError) {
                        null
                    }
                }
            }
            timeTask1 = deferredTimeTask1.await()
            timeTask2 = deferredTimeTask2.await()
        }
        timeTotal.toString()
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
            println("Time Measurements; total: ${timeTotal.nsToString()}, init: ${timeInit.nsToString()}, task1: ${timeTask1.nsToString()}, task2: ${timeTask2.nsToString()}")
        }
    }
}

/**
 * Similar to [kotlin.time.Duration.toString]
 */
private fun Long.nsToString(): String {
    // TODO
    val ns = absoluteValue
    val unit = when {
        ns < 1e3 -> NANOSECONDS
        ns < 1e6 -> MICROSECONDS
        ns < 1e9 -> MILLISECONDS
        ns < 1000e9 -> SECONDS
        ns < 60_000e9 -> MINUTES
        ns < 3600_000e9 -> HOURS
        else -> HOURS
    }
    val value = String.format(Locale.US, "%.2f", convertNsToDouble(ns, unit))
    return value + unit.shortName
}

private fun convertNsToDouble(ns: Long, unit: TimeUnit): Double {
    return when (unit) {
        NANOSECONDS -> ns.toDouble()
        MICROSECONDS -> ns / 1.0e3
        MILLISECONDS -> ns / 1.0e6
        SECONDS -> ns / 1.0e9
        MINUTES -> ns / 60.0e9
        HOURS -> ns / 3600.0e9
    }
}

private enum class TimeUnit(val shortName: String) {
    NANOSECONDS("ns"),
    MICROSECONDS("us"),
    MILLISECONDS("ms"),
    SECONDS("s"),
    MINUTES("m"),
    HOURS("h")
}
