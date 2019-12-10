package io.github.kmakma.adventofcode.y2019

import io.github.kmakma.adventofcode.y2019.utils.Vector

internal class Y2019Day03() : Y2019Day(
    3,
    "Distance to closest intersection of wires"
) {
    private lateinit var wirePath1: List<Vector>
    private lateinit var wirePath2: List<Vector>
    private lateinit var intersections: Set<Vector>
    
    override fun initializeDay() {
        super.initializeDay()
    }

    override suspend fun solveTask1(): Any? {
        return super.solveTask1()
    }

    override suspend fun solveTask2(): Any? {
        return super.solveTask2()
    }

    //    override suspend fun solve() {
//        setPathsAndIntersection(getInput())
//        resultTask1 = distanceNextIntersection()
//        resultTask2 = fewestStepsToIntersection()
//    }

    private fun setPathsAndIntersection(input: List<List<String>>) {
        val pathVectors = parseInstructionsToVectors(input)
        wirePath1 = tracePath(pathVectors[0])
        wirePath2 = tracePath(pathVectors[1])
        intersections = wirePath1.intersect(wirePath2)
    }

    override fun getInput(): List<List<String>> = csvLinesToLists()

    private fun parseInstructionsToVectors(pathInstructions: List<List<String>>): List<List<Vector>> {
        return pathInstructions.map { instrList -> instrList.map { instr -> parseInstructionToVector(instr) } }
    }

    private fun parseInstructionToVector(instruction: String): Vector {
        require(instruction.length >= 2)
        val length = instruction.substring(1).toInt()
        return when (instruction.first()) {
            'U' -> Vector(0, length)
            'D' -> Vector(0, -1 * length)
            'R' -> Vector(length, 0)
            'L' -> Vector(-1 * length, 0)
            else -> throw IllegalArgumentException("Unknown wire path instruction")
        }
    }

    private fun distanceNextIntersection(): Int {
        var minDist = 0
        for (point in intersections) {
            if (point.manhattanDistance != 0 && (minDist == 0 || point.length < minDist)) {
                minDist = point.manhattanDistance
            }
        }
        return minDist
    }

    private fun tracePath(vectors: List<Vector>): List<Vector> {
        val path = ArrayList<Vector>()
        var nextPoint = Vector(0, 0)
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

    private fun combinedStepsFor(vector: Vector): Int {
        return wirePath1.indexOf(vector) + wirePath2.indexOf(vector)
    }
}