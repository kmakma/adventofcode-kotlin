package io.github.kmakma.adventofcode.y2020

import io.github.kmakma.adventofcode.utils.Day
import kotlin.math.abs

fun main() {
    Y2020Day12().solveAndPrint()
}

class Y2020Day12 : Day(2020, 12, "Rain Risk") {
    private lateinit var inputList: List<String>

    override fun initializeDay() {
        inputList = inputInStringLines()
    }

    override suspend fun solveTask1(): Any? {
        val startPos = FerryPos(0, 0, 'E')
        val endPos = inputList.fold(startPos) { pos, line ->
            pos.execute(line)
        }
        return abs(endPos.x) + abs(endPos.y)
    }

    override suspend fun solveTask2(): Any? {
        val startPos = FerryPos(0, 0, 'E')
        val startWaypoint = FerryWaypoint(10, 1)
        val endPos = inputList.fold(Pair(startPos, startWaypoint)) { pair, line ->
            pair.first.execute(pair.second, line)
        }.first
        return abs(endPos.x) + abs(endPos.y)
    }
}

private data class FerryPos(val x: Int, val y: Int, val dir: Char) {
    fun execute(instr: String): FerryPos {
        val value = instr.substring(1).toInt()
        return when (instr[0]) {
            'N' -> copy(y = y + value)
            'S' -> copy(y = y - value)
            'E' -> copy(x = x + value)
            'W' -> copy(x = x - value)
            'L' -> copy(dir = parseTurn(dir, -value))
            'R' -> copy(dir = parseTurn(dir, value))
            'F' -> forward(value)
            else -> throw IllegalArgumentException("action: ${instr[0]}")
        }
    }

    fun execute(waypoint: FerryWaypoint, instr: String): Pair<FerryPos, FerryWaypoint> {
        var pos = this
        var wp = waypoint
        val value = instr.substring(1).toInt()

        when (instr[0]) {
            'N' -> wp = wp.copy(y = wp.y + value)
            'S' -> wp = wp.copy(y = wp.y - value)
            'E' -> wp = wp.copy(x = wp.x + value)
            'W' -> wp = wp.copy(x = wp.x - value)
            'L' -> wp = wp.turn(360 - value)
            'R' -> wp = wp.turn(value)
            'F' -> pos = moveTo(wp, value)
            else -> throw IllegalArgumentException("action: ${instr[0]}")
        }
        return Pair(pos, wp)
    }

    fun moveTo(ferryPoint: FerryWaypoint, value: Int): FerryPos {
        return copy(x = x + value * ferryPoint.x, y = y + value * ferryPoint.y)
    }

    private fun forward(value: Int): FerryPos {
        return when (dir) {
            'N' -> copy(y = y + value)
            'S' -> copy(y = y - value)
            'E' -> copy(x = x + value)
            'W' -> copy(x = x - value)
            else -> throw IllegalArgumentException()
        }
    }

    companion object {
        fun parseTurn(dir: Char, degree: Int): Char {
            var dirDegree = when (dir) {
                'N' -> 0
                'E' -> 90
                'S' -> 180
                'W' -> 270
                else -> throw IllegalArgumentException()
            }
            dirDegree += degree
            if (dirDegree < 0) dirDegree += 360
            return when (dirDegree.rem(360)) {
                0 -> 'N'
                90 -> 'E'
                180 -> 'S'
                270 -> 'W'
                else -> throw IllegalArgumentException("turn: $dirDegree, with ${abs(dirDegree.rem(360))}")
            }
        }
    }
}

private data class FerryWaypoint(val x: Int, val y: Int) {
    fun turn(degree: Int): FerryWaypoint {
        return when (degree) {
            0 -> copy()
            90 -> copy(x = y, y = -x)
            180 -> copy(x = -x, y = -y)
            270 -> copy(x = -y, y = x)
            else -> throw IllegalArgumentException()
        }
    }
}