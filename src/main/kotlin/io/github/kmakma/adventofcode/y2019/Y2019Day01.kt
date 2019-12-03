package io.github.kmakma.adventofcode.y2019

class Y2019Day01 : Y2019Day(
    1,
    "Result (index = 0) for intcode program with set parameter:",
    "Parameter for intcode program with set target result:"
) {
    private lateinit var fuelForModules: List<Int>

    override fun solve() {
        resultTask1 = calculateFuelForAllModules()
    }

    override fun getInput(): List<String> {
        return linesToList()
    }

    private fun calculateFuelForAllModules(): Int {
        fuelForModules = getInput().map { it.toInt() }.map { fuelForWeight(it) }
        return fuelForModules.sum()
    }

    private fun fuelForWeight(weight: Int): Int {
        return weight / 3 - 2
    }
}