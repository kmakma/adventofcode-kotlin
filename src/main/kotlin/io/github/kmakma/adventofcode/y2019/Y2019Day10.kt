package io.github.kmakma.adventofcode.y2019

import io.github.kmakma.adventofcode.y2019.utils.Vector2D
import kotlin.math.abs

internal class Y2019Day10 : Y2019Day(10, "Monitoring Station") {
    private lateinit var asteroidList: List<Vector2D>
    private lateinit var detectedAsteroids: List<Int>

    override fun initializeDay() {
        val asteroidMapLines = inputInStringLines()
        val asteroidMutList = mutableListOf<Vector2D>()
        // parse asteroid map to list of asteroids
        for (y in asteroidMapLines.indices) {
            for (x in asteroidMapLines[y].indices) {
                if (asteroidMapLines[y][x] == '#') {
                    asteroidMutList.add(Vector2D(x, y))
                }
            }
        }
        asteroidList = asteroidMutList
        // calculate detected asteroids
        val detected = mutableListOf<Int>()
        for (asteroid in asteroidList) {
            detected.add(numberOfDetectedAsteroids(asteroid))
        }
        detectedAsteroids = detected
    }

    override suspend fun solveTask1(): Int {
        return detectedAsteroids.maxOrNull() ?: 0
    }

    override suspend fun solveTask2(): Int? {
        val stationAsteroid = asteroidList[detectedAsteroids.indexOf(detectedAsteroids.maxOrNull() ?: 0)]
        // map of vectors grouped by shortest vector; e.g. ((1,2),(2,4),(3,6)..) at index (1,2)
        val vectorsToAsteroids: MutableMap<Vector2D, MutableList<Vector2D>> =
            (asteroidList - stationAsteroid)
                .map { (it - stationAsteroid) }
                .groupBy { it.shortest() }
                .mapValues { it.value.sortedWith { a, b -> clockwiseCompare(a, b) }.toMutableList() }
                .toMutableMap()
        // sort keys clockwise (actually not clockwise, since it starts at (0,-1) and goes counter-clockwise)
        val sortedKeys = (vectorsToAsteroids.keys)
            .sortedWith { a, b -> clockwiseCompare(a, b) }
            .toMutableSet()
        // laser show, until 200 destroyed
        var destroyed = 0
        while (sortedKeys.isNotEmpty()) {
            val iterator = sortedKeys.iterator()
            while (iterator.hasNext()) {
                val direction = iterator.next()
                val vecsInDir: MutableList<Vector2D> = vectorsToAsteroids[direction]!!
                var destroyedV = vecsInDir.removeAt(0)
                destroyed++
                if (destroyed == 200) {
                    destroyedV += stationAsteroid
                    return destroyedV.x * 100 + destroyedV.y
                }
                if (vecsInDir.size < 1) {
                    iterator.remove()
                }
            }
        }
        return null
    }

    /**
     * compare function to sort vectors around (0,0) clockwise starting at 12 o'clock (0,-1). don't use (0,0), might not work
     *
     * compliments to manipulated work of https://stackoverflow.com/a/6989383
     */
    private fun clockwiseCompare(a: Vector2D, b: Vector2D): Int {
        // a and b on opposing sites (left/right)
        if (a.x >= 0 && b.x < 0) return -1
        if (a.x < 0 && b.x >= 0) return 1
        // a and b on y-axis
        if (a.x == 0 && b.x == 0) {
            return when {
                (a.y > 0 && b.y > 0) || (a.y <= 0 && b.y <= 0) -> abs(a.y) - abs(b.y)
                a.y <= 0 && b.y > 0 -> -1
                a.y > 0 && b.y <= 0 -> 1
                else -> error("impossible state")
            }
        }
        // a and b both on one site (left/right); cross product of vectors b x a
        val det = b.x * a.y - a.x * b.y
        if (det != 0) return det
        // points a and b are on the same line from the center
        // check which point is closer to the center
        return (a.length - b.length).toInt()
    }

    private fun numberOfDetectedAsteroids(asteroid: Vector2D): Int {
        return (asteroidList - asteroid)
            .map { (it - asteroid).shortest() }
            .toSet()
            .size
    }
}