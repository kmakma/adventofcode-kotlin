package io.github.kmakma.adventofcode.y2020

import io.github.kmakma.adventofcode.utils.Day
import kotlin.time.ExperimentalTime

@ExperimentalTime
class Y2020Day02() : Day(2020, 2, "Password Philosophy") {
    private lateinit var passwordList: List<List<String>>

    override fun initializeDay() {
        passwordList = inputInStringLines().map { parseLine(it) }
    }

    private fun parseLine(line: String): List<String> {
        // line example: "1-2 a: asdf"
        val hyphen = line.indexOf("-")
        val firstSpace = line.indexOf(" ")
        val colon = line.indexOf(":")
        val newLine = ArrayList<String>()
        return newLine.apply {
            add(line.substring(0, hyphen))
            add(line.substring(hyphen + 1, firstSpace))
            add(line.substring(firstSpace + 1, colon))
            add(line.substring(colon + 2))
        }
    }

    override suspend fun solveTask1(): Any? {
        return passwordList.count { isValidWithOldPolicy(it) }
    }

    private fun isValidWithOldPolicy(line: List<String>): Boolean {
        return line.last().count { line[2].first() == it } in IntRange(line[0].toInt(), line[1].toInt())
    }

    override suspend fun solveTask2(): Any? {
        return passwordList.count { isValidWithNewPolicy(it) }
    }

    private fun isValidWithNewPolicy(line: List<String>): Boolean {
        val first = line.last()[line[0].toInt() - 1] == line[2].first()
        val scnd = line.last()[line[1].toInt() - 1] == line[2].first()
        return first.xor(scnd)
    }
}

@ExperimentalTime
fun main() {
    Y2020Day02().solveAndPrint()
}