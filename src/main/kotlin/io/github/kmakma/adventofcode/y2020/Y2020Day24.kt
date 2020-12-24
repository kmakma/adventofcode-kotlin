package io.github.kmakma.adventofcode.y2020

import io.github.kmakma.adventofcode.utils.Day

fun main() {
    Y2020Day24().solveAndPrint()
}

class Y2020Day24 : Day(2020, 24, "???") {
    private lateinit var inputLines: List<List<Char>>

    override fun initializeDay() {
        inputLines = inputInCharLines()
    }

    override suspend fun solveTask1(): Any {
        val tiles = buildTileMap()
        return tiles.values.count { it } // true == black tile
    }

    override suspend fun solveTask2(): Any {
        val tiles = buildTileMap()
        val neighbours = mutableMapOf<Pair<Int, Int>, List<Pair<Int, Int>>>()
        val blackNeighbours = mutableMapOf<Pair<Int, Int>, Int>()
        for (i in 1..100) {
            updateNeighbours(tiles, neighbours)
            for (tile in tiles.keys) {
                var blackNeighs = 0
                for (neigh in neighbours[tile]!!) {
                    if (tiles.getOrDefault(neigh, false)) blackNeighs++
                }
                blackNeighbours[tile] = blackNeighs
            }
            for ((tile, status) in tiles) {
                val blackNeighs = blackNeighbours[tile]!!
                if (status && (blackNeighs == 0 || blackNeighs > 2)) {
                    tiles[tile] = false
                } else if (!status && blackNeighs == 2) {
                    tiles[tile] = true
                }
            }
        }
        return tiles.values.count { it }
    }

    private fun updateNeighbours(
        tiles: MutableMap<Pair<Int, Int>, Boolean>,
        neighbours: MutableMap<Pair<Int, Int>, List<Pair<Int, Int>>>
    ) {
        val neighs = mutableSetOf<Pair<Int, Int>>()
        for (tile in tiles.keys) {
            val localNeighs = neighbours.getOrPut(tile) { neighboursOf(tile) }
            neighs.addAll(localNeighs)
        }
        neighs.forEach {
            if (!tiles.containsKey(it)) {
                tiles[it] = false
                neighbours.getOrPut(it) { neighboursOf(it) }
            }
        }
    }

    private fun neighboursOf(tile: Pair<Int, Int>): List<Pair<Int, Int>> {
        val x = tile.first
        val y = tile.second
        return listOf(
            Pair(x + 1, y),
            Pair(x + 1, y - 1),
            Pair(x, y - 1),
            Pair(x - 1, y),
            Pair(x - 1, y + 1),
            Pair(x, y + 1)
        )
    }

    private fun buildTileMap(): MutableMap<Pair<Int, Int>, Boolean> {
        val tiles = mutableMapOf<Pair<Int, Int>, Boolean>()
        for (line in inputLines) {
            val tile = parseTile(line)
            tiles[tile] = !tiles.getOrDefault(tile, false)
        }
        return tiles
    }

    private fun parseTile(line: List<Char>): Pair<Int, Int> {
        var x = 0
        var y = 0
        val iter = line.listIterator()
        while (iter.hasNext()) {
            when (iter.next()) {
                'e' -> x++
                'w' -> x--
                's' -> {
                    y--
                    if (iter.next() == 'e') x++
                }
                'n' -> {
                    y++
                    if (iter.next() == 'w') x--
                }
            }
        }
        return Pair(x, y)
    }
}