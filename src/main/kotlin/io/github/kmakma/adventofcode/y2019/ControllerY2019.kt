package io.github.kmakma.adventofcode.y2019

import io.github.kmakma.adventofcode.YearController
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlin.time.ExperimentalTime

@ExperimentalTime
@ExperimentalCoroutinesApi
internal class ControllerY2019 : YearController() {
    override val year: Int = 2019

    override fun getDay(day: Int): Y2019Day? {
        require(day in 1..25)
        return when (day) {
            1 -> Y2019Day01()
            2 -> Y2019Day02()
            3 -> Y2019Day03()
            4 -> Y2019Day04()
            5 -> Y2019Day05()
            6 -> Y2019Day06()
            7 -> Y2019Day07()
            8 -> Y2019Day08()
            9 -> Y2019Day09()
            10 -> Y2019Day10()
            11 -> Y2019Day11()
            12 -> Y2019Day12()
            13 -> Y2019Day13()
            14 -> Y2019Day14()
            15 -> Y2019Day15()
            16 -> Y2019Day16()
            17 -> Y2019Day17()
            18 -> Y2019Day18()
            19 -> Y2019Day19()
            20 -> Y2019Day20()
//            21 -> Y2019Day21()
//            22 -> Y2019Day22()
//            23 -> Y2019Day23()
//            24 -> Y2019Day24()
//            25 -> Y2019Day25()
            else -> null
        }
    }
}