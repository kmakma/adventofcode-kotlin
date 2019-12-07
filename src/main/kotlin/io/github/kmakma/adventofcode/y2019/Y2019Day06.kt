package io.github.kmakma.adventofcode.y2019

import io.github.kmakma.adventofcode.y2019.utils.OrbitalMap

class Y2019Day06 : Y2019Day(
    6,
    "Number of direct and indirect orbits:",
    "NUmber of orbital transfers from YOU to SAN:"
) {
    private lateinit var orbitalMap: OrbitalMap

    override fun solve() {
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