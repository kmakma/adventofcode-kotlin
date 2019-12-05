package io.github.kmakma.adventofcode.y2019

import io.github.kmakma.adventofcode.Day
import io.github.kmakma.adventofcode.YearController

class ControllerY2019 : YearController() {
    override val year: Int = 2019

    override fun getDay(day: Int): Day? {
        require(day in 1..25)
        return when (day) {
            1 -> Y2019Day01()
            2 -> Y2019Day02()
            3 -> Y2019Day03()
            4 -> Y2019Day04()
            5 -> Y2019Day05()
            6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25 -> null
            else -> null
        }
    }
}