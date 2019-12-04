package io.github.kmakma.adventofcode.y2019

class Y2019Day04 : Y2019Day(
    4,
    "Number of possible passwords:",
    "Number of possible passwords, extra rules"
) {
    private lateinit var passwordRange: IntRange

    override fun solve() {
        setPasswordRange(getInput())
        resultTask1 = countPossiblePasswordsTask1()
        resultTask2 = countPossiblePasswordsTask2()
    }

    override fun getInput(): List<String> = linesToList()

    private fun setPasswordRange(inputLines: List<String>) {
        val rangeValues = inputLines.first().split("-").map { it.toInt() }
        passwordRange = rangeValues[0]..rangeValues[1]
    }

    private fun countPossiblePasswordsTask1(): Int {
        return passwordRange.mapNotNull { if (isValidPasswordTask1(it)) it else null }.size
    }

    private fun countPossiblePasswordsTask2(): Int {
        return passwordRange.mapNotNull { if (isValidPasswordTask2(it)) it else null }.size
    }

    private fun isValidPasswordTask1(pwInt: Int): Boolean {
        val pw = pwInt.toIntArray()
        return pw.size == 6 &&
                pwInt >= passwordRange.first &&
                pwInt <= passwordRange.last &&
                hasDouble(pw) &&
                !decreases(pw)
    }

    private fun isValidPasswordTask2(pwInt: Int): Boolean {
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