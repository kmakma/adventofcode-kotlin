package io.github.kmakma.adventofcode.y2019

internal class Y2019Day01 : Y2019Day(
    1,
    "The Tyranny of the Rocket Equation"
) {
    private lateinit var fuelForModules: List<Int>

    override fun initializeDay() {
        fuelForModules = calculateFuelForModules(linesToList())
    }

    override suspend fun solveTask1(): Int {
        return fuelForModules.sum()
    }

    override suspend fun solveTask2(): Int {
        return totalFuel()
    }

    private fun calculateFuelForModules(modules: List<String>): List<Int> {
        return modules
            .map { it.toInt() }
            .map { fuelForWeight(it) }
    }

    private fun totalFuel(): Int {
        return fuelForModules
            .map { it + fuelForFuel(it) }
            .sum()
    }

    private fun fuelForWeight(weight: Int): Int {
        return weight / 3 - 2
    }

    private fun fuelForFuel(fuel: Int): Int {
        var neededFuel = 0
        var partialFuel = fuelForWeight(fuel)
        while (partialFuel > 0) {
            neededFuel += partialFuel
            partialFuel = fuelForWeight(partialFuel)
        }
        return neededFuel
    }
}