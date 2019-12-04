package io.github.kmakma.adventofcode.y2019

class Y2019Day03 : Y2019Day(
    3,
    "Distance to closest intersection of wires",
    "unknown task 2" // TODO describe task 2
) {
    private lateinit var pathVectors: List<List<Point>>

    override fun solve() {
        parseInstructionsToVectors(getInput())
        resultTask1 = distanceNextIntersection()
//            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        // todo fun
    }

    override fun getInput(): List<List<String>> = csvLinesToLists()


    private fun parseInstructionsToVectors(pathInstructions: List<List<String>>) {
        pathVectors = pathInstructions.map { instrList -> instrList.map { instr -> parseInstructionToVector(instr) } }
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
        val wirePath1 = tracePath(pathVectors[0])
        val wirePath2 = tracePath(pathVectors[1])
        val intersection = wirePath1.intersect(wirePath2)
        var minDist: Int = 0
        for (point in intersection) {
            if (point.length != 0 && (minDist == 0 || point.length < minDist)) {
                minDist = point.length
            }
        }
        return minDist
    }

    private fun tracePath(vectors: List<Point>): List<Point> {
        val path = ArrayList<Point>()
//        path.add(Point(0, 0))
//        vectors.forEach { vector -> (path.last()..(path.last() + vector)).forEach { point -> path.add(point) } }
        // TODO attention! duplicate values between vectors
        var nextPoint = Point(0, 0)
        vectors.forEach { vector ->
            (nextPoint until (nextPoint + vector)).forEach { point -> path.add(point) }
            nextPoint += vector
        }
        return path
    }
}