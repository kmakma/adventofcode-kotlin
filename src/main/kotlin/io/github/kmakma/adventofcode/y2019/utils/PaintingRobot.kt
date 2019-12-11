package io.github.kmakma.adventofcode.y2019.utils

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

private const val BLACK = 0L
private const val WHITE = 1L
private const val TURN_RIGHT = 1L
private const val TURN_LEFT = 0L


@ExperimentalCoroutinesApi
class PaintingRobot private constructor(intcodeProgram: List<Long>) {

    private val computerOutput = ComputerIO()
    private val computerInput = ComputerIO()
    private val intcodeComputer = IntcodeComputer.Builder(intcodeProgram, computerInput, computerOutput).build()
    private val robotCanvas = mutableMapOf<Vector, Long>()
    private var pos = Vector(0, 0)
    private var dir = Vector(0, 1)

    companion object {
        internal suspend fun paintJobsWithBlackStart(intcodeProgram: List<Long>): Int {
            return PaintingRobot(intcodeProgram)
                .startWith(BLACK)
                .paint()
                .paintedTiles()
        }

        internal suspend fun paintResultWithWhiteStart(intcodeProgram: List<Long>): Array<Array<Int>> {
            return PaintingRobot(intcodeProgram)
                .startWith(WHITE)
                .paint()
                .canvasToArray()
        }
    }

    private fun canvasToArray(): Array<Array<Int>> {
        var minX = 0
        var maxX = 0
        var minY = 0
        var maxY = 0
        // get dimensions of canvas
        for (entry in robotCanvas) {
            if (entry.key.x > maxX) maxX = entry.key.x
            if (entry.key.x < minX) minX = entry.key.x
            if (entry.key.y > maxY) maxY = entry.key.y
            if (entry.key.y < minY) minY = entry.key.y
        }
        // build new canvas as map with lower-left-corner = (0,0) and lower-right-corner = (x,-y)
        val sizeX = maxX - minX + 1 // |maxX| + |minX| + 1 (for the 0)
        val sizeY = maxY - minY + 1
        val moveVec = Vector(-minX, -maxY)
        val zeroedCanvas = robotCanvas.mapKeys { it.key + moveVec }
        // build new canvas as array so that upper-left-corner = (0,0) and lower-right = (x,y)
        return Array(sizeY) { y ->
            Array(sizeX) { x ->
                val vec = Vector(x, -sizeY + y + 1)
                zeroedCanvas.getOrDefault(vec, -1).toInt()
            }
        }
    }

    private fun paintedTiles(): Int = robotCanvas.size

    private fun startWith(colorCode: Long): PaintingRobot {
        robotCanvas[pos] = colorCode
        return this
    }

    private suspend fun paint(): PaintingRobot = coroutineScope {
        val computer = async { intcodeComputer.run() }
        val output = async {
            while (!computerOutput.isClosedForReceive()) {
                computerInput.send(colorAtPos())
                paintPos(computerOutput.receive())
                turn(computerOutput.receive())
                move()
            }
        }
        computer.await()
        output.await()
        return@coroutineScope this@PaintingRobot
    }

    private fun paintPos(colorCode: Long) {
        if (colorCode == BLACK || colorCode == WHITE) robotCanvas[pos] = colorCode
        else error("invalid color: $colorCode")
    }


    private fun turn(directionCode: Long) {
        dir = when (directionCode) {
            TURN_LEFT -> dir.rotated(90)
            TURN_RIGHT -> dir.rotated(-90)
            else -> error("invalid direction: $directionCode")
        }
    }

    private fun move() {
        pos += dir
    }

    private fun colorAtPos(): Long {
        return robotCanvas.getOrPut(pos) { BLACK }
    }

}