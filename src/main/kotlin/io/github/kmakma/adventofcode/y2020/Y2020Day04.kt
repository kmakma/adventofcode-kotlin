package io.github.kmakma.adventofcode.y2020

import io.github.kmakma.adventofcode.utils.Day
import kotlin.time.ExperimentalTime

@ExperimentalTime
class Y2020Day04 : Day(2020, 4, "Passport Processing") {
    private lateinit var input: List<String>
    private lateinit var passports: List<Passport>

    override fun initializeDay() {
        input = inputInStringLines()
        passports = input.fold(ArrayList<Passport>()) { list, data ->
            if (data.isBlank()) {
                list.add(Passport())
            } else {
                var passport = list.lastOrNull()
                if (passport == null) {
                    passport = Passport()
                    list.add(passport)
                }
                passport.parseInput(data)
            }
            list
        }
    }

    override suspend fun solveTask1(): Any? {
        return passports.count { it.isInitialized() }
    }

    override suspend fun solveTask2(): Any? {
        return passports.count { it.validateData() }
    }
}

class Passport {
    private lateinit var byr: String
    private lateinit var iyr: String
    private lateinit var eyr: String
    private lateinit var hgt: String
    private lateinit var hcl: String
    private lateinit var ecl: String
    private lateinit var pid: String
    private lateinit var cid: String

    fun isInitialized(): Boolean {
        return this::byr.isInitialized && this::iyr.isInitialized && this::eyr.isInitialized
                && this::hgt.isInitialized && this::hcl.isInitialized && this::ecl.isInitialized
                && this::pid.isInitialized // && this::byr.isInitialized
    }

    fun validateData(): Boolean {
        if (!isInitialized()) return false
        return validateYearData() && validateHeight() && validateHairColor() && validateEyeColor() && validateID()

    }

    private val idRegex = """\d{9}""".toRegex()
    private fun validateID(): Boolean {
        return pid.matches(idRegex)
    }

    private val allowedEcl = listOf("amb", "blu", "brn", "gry", "grn", "hzl", "oth")
    private fun validateEyeColor(): Boolean {
        return ecl in allowedEcl
    }

    private val hclRegex = """#[\d|a-f]{6}""".toRegex()
    private fun validateHairColor(): Boolean {
        return hcl.matches(hclRegex)
    }

    private fun validateHeight(): Boolean {
        val unit = hgt.substring(hgt.lastIndex - 1)
        val value = hgt.substring(0, hgt.lastIndex - 1).toIntOrNull()
        return (value != null)
                && ((unit == "cm" && value >= 150 && value <= 193)
                || (unit == "in" && value >= 59 && value <= 76))
    }

    private fun validateYearData(): Boolean {
        val byrInt = byr.toIntOrNull()
        val iyrInt = iyr.toIntOrNull()
        val eyrInt = eyr.toIntOrNull()
        return (byrInt != null && byrInt >= 1920 && byrInt <= 2002)
                && (iyrInt != null && iyrInt >= 2010 && iyrInt <= 2020)
                && (eyrInt != null && eyrInt >= 2020 && eyrInt <= 2030)
    }

    fun parseInput(input: String) {
        input.split(" ").forEach {
            val (field, data) = it.split(":")
            setField(field, data)
        }
    }

    private fun setField(field: String, data: String) {
        when (field) {
            "byr" -> byr = data
            "iyr" -> iyr = data
            "eyr" -> eyr = data
            "hgt" -> hgt = data
            "hcl" -> hcl = data
            "ecl" -> ecl = data
            "pid" -> pid = data
            "cid" -> cid = data
        }
    }


}

@ExperimentalTime
fun main() {
    Y2020Day04().solveAndPrint()
}