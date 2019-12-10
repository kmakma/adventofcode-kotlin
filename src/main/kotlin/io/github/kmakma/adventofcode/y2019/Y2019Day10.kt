package io.github.kmakma.adventofcode.y2019

import io.github.kmakma.adventofcode.y2019.utils.Vector
import kotlin.math.abs

internal class Y2019Day10 : Y2019Day(10, "Monitoring Station") {
    // asteroidMap with key:Vector=coordinate, value:Boolean:true=asteroid,false=empty
    private lateinit var asteroidList: List<Vector>
    private lateinit var detectedAsteroids: List<Int>
    private lateinit var monitoringStationAsteroid: Vector

    override fun initializeDay() {
        val asteroidMapLines = linesToList()
        val asteroidMutList = mutableListOf<Vector>()
        // parse asteroid map to list of asteroids
        for (y in asteroidMapLines.indices) {
            for (x in asteroidMapLines[y].indices) {
                if (asteroidMapLines[y][x] == '#') {
                    asteroidMutList.add(Vector(x, y))
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
        return detectedAsteroids.max() ?: 0
    }

    override suspend fun solveTask2(): Int {
        val stationAsteroid = asteroidList[detectedAsteroids.indexOf(detectedAsteroids.max() ?: 0)]
        println(stationAsteroid)
        val nullVector = Vector(0, 0)

        val vectorsToAsteroids: MutableMap<Vector, MutableList<Vector>> =
            (asteroidList - stationAsteroid)
                .map { (it - stationAsteroid) }
                .groupBy { it.shortest() }
                .mapValues { it.value.sortedWith(Comparator { a, b -> clockwiseCompare(a, b) }).toMutableList() }
                .toMutableMap()

        val sortedKeys = (vectorsToAsteroids.keys)
            .sortedWith(Comparator { a, b -> clockwiseCompare(a, b) })
            .toMutableSet()

        var destroyed = 0
        while (sortedKeys.isNotEmpty()) {
            val iterator = sortedKeys.iterator()
            while (iterator.hasNext()) {
                val direction = iterator.next()
                val vecsInDir: MutableList<Vector> = vectorsToAsteroids[direction]!!
                var destroyedV = vecsInDir.removeAt(0)
                destroyed++
                println("$destroyed: $destroyedV ${stationAsteroid + destroyedV}") //todo
                if (destroyed == 200) {
                    destroyedV += stationAsteroid
                    return destroyedV.x * 100 + destroyedV.y
                }
                if (vecsInDir.size < 1) {
                    iterator.remove()
                }
            }
        }
        return 0
    }

    /**
     * compare function to sort vectors around (0,0) clockwise starting at 12 o'clock (0,-1). don't use (0,0), might not work
     *
     * compliments to manipulated work of https://stackoverflow.com/a/6989383
     */
    private fun clockwiseCompare(a: Vector, b: Vector): Int {
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

    private fun numberOfDetectedAsteroids(asteroid: Vector): Int {
        return (asteroidList - asteroid)
            .map { (it - asteroid).shortest() }
            .toSet()
            .size
    }
}