package io.github.kmakma.adventofcode.y2019

import io.github.kmakma.adventofcode.y2019.utils.Point

class Y2019Day03() : Y2019Day(
    3,
    "Distance to closest intersection of wires",
    "Fewest combined step to an intersection:"
) {
    private lateinit var wirePath1: List<Point>
    private lateinit var wirePath2: List<Point>
    private lateinit var intersections: Set<Point>

    override fun solve() {
        setPathsAndIntersection(getInput())
        resultTask1 = distanceNextIntersection()
        resultTask2 = fewestStepsToIntersection()
    }

    private fun setPathsAndIntersection(input: List<List<String>>) {
        val pathVectors = parseInstructionsToVectors(input)
        wirePath1 = tracePath(pathVectors[0])
        wirePath2 = tracePath(pathVectors[1])
        intersections = wirePath1.intersect(wirePath2)
    }

    override fun getInput(): List<List<String>> = csvLinesToLists()

    private fun parseInstructionsToVectors(pathInstructions: List<List<String>>): List<List<Point>> {
        return pathInstructions.map { instrList -> instrList.map { instr -> parseInstructionToVector(instr) } }
    }

    private fun parseInstructionToVector(instruction: String): Point {
        require(instruction.length >= 2)
        val length = instruction.substring(1).toInt()
        return when (instruction.first()) {
            'U' -> Point(0, length)
            'D' -> Point(0, -1 * length)
            'R' -> Point(length, 0)
            'L' -> Point(-1 * length, 0)
            else -> throw IllegalArgumentException("Unknown wire path instruction")
        }
    }

    private fun distanceNextIntersection(): Int {
        var minDist = 0
        for (point in intersections) {
            if (point.length != 0 && (minDist == 0 || point.length < minDist)) {
                minDist = point.length
            }
        }
        return minDist
    }

    private fun tracePath(vectors: List<Point>): List<Point> {
        val path = ArrayList<Point>()
        var nextPoint = Point(0, 0)
        vectors.forEach { vector ->
            (nextPoint until (nextPoint + vector)).forEach { point -> path.add(point) }
            nextPoint += vector
        }
        return path
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

    private fun combinedStepsFor(point: Point): Int {
        return wirePath1.indexOf(point) + wirePath2.indexOf(point)
    }
}