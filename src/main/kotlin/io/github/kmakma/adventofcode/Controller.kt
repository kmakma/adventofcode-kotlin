package io.github.kmakma.adventofcode

import io.github.kmakma.adventofcode.ControllerExitStatus.*
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
        | Choose a year or action:
        |  [ ] "2015".."$currentAoCYear" => go to year
        |  [ ] no input => execute current (or latest) AoC day: $currentAoCYear-12-$currentAoCDay
        |  [ ] "*" => execute all tasks from all years
        |  [ ] "exit" => end program
        | Input: 
        """.trimMargin()

    private val invalidYearAction = " Not a year or action!"
    private val unknownYear = " Year not in known range!"
    private val notImplementedYear = " Year not implemented yet!"
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
        // TODO add "[year] [day]*|"*"" to execute selected/all day from a year
        when {
            input == "exit" -> stop()
            input == "*" -> print("not implemented :P") // TODO implement all years all tasks execution
            input.isNullOrBlank() -> startYearController(currentAoCYear)//, currentAoCDay)
            year != null -> startYearController(year)
            else -> println(invalidYearAction)
        }
    }

    private fun startYearController(year: Int) {
        val yearController: YearController = when (year) {
            2015, 2016, 2017, 2018 -> {
                println(notImplementedYear)
                return
            }
            2019 -> ControllerY2019()
            else -> {
                println(unknownYear)
                return
            }
        }
        // TODO feed controler info before start, like:
        //    if day > 0 auto open day (tell the yC so)
        if (yearController.start() == EXIT_PROGRAM) {
            stop()
        }
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
        |  [ ] no input => execute current/latest AoC day: $year-12-$currentDay
        |  [ ] "*" => execute all days
        |  [ ] "back" => back to year selection
        |  [ ] "exit" => end program
        | Input: 
        """.trimMargin()
    private val invalidDayAction = " Not a day or action!"
    private val unknownDay = " Day not in range (1..25)!"

    fun start(): ControllerExitStatus {
        var exitStatus = IDLE
        while (exitStatus != EXIT_YEAR && exitStatus != EXIT_PROGRAM) {
            exitStatus = requestDayInput()
        }
        return exitStatus
    }

    private fun requestDayInput(): ControllerExitStatus {
        println(header)
        println(chooseDayActions)
        val input = readLine()
        val day = input?.toIntOrNull()
        // TODO implement ranges of days
        return when {
            input == "exit" -> EXIT_PROGRAM
            input == "back" -> EXIT_YEAR
            input == "*" -> {
                // TODO implement "*" days
                println("not yet implemented :P")
                IDLE
            }
            input.isNullOrBlank() -> executeDay(currentDay)
            day != null -> executeDay(day)
            else -> {
                println(invalidDayAction)
                IDLE
            }
        }
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