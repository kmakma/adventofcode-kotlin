package io.github.kmakma.adventofcode.y2019

import io.github.kmakma.adventofcode.y2019.utils.Vector

internal class Y2019Day10 : Y2019Day(10, "Monitoring Station") {
    // asteroidMap with key:Vector=coordinate, value:Boolean:true=asteroid,false=empty
    private lateinit var asteroidList: List<Vector>
    private lateinit var xRange: IntRange // todo unneeded?
    private lateinit var yRange: IntRange // todo unneeded?

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
        println("Number of asteroids: ${asteroidList.size}")
        xRange = asteroidMapLines[0].indices
        yRange = asteroidMapLines.indices
    }

    override suspend fun solveTask1(): Int {
        val detectedAsteroids = mutableListOf<Int>()
        for (asteroid in asteroidList) {
            detectedAsteroids.add(numberOfDetectedAsteroids(asteroid))
        }
        println("detectedAsteroids list: ${detectedAsteroids.size}")
        return detectedAsteroids.max() ?: 0
    }

    private fun numberOfDetectedAsteroids(asteroid: Vector): Int {
        var a = (asteroidList - asteroid).map { it - asteroid }
        a = a.map { it.shortest() }
        val b = a.toMutableSet()
        println("test")
        return b.size
//        return asteroidList.map { (it - asteroid).shortest() }.toMutableSet().apply { remove(Vector(0, 0)) }.size

    }

    override suspend fun solveTask2(): Any? {
        return super.solveTask2()
    }
}