package io.github.kmakma.adventofcode

import io.github.kmakma.adventofcode.ControllerExitStatus.*
import io.github.kmakma.adventofcode.utils.Day
import io.github.kmakma.adventofcode.y2019.ControllerY2019
import java.time.LocalDateTime
import java.time.Month
import java.time.ZoneId

private val today: LocalDateTime
    get() = LocalDateTime.now(ZoneId.of("UTC-5"))
private val currentAoCYear: Int
    get() {
        return when {
            today.month == Month.DECEMBER -> today.year
            today.year <= 2015 -> 2015
            else -> today.year - 1
        }
    }
private val currentAoCDay: Int
    get() {
        return when {
            currentAoCYear == today.year -> if (today.dayOfMonth > 25) 25 else today.dayOfMonth
            currentAoCYear < today.year -> 25 // last day of last year's AoC
            currentAoCYear > today.year -> 1 // first AoC day ever, since year = 2015
            else -> error("Controller.currentAoCYear: weird year? today's year=${today.year}, aocYear=$currentAoCYear")
        }
    }

class MainController {
    private var exitOrdered = false

    private val mainHeader = """
        | * * * * * * * * * * * * * * * * * * * * *
        | *  Welcome to Advent of Code Solutions  *
        | *       provided to you by kmakma       *
        | * * * * * * * * * * * * * * * * * * * * *
        """.trimMargin()
    private val chooseYearActions = """
        |
        | Choose an action:
        |  [ ] "2015".."$currentAoCYear" => go to year
        |  [ ] "2015".."$currentAoCYear" ("2015".."$currentAoCYear")* => execute tasks multiple years
        |  [ ] "*" => execute all tasks from all years (2015..$currentAoCYear)
        |  [ ] no input => execute current (or latest) AoC day: $currentAoCYear-12-$currentAoCDay
        |  [ ] "exit" => end program
        | Input: 
        """.trimMargin()
    // TODO add: terminal selection: in year/action selection, just day | [ ]

    private val invalidYearAction = " Not a year or action!"
    private val unknownYear = " Year not in known range!"
    private val exiting = "Stopping program..."

    fun start() {
        println(mainHeader)
        while (!exitOrdered) {
            requestYearInput()
        }
        println(exiting)
    }

    /**
     * Doesn't actually stop the execution of current running stuff, but "lets it run out of steam"
     */
    private fun stop() {
        exitOrdered = true
    }

    private fun requestYearInput() {
        println(chooseYearActions)
        val input = readLine()
        val year = input?.toIntOrNull()
        val years = input?.split(" ")?.mapNotNull { it.toIntOrNull() }
        when {
            input == "exit" -> stop()
            input == "*" -> executeAllYears()
            input.isNullOrBlank() -> startYearController(currentAoCYear, currentAoCDay)
            years != null && years.size > 1 -> executeYears(years)
            year != null -> startYearController(year)
            else -> println(invalidYearAction)
        }
    }

    private fun executeAllYears() = executeYears(2015..currentAoCYear)

    private fun executeYears(years: Iterable<Int>) {
        years.forEach { getYearController(it)?.execute() }
    }

    private fun startYearController(year: Int, day: Int = 0) {
        val yearController = getYearController(year) ?: return
        if (yearController.start(day) == EXIT_PROGRAM) {
            stop()
        }
    }

    private fun getYearController(year: Int): YearController? {
        return when (year) {
            2015, 2016, 2017, 2018 -> {
                println(notImplementedYear(year))
                null
            }
            2019 -> ControllerY2019()
            else -> {
                println(unknownYear)
                null
            }
        }
    }


    private fun notImplementedYear(year: Int) {
        println(" Year $year not implemented yet!")
    }
}

enum class ControllerExitStatus {
    EXIT_PROGRAM, EXIT_YEAR, IDLE
}

abstract class YearController {
    protected abstract val year: Int

    private val currentDay: Int
        get() {
            return if (year == currentAoCYear) {
                currentAoCDay
            } else {
                25
            }
        }

    private val header
        get() = "Selected year: $year"
    private val chooseDayActions
        get() = """
        | Choose a day or action
        |  [ ] "1".."25" => execute tasks of one day
        |  [ ] "1".."25" ("1".."25")* => execute multiple days
        |  [ ] "*" => execute all days$allDaysLimiter
        |  [ ] no input => execute current/latest AoC day: $year-12-$currentDay
        |  [ ] "back" => back to year selection
        |  [ ] "exit" => end program
        | Input: 
        """.trimMargin()
    private val invalidDayAction = " Not a day or action!"
    private val unknownDay = " Day not in range (1..25)!"
    private val allDaysLimiter: String
        get() {
            return if (year == currentAoCYear) {
                " until today (1..$currentDay)"
            } else ""
        }

    fun start(day: Int = 0): ControllerExitStatus {
        if (day in 1..25) {
            executeDay(day)
        }
        var exitStatus = IDLE
        while (exitStatus != EXIT_YEAR && exitStatus != EXIT_PROGRAM) {
            exitStatus = requestDayInput()
        }
        return exitStatus
    }

    fun execute() {
        println(header)
        executeAllDays()
    }

    private fun requestDayInput(): ControllerExitStatus {
        println(header)
        println(chooseDayActions)
        val input = readLine()
        val day = input?.toIntOrNull()
        val days = input?.split(" ")?.mapNotNull { it.toIntOrNull() }
        return when {
            input == "exit" -> EXIT_PROGRAM
            input == "back" -> EXIT_YEAR
            input == "*" -> executeAllDays()
            days != null && days.size > 1 -> executeDays(days)
            input.isNullOrBlank() -> executeDay(currentDay)
            day != null -> executeDay(day)
            else -> {
                println(invalidDayAction)
                IDLE
            }
        }
    }

    private fun executeAllDays(): ControllerExitStatus {
        return if (year == currentAoCYear) {
            executeDays(1..currentDay)
        } else {
            executeDays(1..25)
        }
    }

    private fun executeDays(days: Iterable<Int>): ControllerExitStatus {
        days.forEach { executeDay(it) }
        return IDLE
    }

    private fun executeDay(day: Int): ControllerExitStatus {
        if (day !in 1..25) {
            println(unknownDay)
            return IDLE
        }
        val dayObj = getDay(day)
        if (dayObj != null) {
            dayObj.solveAndPrint()
        } else {
            notImplementedDay(day)
        }
        return IDLE
    }

    private fun notImplementedDay(day: Int) {
        println(" Day $day of year $year not implemented yet!")
    }

    protected abstract fun getDay(day: Int): Day?
}