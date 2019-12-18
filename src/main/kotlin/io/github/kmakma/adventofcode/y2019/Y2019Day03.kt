package io.github.kmakma.adventofcode.y2019

import io.github.kmakma.adventofcode.y2019.utils.Vector2D
import kotlin.time.ExperimentalTime

@ExperimentalTime
internal class Y2019Day03 : Y2019Day(3, "Distance to closest intersection of wires") {
    private lateinit var wirePath1: List<Vector2D>
    private lateinit var wirePath2: List<Vector2D>
    private lateinit var intersections: Set<Vector2D>

    override fun initializeDay() {
        val pathVectors = parseInputInstructionsToVectors(inputInSplitLines(","))
        wirePath1 = tracePath(pathVectors[0])
        wirePath2 = tracePath(pathVectors[1])
        intersections = wirePath1.intersect(wirePath2)
    }

    override suspend fun solveTask1(): Int {
        return distanceNextIntersection()
    }

    override suspend fun solveTask2(): Int {
        return fewestStepsToIntersection()
    }

    private fun parseInputInstructionsToVectors(pathInstructions: List<List<String>>): List<List<Vector2D>> {
        return pathInstructions.map { instrList -> instrList.map { instr -> parseInstructionToVector(instr) } }
    }

    private fun parseInstructionToVector(instruction: String): Vector2D {
        require(instruction.length >= 2)
        val length = instruction.substring(1).toInt()
        return when (instruction.first()) {
            'U' -> Vector2D(0, length)
            'D' -> Vector2D(0, -1 * length)
            'R' -> Vector2D(length, 0)
            'L' -> Vector2D(-1 * length, 0)
            else -> throw IllegalArgumentException("Unknown wire path instruction")
        }
    }

    private fun tracePath(vector2Ds: List<Vector2D>): List<Vector2D> {
        val path = ArrayList<Vector2D>()
        var nextPoint = Vector2D(0, 0)
        vector2Ds.forEach { vector ->
            (nextPoint until (nextPoint + vector)).forEach { point -> path.add(point) }
            nextPoint += vector
        }
        return path
    }

    private fun distanceNextIntersection(): Int {
        var minDist = 0
        for (point in intersections) {
            if (point.manhattanDistance != 0 && (minDist == 0 || point.manhattanDistance < minDist)) {
                minDist = point.manhattanDistance
            }
        }
        return minDist
    }

    private fun fewestStepsToIntersection(): Int {
        var shortestPath = 0
        for (point in intersections) {
            val currentSteps = combinedStepsFor(point)
            if (currentSteps != 0 && (shortestPath == 0 || currentSteps < shortestPath)) {
                shortestPath = currentSteps
            }
        }
        return shortestPath
    }

    private fun combinedStepsFor(vector2D: Vector2D): Int {
        return wirePath1.indexOf(vector2D) + wirePath2.indexOf(vector2D)
    }
}