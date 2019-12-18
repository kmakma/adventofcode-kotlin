package io.github.kmakma.adventofcode.y2019.utils

import io.github.kmakma.adventofcode.y2019.utils.Vector2D.Companion.Direction.RIGHT
import io.github.kmakma.adventofcode.y2019.utils.Vector2D.Companion.Direction.UP

class UndergroundVault(inputLines: List<String>) {
    private val wall = '#'
    private val open = '.'
    private val entrance = '@'

    private val doors = mutableMapOf<Char, VaultPosition>()
    private var pathTiles: MutableMap<Vector2D, VaultPosition>
    private lateinit var keys: List<VaultPosition>
    private lateinit var entryPoint: VaultPosition


    init {
        // TODO 1. to char matrix
        //      2. find (first) not wall
        //      3. iterate through
        //      4.
        pathTiles = buildPathMap(inputLines)
        buildGraph()
        println(pathTiles)
    }


    private fun buildPathMap(inputLines: List<String>): MutableMap<Vector2D, VaultPosition> {
        var x = 0
        var y = 0
        val paths = mutableMapOf<Vector2D, VaultPosition>()
        inputLines.forEach { line ->
            line.forEach {
                buildVaultPos(x, y, it).let { vaultPos ->
                    if (vaultPos != null) {
                        paths[vaultPos.position] = vaultPos
                    }
                }
                x++
            }
            x = 0
            y++
        }
        return paths
    }

    private fun buildGraph() {
        // find for all tiles adjacent neighbors and filter keys and doors
        keys = pathTiles.values.filter {
            it.findAdjacentNeighbors(pathTiles)
            if (it.id.isUpperCase()) doors[it.id] = it
            it.id.isLowerCase()
        }
        pathTiles.values.forEach { println("${it.position}, ${it.id}, neighbors.size: ${it.neighbors.size}") } // TODO remove this prinln
        // remove open tiles
        val openTiles = pathTiles.values.filter { it.id == open }.toMutableList()
        while (openTiles.isNotEmpty()) {
            with(openTiles.removeAt(0)) {
                pathTiles.remove(this.position)
                killThyself()
            }
        }

        // actually build the graph now
        val closedDoors = doors.keys.associateWith { false }


    }

    private fun buildVaultPos(x: Int, y: Int, tile: Char): VaultPosition? {
        return when {
            tile == wall -> null
            tile == open || tile.isLowerCase() || tile.isUpperCase() -> VaultPosition(tile, Vector2D(x, y))
            tile == entrance -> {
                val vp = VaultPosition(tile, Vector2D(x, y))
                entryPoint = vp
                vp
            }
            else -> error("unknown field tile in vault")

        }
    }

}

internal data class VaultPosition(val id: Char, val position: Vector2D) {
    val neighbors = mutableMapOf<VaultPosition, Int>()

    fun findAdjacentNeighbors(pathTiles: Map<Vector2D, VaultPosition>) {
        // check for neighbor on the right
        pathTiles[position + RIGHT.vector].let {
            if (it != null) {
                neighbors[it] = 1
                it.setNeighbor(this, 1)
            }
        }
        // check for neighbor below (UP since y=-y)
        pathTiles[position + UP.vector].let {
            if (it != null) {
                neighbors[it] = 1
                it.setNeighbor(this, 1)
            }
        }
    }

    private fun setNeighbor(neighbor: VaultPosition, distance: Int) {
        neighbors[neighbor] = distance
    }

    private fun replaceNeighbor(oldNeighbor: VaultPosition, newNeighbor: VaultPosition, distance: Int) {
        neighbors.remove(oldNeighbor)
        neighbors[newNeighbor] = distance
    }

    fun killThyself() {
        neighbors.keys.toList().forEach {
            it.replaceNeighbor(this, neighbors)
            println("killed: ${it.position}")
        }
    }

    private fun replaceNeighbor(oldNeighbor: VaultPosition, newNeighbors: MutableMap<VaultPosition, Int>) {
        val distance = neighbors.remove(oldNeighbor)!!
        newNeighbors.forEach { (newNeighbor, distFromOld) ->
            if (newNeighbor == this) return@forEach
            val newDistance = distFromOld + distance
            val distToNewNeighbor = neighbors[newNeighbor]
            if (distToNewNeighbor == null || distToNewNeighbor > newDistance) {
                neighbors[newNeighbor] = newDistance
            }
        }
    }

}
