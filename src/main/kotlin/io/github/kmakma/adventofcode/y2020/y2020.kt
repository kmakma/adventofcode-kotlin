package io.github.kmakma.adventofcode.y2020

import io.github.kmakma.adventofcode.utils.nsToString
import kotlin.system.measureNanoTime

fun main() {
    val time = measureNanoTime {
        Y2020Day01().solveAndPrint()
        Y2020Day02().solveAndPrint()
        Y2020Day03().solveAndPrint()
        Y2020Day04().solveAndPrint()
        Y2020Day05().solveAndPrint()
        Y2020Day06().solveAndPrint()
        Y2020Day07().solveAndPrint()
        Y2020Day08().solveAndPrint()
        Y2020Day09().solveAndPrint()
        Y2020Day10().solveAndPrint()
        Y2020Day11().solveAndPrint()
        Y2020Day12().solveAndPrint()
        Y2020Day13().solveAndPrint()
        Y2020Day14().solveAndPrint()
        Y2020Day15().solveAndPrint()
    }.nsToString()
    println("Time for all days (1-15): $time")
}