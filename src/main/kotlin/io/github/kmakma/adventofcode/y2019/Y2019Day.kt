package io.github.kmakma.adventofcode.y2019

import io.github.kmakma.adventofcode.utils.Day

internal abstract class Y2019Day(day: Int, dayTitle: String) :
    Day(2019, day, dayTitle) {

    protected fun inputAsIntcodeProgram(): List<Long> {
        return firstCsvLineToList().map { it.toLong() }
    }


    // TODO delete this  functions (when done):

    override fun solve() {
    }

    open fun getInput(): Any {
        return ""
    }

    override fun initializeDay() {
    }

    override suspend fun solveTask1(): Any? {
        return null
    }

    override suspend fun solveTask2(): Any? {
        return null
    }
}