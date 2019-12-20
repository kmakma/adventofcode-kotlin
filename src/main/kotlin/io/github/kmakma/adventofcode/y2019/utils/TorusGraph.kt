package io.github.kmakma.adventofcode.y2019.utils

import io.github.kmakma.adventofcode.y2019.utils.Vector2D.Companion.basicDirections

private const val NOT_VISITED = -1
private const val IMPASSABLE = -2

private const val VOID = ' '
private const val WALL = '#'
private const val PATH = '.'


internal class TorusGraph internal constructor(
    pathMap: List<List<Int>>,
    private val portalPoints: Map<Vector2D, String>
) {
    private val portalLabels: Map<String, List<Vector2D>>
    private val pathDistances: List<MutableList<Int>>

    init {
        portalLabels = portalPoints.keys
            .groupBy { portalPoint -> portalPoints[portalPoint] ?: error("should not be null") }
        pathDistances = pathMap.map { line -> line.toMutableList() }
    }

    companion object {
        internal fun build(map: List<List<Char>>): TorusGraph = TorusGraphBuilder(map).buildTorusGraph()
    }

    fun shortestPathLength(startLabel: String, endLabel: String): Int {
        val startVector = getSingleVector(startLabel)
        val endVector = getSingleVector(endLabel)

        val openPoints = mutableListOf(startVector)
        setDistance(startVector, 0)

        while (openPoints.isNotEmpty()) {
            with(openPoints.removeAt(0)) {
                if (this == endVector) return distanceAt(this)
                val newDistance = distanceAt(this) + 1
                neighborsOf(this).forEach { neighbor ->
                    println("n: $neighbor; dist: $newDistance")
                    openPoints.add(neighbor)
                    setDistance(neighbor, newDistance)
                }
            }
        }
        return -1
    }

    private fun printGraph() {
        pathDistances.forEach { line ->
            if (line.any { it >= 0 }) {
                line.forEach { if (it >= 0) print(it.toString().padStart(4, '-')) else print("    ") }
                println()
            }
        }
    }

    private fun getSingleVector(label: String): Vector2D {
        portalLabels[label]
            .let { vectorList ->
                if (vectorList == null || vectorList.size != 1) error("bad label vectors ($label)")
                else return vectorList.first()
            }
    }

    private fun setDistance(vector: Vector2D, distance: Int) {
        pathDistances[vector.y][vector.x] = distance
    }

    private fun neighborsOf(vector: Vector2D): List<Vector2D> {
        val neighbors = basicDirections.mapNotNull { dir ->
            val potNeighbor = vector + dir
            val distance = distanceAt(potNeighbor)
            if (distance == NOT_VISITED) potNeighbor
            else null
        }.toMutableList()
        getOtherPortalOrNull(vector)?.let {
            neighbors.add(it)
        }
        return neighbors
    }

    private fun distanceAt(vector: Vector2D): Int {
        return pathDistances[vector.y][vector.x]
    }

    private fun getOtherPortalOrNull(sourceVector: Vector2D): Vector2D? {
        portalPoints[sourceVector].let { label ->
            if (label == null) return null
            portalLabels[label].let { portalList ->
                return if ((portalList?.size ?: 0) < 2) null
                else portalList?.first { portalVector -> sourceVector != portalVector }
            }
        }
    }
}

internal class TorusGraphBuilder(private val map: List<List<Char>>) {
    private val portalLabelPoints = mutableListOf<Vector2D>()
    private val portalsPoints: Map<Vector2D, String>
    private val pathMap: List<List<Int>>

    init {
        pathMap = buildPathMap()
        portalsPoints = buildPortals()
    }

    internal fun buildTorusGraph(): TorusGraph = TorusGraph(pathMap, portalsPoints)

    private fun buildPathMap(): List<List<Int>> {
        return map.mapIndexed { lineIndex, line ->
            line.mapIndexed { charIndex, char ->
                when (char) {
                    VOID, WALL -> IMPASSABLE
                    PATH -> NOT_VISITED
                    else -> {
                        if (!char.isUpperCase()) error("unexpected element")
                        portalLabelPoints.add(Vector2D(charIndex, lineIndex))
                        IMPASSABLE
                    }
                }
            }
        }
    }

    private fun buildPortals() = portalLabelPoints.mapNotNull { labelPoint -> buildPortal(labelPoint) }.toMap()

    private fun buildPortal(labelPoint: Vector2D): Pair<Vector2D, String>? {
        Direction.values().forEach { dir ->
            val point = labelPoint + dir.vector2D
            if (isPath(point)) {
                val labelPointChar = charAt(labelPoint)
                val otherChar = charAt(labelPoint - dir.vector2D)
                val label = String(
                    when (dir) {
                        Direction.UP -> charArrayOf(otherChar, labelPointChar)
                        Direction.DOWN -> charArrayOf(labelPointChar, otherChar)
                        Direction.RIGHT -> charArrayOf(otherChar, labelPointChar)
                        Direction.LEFT -> charArrayOf(labelPointChar, otherChar)
                    }
                )
                return Pair(point, label)
            }
        }
        return null
    }

    private fun charAt(point: Vector2D) = map[point.y][point.x]

    private fun isPath(point: Vector2D): Boolean {
        map.getOrNull(point.y)?.let { line -> return line.getOrNull(point.x) == PATH }
        return false
    }

}