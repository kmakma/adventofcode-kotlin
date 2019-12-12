package io.github.kmakma.adventofcode.y2019

import io.github.kmakma.adventofcode.y2019.utils.OrbitalMap
import kotlin.time.ExperimentalTime

@ExperimentalTime
internal class Y2019Day06 : Y2019Day(6, "Universal Orbit Map") {
    private lateinit var orbitalMap: OrbitalMap

    override fun initializeDay() {
        orbitalMap = OrbitalMap.parse(linesToList())
    }

    override suspend fun solveTask1(): Any? {
        return orbitalMap.totalNumberOfOrbits()
    }

    override suspend fun solveTask2(): Any? {
        return orbitalMap.travelDistance("YOU", "SAN") - 2
    }
}