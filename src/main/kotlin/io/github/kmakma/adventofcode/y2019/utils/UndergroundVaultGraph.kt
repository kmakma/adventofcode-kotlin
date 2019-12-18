package io.github.kmakma.adventofcode.y2019.utils

import io.github.kmakma.adventofcode.y2019.utils.MazeTile.*
import io.github.kmakma.adventofcode.y2019.utils.UndergroundVaultGraph.Key

internal class UndergroundVaultGraph {

    lateinit var entrance: Vector2D
    private lateinit var keys: List<Key>
    private lateinit var closedDoors: Map<Char, Boolean>


    fun setKeysAndDoor(keys: List<Key>) {
        closedDoors = keys.map { key -> key.id.toUpperCase() to false }.toMap()
        this.keys = keys
    }

    internal data class Key(val id: Char, val paths: List<Path>) {

    }

    inner class Path(val to: Char, val distance: Int, val doors: List<Char>) {
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

private class GraphBuilder(private val inputLines: List<List<Char>>) {
    val graph = UndergroundVaultGraph()

    fun build() {
        val entranceAndKeys = findEntranceAndKeys()
        println(entranceAndKeys.second)
        // first value is the entrance vector
        graph.entrance = entranceAndKeys.first
        // for each key (second value) build a key
        graph.setKeysAndDoor(entranceAndKeys.second.map { (char, vector) -> buildKey(char, vector) })
    }

    private fun findEntranceAndKeys(): Pair<Vector2D, Map<Char, Vector2D>> {
        var entrance: Vector2D? = null
        val keys = inputLines.mapIndexed { lineIndex, line ->
            line.mapIndexedNotNull { charIndex, char ->
                when {
                    char == ENTRANCE.char -> {
                        entrance = Vector2D(charIndex, lineIndex);
                        null
                    }
                    char.isLowerCase() -> char to Vector2D(charIndex, lineIndex)
                    else -> null
                }
            }
        }.flatten().toMap()
        if (entrance == null) error("no entrance found")
        return Pair(entrance!!, keys)
    }

    // variables for building a key:

    private lateinit var openNodes: MutableList<Pair<Vector2D, Int>>
    private lateinit var visited: MutableSet<Vector2D>
    private lateinit var doorDependencies: MutableMap<Vector2D, MutableList<Char>>
    private lateinit var paths: MutableList<UndergroundVaultGraph.Path>

    private fun buildKey(id: Char, vector: Vector2D): Key {
        println(id)
        openNodes = mutableListOf(vector to 0)
        visited = mutableSetOf()
        doorDependencies = mutableMapOf()
        paths = mutableListOf()

        openNodes.removeAt(0).let {
            openNodes.addAll(surroundingsOf(it))
            visited.add(it.first)
        }

        while (openNodes.isNotEmpty()) {
            analyseNode(openNodes.removeAt(0))
        }

        return Key(id, paths)
    }

    private fun analyseNode(current: Pair<Vector2D, Int>) {
        println(current)
        when (current.first.toMazeTile()) {
            OPEN, ENTRANCE -> addNode(current)
            KEY -> graph.Path(
                charOf(current.first),
                current.second,
                doorDependencies.getOrDefault(current.first, listOf<Char>())
            )
            DOOR -> {
                doorDependencies.getOrPut(current.first) { mutableListOf() }.add(charOf(current.first))
                addNode(current)
            }
            WALL -> error("encountered an unexpected wall at ${current.first}")
        }
    }

    private fun addNode(current: Pair<Vector2D, Int>) {
        surroundingsOf(current).let { surroundings ->
            openNodes.addAll(surroundings)
            doorDependencies[current.first]?.let { doors ->
                surroundings.forEach { vectorDist ->
                    doorDependencies.getOrPut(vectorDist.first) { mutableListOf() }.addAll(doors)
                }
            }
            visited.add(current.first)
        }
        visited.add(current.first)
    }

    private fun surroundingsOf(vectorDistance: Pair<Vector2D, Int>): List<Pair<Vector2D, Int>> {
        return Vector2D.basicDirections.mapNotNull { dir ->
            (vectorDistance.first + dir.vector).let {
                if (it.toMazeTile() == WALL) null
                else it to (vectorDistance.second + 1)
            }
        }
    }

    private fun charOf(vector: Vector2D): Char = inputLines[vector.y][vector.x]


    private fun Vector2D.toMazeTile(): MazeTile {
        charOf(this).let { mazeTile ->
            return when {
                mazeTile == OPEN.char -> OPEN
                mazeTile == ENTRANCE.char -> ENTRANCE
                mazeTile == WALL.char -> WALL
                mazeTile.isLowerCase() -> KEY
                mazeTile.isUpperCase() -> DOOR
                else -> error("invalid/unknown maze tile ($mazeTile)")
            }
        }
    }

}

private enum class MazeTile(val char: Char) {
    OPEN('.'), WALL('#'), ENTRANCE('@'), KEY('a'), DOOR('A')
}