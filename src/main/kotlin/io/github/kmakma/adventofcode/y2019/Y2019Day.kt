package io.github.kmakma.adventofcode.y2019

import io.github.kmakma.adventofcode.utils.Day

internal abstract class Y2019Day(day: Int, dayTitle: String, descrTask1: String, descrTask2: String) :
    Day(2019, day, dayTitle, descrTask1, descrTask2) {

    protected fun inputAsIntcodeProgram(): List<Long> {
        return firstCsvLineToList().map { it.toLong() }
    }
}