package io.github.kmakma.adventofcode.y2019

class Y2019Day01 : Y2019Day(
    1,
    "Fuel for all modules:",
    "Fuel for all modules and for the fuel itself:"
) {
    private lateinit var fuelForModules: List<Int>

    override fun solve() {
        fuelForModules = calculateFuelForModules(getInput())
        resultTask1 = fuelForAllModules()
        resultTask2 = totalFuel()
    }

    override fun getInput(): List<String> = linesToList()

    private fun calculateFuelForModules(modules: List<String>): List<Int> {
        return modules
            .map { it.toInt() }
            .map { fuelForWeight(it) }
    }

    private fun fuelForAllModules(): Int {
        return fuelForModules.sum()
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