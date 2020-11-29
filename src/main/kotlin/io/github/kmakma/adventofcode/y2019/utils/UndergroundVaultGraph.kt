package io.github.kmakma.adventofcode.y2019.utils

import io.github.kmakma.adventofcode.y2019.utils.Direction.LEFT
import io.github.kmakma.adventofcode.y2019.utils.Direction.UP
import io.github.kmakma.adventofcode.y2019.utils.MazeTile.*
import io.github.kmakma.adventofcode.y2019.utils.UndergroundVaultGraph.Path

internal class UndergroundVaultGraph {

    // TODO spannbaum

    lateinit var entrance: Vector2D
    private lateinit var keys: List<Key>
    private lateinit var closedDoors: Map<Char, Boolean>


    fun setKeysAndDoor(keys: List<Key>) {
        closedDoors = keys.map { key -> key.id.toUpperCase() to false }.toMap()
        this.keys = keys
    }

    internal data class Key(val id: Char, val paths: List<Path>) {

    }

    inner class Path(val to: Char, val distance: Int, val doors: Set<Char> = emptySet()) {
        fun isOpen(): Boolean {
            doors.forEach { if (closedDoors[it] ?: error("Couldn't find door status")) return false }
            return true
        }

    }


    companion object {
        internal fun build(inputLines: List<List<Char>>) {
            GraphBuilder(inputLines).build() // TODO
        } // build end
    } // companion end
} // Graph class end

/**
 * graphbuilder
 */

private class GraphBuilder(private val inputLines: List<List<Char>>) {
    val graph = UndergroundVaultGraph()
    var buildingNodeGraph = mutableMapOf<Vector2D, BuildingNode>()

    fun build() {
        buildDistance1Graph()
        println("build graph; size = ${buildingNodeGraph.size}")
        reduceOpenTiles()
        println("reduced open tiles; size = ${buildingNodeGraph.size}")
        convertDirectPaths()
        println("converted direct paths; size = ${buildingNodeGraph.size}")
        reduceDoorTiles()
        // TODO do smth with entrance

        // old:
//        val entranceAndKeys = findEntranceAndKeys()
//        println(entranceAndKeys.second)
//        // first value is the entrance vector
//        graph.entrance = entranceAndKeys.first
//        // for each key (second value) build a key
//        graph.setKeysAndDoor(entranceAndKeys.second.map { (char, vector) -> buildKey(char, vector) })
    }

    private fun buildDistance1Graph() {
        inputLines.forEachIndexed { lineIndex, line ->
            line.forEachIndexed { charIndex, char ->
                if (char != WALL.char) {
                    // check up(dir down) and left
                    BuildingNode(Vector2D(charIndex, lineIndex), char, char.toMazeTile()).let { node ->
                        buildingNodeGraph[node.vector] = node
                        updateNeighbors(node)
                    }
                }
            }
        }
    }

    private fun updateNeighbors(node: BuildingNode) {
        buildingNodeGraph[node.vector + LEFT.vector2D]?.let {
            node.addNeighbor(it)
            it.addNeighbor(node)
        }
        buildingNodeGraph[node.vector - UP.vector2D]?.let {
            node.addNeighbor(it)
            it.addNeighbor(node)
        }
    }

    private fun reduceOpenTiles() {
        buildingNodeGraph.values
            .filter { node -> node.mazeTile == OPEN }
            .forEach { node ->
                node.neighbors.forEach { (neighbor, _) ->
                    neighbor.replaceOpen(node)
                    buildingNodeGraph.remove(node.vector)
                }
            }
    }

    private fun convertDirectPaths() {
        buildingNodeGraph.values
            .filter { node -> node.mazeTile == KEY }
            .forEach { node ->
                node.convertDirectPaths()
            }
    }

    private fun reduceDoorTiles() {
        buildingNodeGraph.values
            .filter { node -> node.mazeTile == KEY }
            .forEach { node ->
                node.convertDoors()
            }
    }

    private fun charOf(vector: Vector2D): Char = inputLines[vector.y][vector.x]

    private fun Char.toMazeTile(): MazeTile {
        return when {
            this == OPEN.char -> OPEN
            this == ENTRANCE.char -> ENTRANCE
            this == WALL.char -> WALL
            this.isLowerCase() -> KEY
            this.isUpperCase() -> DOOR
            else -> error("invalid/unknown maze tile ($this)")
        }
    }

    private fun Vector2D.toMazeTile(): MazeTile = charOf(this).toMazeTile()

    private inner class BuildingNode(val vector: Vector2D, val char: Char, val mazeTile: MazeTile) {
        val neighbors = mutableMapOf<BuildingNode, Int>()
        val paths = mutableListOf<Path>()

        fun addNeighbor(node: BuildingNode) {
            neighbors[node] = 1
        }

        fun replaceOpen(node: BuildingNode) {
            val oldDist = neighbors.remove(node)!!
            node.neighbors.filterKeys { it != this }.forEach { (newNeighbor, distance) ->
                neighbors[newNeighbor].let { existingDist ->
                    (distance + oldDist).let { newDist ->
                        if (existingDist == null || existingDist > newDist)
                            neighbors[newNeighbor] = newDist
                    }
                }
            }
        }

        fun convertDirectPaths() {
            paths.addAll(neighbors
                .filterKeys { node -> node.mazeTile == KEY }
                .map { (node, distance) ->
                    neighbors.remove(node)
                    graph.Path(node.char, distance)
                })
        }

        fun convertDoors() {
            var nextDoors: MutableMap<BuildingNode, Pair<Int, MutableSet<Char>>>
            var doors = neighbors
                .filterKeys { node -> node.mazeTile == DOOR }
                .mapValues { (_, distance) ->
                    Pair(distance, mutableSetOf<Char>())
                }
            while (doors.isNotEmpty()) {
                nextDoors = mutableMapOf()
                // map to pair of lists (one for direct paths, one for new door)
                doors.map { (doorNode, distAndBlockingDoors) ->
                    nextDoors.putAll(splitDirectPathsDoors(doorNode, distAndBlockingDoors))
                }
                doors = nextDoors
            }
        }

        fun splitDirectPathsDoors(
            doorNode: BuildingNode,
            distAndBlockingDoors: Pair<Int, MutableSet<Char>>
        ): MutableMap<BuildingNode, Pair<Int, MutableSet<Char>>> {
            val nextDoors = mutableMapOf<BuildingNode, Pair<Int, MutableSet<Char>>>()
            val blockingDoors = mutableSetOf(doorNode.char)
            blockingDoors.addAll(distAndBlockingDoors.second)
            doorNode.neighbors
                .filterKeys { it != this }
                .forEach { (neighbor, distance) ->
                    val newDist = distAndBlockingDoors.first + distance
                    when (neighbor.mazeTile) {
                        DOOR -> nextDoors[neighbor] = Pair(newDist, blockingDoors)
                        KEY -> paths.add(graph.Path(neighbor.char, newDist, blockingDoors))
                        else -> error("unexpected node; main node ${doorNode.char}, unexpected ${neighbor.char}")
                    }
                }
            return nextDoors
        }
    }

}

private enum class MazeTile(val char: Char) {
    OPEN('.'), WALL('#'), ENTRANCE('@'), KEY('a'), DOOR('A')
}