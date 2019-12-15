package io.github.kmakma.adventofcode.y2019.utils

import io.github.kmakma.adventofcode.y2019.utils.RepairDroid.Tile.*

internal class RepairDroid(intcodeProgram: List<Long>) {
    private val intcodeComputer = IntcodeComputerV2(intcodeProgram)
    private val surroundings = mutableMapOf<Vector2D, Tile>()
    private var position = Vector2D(0, 0)
    private val directions by lazy { Direction.values().apply { sortBy { it.dirCode } } }


    fun activate() {
        surroundings[position] = EMPTY
        // tODO
        //  1. map the room
        //  2. (for now) print the room
        intcodeComputer.run()
        mapSurroundings()
        printSurroundings()

    }

    private fun mapSurroundings(direction: Direction? = null) {
        if (direction != null) {
            val newPosition = position + direction.vector
            if (surroundings.getOrDefault(newPosition, null) != null) return
            // run program, and move in direction (or at least map the new tile)
            intcodeComputer.runWith(direction.dirCode)
            if (!mapAndMove(newPosition, intcodeComputer.nextTile())) return
        }
        // mapped new tile and moved in direction
        for (newDirection in directions) {
            mapSurroundings(newDirection)
        }
        // move back:
        if (direction != null) {
            intcodeComputer.runWith(direction.opposite().dirCode)
            position -= direction.vector
        }
    }

    private fun printSurroundings() {
        var minX = 0
        var maxX = 0
        var minY = 0
        var maxY = 0
        surroundings.keys.forEach {
            if (it.x < minX) minX = it.x
            if (it.x > maxX) maxX = it.x
            if (it.y < minY) minY = it.y
            if (it.y > maxY) maxY = it.y
        }
        println()
        for (y in maxY downTo minY) {
            for (x in minX..maxX) {
                if (x == position.x && y == position.y) {
                    print("D")
                } else {
                    print(surroundings.getOrDefault(Vector2D(x, y), UNKNOWN).string)
                }
            }
            println()
        }
    }

    /**
     * maps tile in direction (if not mapped) and moves. Returns whether move was executed
     */
    private fun mapAndMove(position: Vector2D, newTile: Tile): Boolean {
        surroundings[position] = newTile
        return if (newTile == WALL) {
            printSurroundings()

            false
        } else {
            this.position = position
            printSurroundings()

            true
        }
    }


    private fun Long.toTile(): Tile {
        return when (this) {
            0L -> WALL
            1L -> EMPTY
            2L -> O2SYSTEM
            else -> error("unknown tile")
        }
    }

    private fun IntcodeComputerV2.nextTile(): Tile = output.removeAt(0).toTile()

    private enum class Tile(val string: String) {
        EMPTY("."), O2SYSTEM("O"), WALL("#"), UNKNOWN(" ")

    }

    private enum class Direction(val dirCode: Long, val vector: Vector2D) {
        NORTH(1, Vector2D(0, 1)),
        SOUTH(2, Vector2D(0, -1)),
        WEST(3, Vector2D(-1, 0)),
        EAST(4, Vector2D(1, 0));

        fun opposite(): Direction {
            return when (this) {
                NORTH -> SOUTH
                SOUTH -> NORTH
                WEST -> EAST
                EAST -> WEST
            }
        }
    }

}
