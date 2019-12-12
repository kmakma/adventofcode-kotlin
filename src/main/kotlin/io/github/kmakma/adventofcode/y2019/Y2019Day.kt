package io.github.kmakma.adventofcode.y2019

import io.github.kmakma.adventofcode.utils.Day
import kotlin.time.ExperimentalTime

@ExperimentalTime
internal abstract class Y2019Day(day: Int, dayTitle: String) :
    Day(2019, day, dayTitle) {

    protected fun inputAsIntcodeProgram(): List<Long> {
        return firstCsvLineToList(",").map { it.toLong() }
    }
}