package io.github.kmakma.adventofcode.y2019

import io.github.kmakma.adventofcode.y2019.utils.PaintingRobot
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
internal class Y2019Day11 : Y2019Day(11, "Space Police") {
    private lateinit var intcodeProgram: List<Long>
    override fun initializeDay() {
        intcodeProgram = inputAsIntcodeProgram()
    }

    override suspend fun solveTask1(): Int {
        return PaintingRobot.paintJobsWithBlackStart(intcodeProgram)
    }

    override suspend fun solveTask2(): String {
        val sb = StringBuilder()
        val canvasArray = PaintingRobot.paintResultWithWhiteStart(intcodeProgram)
        for (line in canvasArray) {
            for (point in line) {
                when (point) {
                    0 -> sb.append("\u25A0")
                    1 -> sb.append("\u25A1")
                    else -> sb.append("") // TODO find reasons for these
                }
            }
            sb.append("\n")
        }
        return sb.toString()
    }

    // TODO bonus: add parser for actual result
}