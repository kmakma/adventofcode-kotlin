package io.github.kmakma.adventofcode.y2019.utils

import io.github.kmakma.adventofcode.y2019.utils.RepairDroid.Tile.*

internal class RepairDroid(intcodeProgram: List<Long>) {
    private val intcodeComputer = IntcodeComputerV2(intcodeProgram)
    private val surroundings = mutableMapOf<Vector2D, Tile>()
    private val distanceFromO2 = mutableMapOf<Vector2D, Int>()

    private var position = Vector2D(0, 0)
    private lateinit var o2System: Vector2D

    private val directions by lazy { Direction.values().apply { sortBy { it.dirCode } } }


    fun activate() {
        surroundings[position] = EMPTY
        intcodeComputer.run()
        mapSurroundings()
        mapDistanceFromO2()
//        printSurroundings()
//        printDistances()
    }

    fun distanceToO2System(): Int = distanceFromO2[Vector2D(0, 0)] ?: -1

    fun timeToFillWithO2(): Int = distanceFromO2.values.max() ?: -1


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
            intcodeComputer.nextTile()
            position -= direction.vector
        }
    }

    private fun mapDistanceFromO2() {
        distanceFromO2[o2System] = 0
        val openPositions = mutableListOf(o2System)
        while (openPositions.isNotEmpty()) {
            val currentPosition = openPositions.removeAt(0)
            accessibleNeighbors(currentPosition).forEach {
                distanceFromO2.getOrPut(it) {
                    openPositions.add(it)
                    distanceFromO2[currentPosition]!! + 1
                }
            }
        }
    }

    private fun accessibleNeighbors(position: Vector2D): List<Vector2D> {
        val neighbors = mutableListOf<Vector2D>()
        for (direction in directions) {
            with(position + direction.vector) {
                if (surroundings[this] != WALL) {
                    neighbors.add(this)
                }
            }
        }
        return neighbors
    }

    @Suppress("DuplicatedCode")
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
                when {
                    x == position.x && y == position.y -> print("D")
                    x == 0 && y == 0 -> print("S")
                    else -> print(surroundings.getOrDefault(Vector2D(x, y), UNKNOWN).string)
                }
            }
            println()
        }
    }

    @Suppress("DuplicatedCode")
    private fun printDistances() {
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
                with(Vector2D(x, y)) {
                    if (surroundings[this] != WALL) {
                        print(distanceFromO2[this].toString().padStart(5, ' '))
                    } else {
                        print("     ")
                    }
                }
            }
            println()
        }
    }

    private fun mapAndMove(position: Vector2D, newTile: Tile): Boolean {
        surroundings[position] = newTile
        if (newTile == O2SYSTEM) o2System = position
        return if (newTile == WALL) {
            false
        } else {
            this.position = position
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
