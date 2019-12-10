package io.github.kmakma.adventofcode.y2019

import io.github.kmakma.adventofcode.y2019.utils.OrbitalMap

internal class Y2019Day06 : Y2019Day(
    6,
    "Number of direct and indirect orbits:"
) {
    private lateinit var orbitalMap: OrbitalMap
    override fun initializeDay() {
        super.initializeDay()
    }

    override suspend fun solveTask1(): Any? {
        return super.solveTask1()
    }

    override suspend fun solveTask2(): Any? {
        return super.solveTask2()
    }

    override  fun solve() {
        orbitalMap = buildOrbitalMap()
        resultTask1 = orbitalMap.totalNumberOfOrbits()
        resultTask2 = orbitalMap.travelDistance("YOU", "SAN") - 2
    }

    override fun getInput(): List<String> {
        return linesToList()
    }

    private fun buildOrbitalMap(): OrbitalMap {
        return OrbitalMap.parse(getInput())
    }
}