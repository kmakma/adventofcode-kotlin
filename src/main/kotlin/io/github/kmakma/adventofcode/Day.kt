package io.github.kmakma.adventofcode

abstract class Day(
    private val year: Int,
    private val day: Int,
    private val descriptionTask1: String = "unknown task",
    private val descriptionTask2: String = "unknown task"
) : InputParser() {
    abstract fun solve()
    abstract fun getInput(): Any

    var resultTask1: Any? = null
    var resultTask2: Any? = null

    override fun getFilePath(): String {
        return if (day < 10) {
            "input/$year/0$day.txt"
        } else {
            "input/$year/$day.txt"
        }
    }

    fun solveAndPrint() {
        solve()
        printResults()
    }

    private fun printResults() {
        println(" ~~~ Year $year - Day $day ~~~")
        println("  Task 1: $descriptionTask1")
        if (resultTask1 != null) {
            println("         $resultTask1")
        } else {
            println("         task 1 not solved yet")
        }
        println("  Task 2: $descriptionTask2")
        if (resultTask2 != null) {
            println("         $resultTask2")
        } else {
            println("         task 2 not solved yet")
        }
    }


}