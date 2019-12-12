package io.github.kmakma.adventofcode.y2019

import kotlin.time.ExperimentalTime

@ExperimentalTime
internal class Y2019Day04 : Y2019Day(4, "Secure Container") {
    private lateinit var passwordRange: IntRange

    override fun initializeDay() {
        val rangeValues = firstLine().split("-").map { it.toInt() }
        passwordRange = rangeValues[0]..rangeValues[1]
    }

    override suspend fun solveTask1(): Int {
        return passwordRange.mapNotNull { if (validateTask1Password(it)) it else null }.size
    }

    override suspend fun solveTask2(): Int {
        return passwordRange.mapNotNull { if (validateTask2Password(it)) it else null }.size
    }

    private fun validateTask1Password(pwInt: Int): Boolean {
        val pw = pwInt.toIntArray()
        return pw.size == 6 &&
                pwInt >= passwordRange.first &&
                pwInt <= passwordRange.last &&
                hasDouble(pw) &&
                !decreases(pw)
    }

    private fun validateTask2Password(pwInt: Int): Boolean {
        val pw = pwInt.toIntArray()
        return pw.size == 6 &&
                pwInt >= passwordRange.first &&
                pwInt <= passwordRange.last &&
                hasTrueDouble(pw) &&
                !decreases(pw)
    }

    private fun Int.toIntArray(): IntArray {
        return this.toString().map(Character::getNumericValue).toIntArray()
    }

    private fun hasDouble(pw: IntArray): Boolean {
        for (x in 1 until pw.size) {
            if (pw[x - 1] == pw[x]) return true
        }
        return false
    }

    private fun hasTrueDouble(pw: IntArray): Boolean {
        var currentGroup = -1
        var groupLength = 0
        for (x in pw) {
            when {
                currentGroup == x -> groupLength++
                groupLength == 2 -> return true
                else -> {
                    currentGroup = x
                    groupLength = 1
                }
            }
        }
        return groupLength == 2
    }

    private fun decreases(pw: IntArray): Boolean {
        for (x in 1 until pw.size) {
            if (pw[x - 1] > pw[x]) return true
        }
        return false
    }

}