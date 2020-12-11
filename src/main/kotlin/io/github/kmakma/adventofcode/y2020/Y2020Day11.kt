package io.github.kmakma.adventofcode.y2020

import io.github.kmakma.adventofcode.utils.Day

fun main() {
    Y2020Day11().solveAndPrint()
}

private const val EMPTY = 'L'
private const val OCCUPIED = '#'
private const val FLOOR = '.'

class Y2020Day11 : Day(2020, 11, "Seating System") {


    private lateinit var inputList: List<List<Char>>
    private lateinit var seatMap: MutableMap<Pos, Char>


    override fun initializeDay() {
        inputList = inputInCharLines()
        seatMap = mutableMapOf()
        for (i in inputList.indices) {
            for (j in inputList[i].indices) {
                seatMap[Pos(i, j)] = inputList[i][j]
            }
        }
    }

    override suspend fun solveTask1(): Any? {
        var currentSeatMap = seatMap
        var changed: Boolean
        do {
            val result = applyRule(currentSeatMap)
            currentSeatMap = result.first
            changed = result.second
        } while (changed)
        return currentSeatMap.values.count { it == OCCUPIED }
    }

    override suspend fun solveTask2(): Any? {
        var currentSeatMap = seatMap
        var changed = true
        while (changed) {
            val result = applyScndRule(currentSeatMap)
            currentSeatMap = result.first
            changed = result.second
        }
        return currentSeatMap.values.count { it == OCCUPIED }
    }

    private fun applyRule(oldSeating: Map<Pos, Char>): Pair<MutableMap<Pos, Char>, Boolean> {
        val newSeating = mutableMapOf<Pos, Char>()
        var changed = false
        oldSeating.forEach { (pos, tile) ->
            when {
                tile == EMPTY && oldSeating.directNeighbours(pos) == 0 -> {
                    newSeating[pos] = OCCUPIED
                    changed = true
                }
                tile == OCCUPIED && oldSeating.directNeighbours(pos) >= 4 -> {
                    newSeating[pos] = EMPTY
                    changed = true
                }
                else -> newSeating[pos] = tile
            }
        }

        return Pair(newSeating, changed)
    }

    private fun applyScndRule(oldSeating: Map<Pos, Char>): Pair<MutableMap<Pos, Char>, Boolean> {
        val newSeating = mutableMapOf<Pos, Char>()
        var changed = false
        oldSeating.forEach { (pos, tile) ->
            when {
                tile == EMPTY && oldSeating.visibleNeighbours(pos) == 0 -> {
                    newSeating[pos] = OCCUPIED
                    changed = true
                }
                tile == OCCUPIED && oldSeating.visibleNeighbours(pos) >= 5 -> {
                    newSeating[pos] = EMPTY
                    changed = true
                }
                else -> newSeating[pos] = tile
            }
        }
        return Pair(newSeating, changed)
    }
}

private fun Map<Pos, Char>.directNeighbours(pos: Pos): Int {
    var neighbours = 0
    val surroundingPos = pos.surroundingPos()
    for (sPos in surroundingPos) {
        if (this[sPos] == OCCUPIED) neighbours++
    }
    return neighbours
}

private fun Map<Pos, Char>.visibleNeighbours(pos: Pos): Int {
    var neighbours = 0
    var next = pos.upLeft()
    var tile = this[next]
    while (tile == FLOOR) {
        next = next.upLeft()
        tile = this[next]
    }
    if (tile == OCCUPIED) neighbours++

    next = pos.up()
    tile = this[next]
    while (tile == FLOOR) {
        next = next.up()
        tile = this[next]
    }
    if (tile == OCCUPIED) neighbours++

    next = pos.upRight()
    tile = this[next]
    while (tile == FLOOR) {
        next = next.upRight()
        tile = this[next]
    }
    if (tile == OCCUPIED) neighbours++

    next = pos.right()
    tile = this[next]
    while (tile == FLOOR) {
        next = next.right()
        tile = this[next]
    }
    if (tile == OCCUPIED) neighbours++

    next = pos.downRight()
    tile = this[next]
    while (tile == FLOOR) {
        next = next.downRight()
        tile = this[next]
    }
    if (tile == OCCUPIED) neighbours++

    next = pos.down()
    tile = this[next]
    while (tile == FLOOR) {
        next = next.down()
        tile = this[next]
    }
    if (tile == OCCUPIED) neighbours++

    next = pos.downLeft()
    tile = this[next]
    while (tile == FLOOR) {
        next = next.downLeft()
        tile = this[next]
    }
    if (tile == OCCUPIED) neighbours++

    next = pos.left()
    tile = this[next]
    while (tile == FLOOR) {
        next = next.left()
        tile = this[next]
    }
    if (tile == OCCUPIED) neighbours++

    return neighbours
}

private data class Pos(val row: Int, val col: Int) {
    fun surroundingPos(): List<Pos> {
        val surroundingPos = mutableListOf<Pos>()
        for (r in (row - 1)..(row + 1)) {
            for (c in (col - 1)..(col + 1)) {
                if (r != row || c != col) surroundingPos.add(Pos(r, c))
            }
        }
        return surroundingPos
    }

    fun upLeft() = Pos(row - 1, col - 1)
    fun up() = Pos(row - 1, col)
    fun upRight() = Pos(row - 1, col + 1)
    fun right() = Pos(row, col + 1)
    fun downRight() = Pos(row + 1, col + 1)
    fun down() = Pos(row + 1, col)
    fun downLeft() = Pos(row + 1, col - 1)
    fun left() = Pos(row, col - 1)
}

