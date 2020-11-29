package io.github.kmakma.adventofcode.y2019.utils

import io.github.kmakma.adventofcode.y2019.utils.Vector2D.Companion.basicDirections

private const val NOT_VISITED = -1
private const val IMPASSABLE = -2

private const val VOID = ' '
private const val WALL = '#'
private const val PATH = '.'


internal open class TorusGraph internal constructor(
    pathMap: List<List<Int>>,
    protected val portalPoints: Map<Vector2D, String>
) {
    private val portalLabels: Map<String, List<Vector2D>>
    protected val pathDistances: List<MutableList<Int>>
    protected val openPoints: MutableList<Vector2D>
    private var endVector: Vector2D?


    init {
        portalLabels = portalPoints.keys
            .groupBy { portalPoint -> portalPoints[portalPoint] ?: error("should not be null") }
        pathDistances = pathMap.map { line -> line.toMutableList() }
        openPoints = mutableListOf()

        endVector = getSingleVector("ZZ")
    }

    companion object {
        internal fun build(map: List<List<Char>>): TorusGraph = TorusGraphBuilder(map).buildTorusGraph()
    }

    open fun shortestPathLength(): Int {
        val startVector = getSingleVector("AA")
        openPoints.add(startVector)
        setDistance(startVector, 0)

        clearOpenPoints()

        return distanceAt(endVector!!)
    }

    protected fun clearOpenPoints() {
        while (openPoints.isNotEmpty()) {
            with(openPoints.removeAt(0)) {
                if (this == endVector) return
                val newDistance = distanceAt(this) + 1
                neighborsOf(this).forEach { neighbor ->
                    openPoints.add(neighbor)
                    setDistance(neighbor, newDistance)
                }
            }
        }
    }

    private fun printGraph() {
        pathDistances.forEach { line ->
            if (line.any { it >= 0 }) {
                line.forEach { if (it >= 0) print(it.toString().padStart(4, '-')) else print("    ") }
                println()
            }
        }
    }

    protected fun getSingleVector(label: String): Vector2D {
        portalLabels[label]
            .let { vectorList ->
                if (vectorList == null || vectorList.size != 1) error("bad label vectors ($label)")
                else return vectorList.first()
            }
    }

    protected fun setDistance(vector: Vector2D, distance: Int) {
        pathDistances[vector.y][vector.x] = distance
    }

    protected open fun neighborsOf(vector: Vector2D): List<Vector2D> {
        val neighbors = directNeighborsOf(vector)
        getOtherPortalOrNull(vector)?.let {
            neighbors.add(it)
        }
        return neighbors
    }

    protected fun directNeighborsOf(vector: Vector2D): MutableList<Vector2D> {
        return basicDirections.mapNotNull { dir ->
            val potNeighbor = vector + dir
            val distance = distanceAt(potNeighbor)
            if (distance == NOT_VISITED) potNeighbor // todo check if distance can be smallered
            else null
        }.toMutableList()
    }

    protected fun distanceAt(vector: Vector2D): Int {
        return pathDistances[vector.y][vector.x]
    }

    protected fun getOtherPortalOrNull(sourceVector: Vector2D): Vector2D? {
        portalPoints[sourceVector].let { label ->
            if (label == null) return null
            portalLabels[label].let { portalList ->
                return if ((portalList?.size ?: 0) < 2) null
                else portalList?.first { portalVector -> sourceVector != portalVector }
            }
        }
    }
}

internal class RecursiveTorusGraph(
    pathMap: List<List<Int>>,
    portalPoints: Map<Vector2D, String>,
    private val layer: Int,
    private val outerGraph: RecursiveTorusGraph?
) : TorusGraph(pathMap, portalPoints) {

    private var outerPortals = mutableMapOf<Vector2D, Int>()
    private var innerPortals = mutableMapOf<Vector2D, Int>()
    private val incomingPortals = mutableListOf<Vector2D>()

    private val innerGraph: RecursiveTorusGraph by lazy {
        RecursiveTorusGraph(pathMap, portalPoints, layer + 1, this)
    }

    override fun shortestPathLength(): Int {
        val startVector = getSingleVector("AA")
        openPoints.add(startVector)
        setDistance(startVector, 0)

        while (openPoints.isNotEmpty()) {
            clearOpenPoints()
            val tempInnerPortals = innerPortals
            innerPortals = mutableMapOf()
            innerGraph.addIncomingPortals(tempInnerPortals)
            innerGraph.clearPoints()
        }

        return -1
    }

    private fun addIncomingPortals(portals: Map<Vector2D, Int>) {
        portals.forEach { (portalVector, distance) ->
            val originalDistance = distanceAt(portalVector)
            if (originalDistance == NOT_VISITED || distance < originalDistance) {
                setDistance(portalVector, distance)
                openPoints.add(portalVector)
                incomingPortals.add(portalVector)
            }
        }
    }

    private fun clearPoints() {
        // todo in clearPoints
        //  if no openPoints do smth with innerGraph
        //  if yes openPoints: clearStuff, then if outerPortals notEmpty go back up
        clearOpenPoints()
        when {
            outerPortals.isNotEmpty() -> {
                val tempInnerPortals = outerPortals
                outerPortals = mutableMapOf()
                outerGraph?.addIncomingPortals(tempInnerPortals)
                outerGraph?.clearPoints()
            }
            innerPortals.isNotEmpty() -> {
                val tempInnerPortals = innerPortals
                innerPortals = mutableMapOf()
                innerGraph.addIncomingPortals(tempInnerPortals)
                innerGraph.clearPoints()
            }
            else -> error("this should not have happened (no openpoints, no outer/inner portals)")
        }
    }


    override fun neighborsOf(vector: Vector2D): List<Vector2D> {
        getOtherPortalOrNull(vector)?.let {
            processPortal(vector)
        }
        return directNeighborsOf(vector)
    }

    private fun processPortal(portalVector: Vector2D) {
        when {
            isOuterPortal(portalVector) -> {
                if (layer > 0 && !incomingPortals.contains(portalVector)) outerPortals[portalVector] =
                    distanceAt(portalVector)
            }
            isInnerPortal(portalVector) -> {
                if (!incomingPortals.contains(portalVector)) innerPortals[portalVector] = distanceAt(portalVector)
            }
            else -> error("not a portal (or anything else?)")
        }
    }

    private fun isOuterPortal(portalVector: Vector2D): Boolean {
        return portalVector.y == 2 ||
                portalVector.x == 2 ||
                portalVector.y == pathDistances.size - 3 ||
                portalVector.x == pathDistances[portalVector.y].size - 3
    }

    private fun isInnerPortal(portalVector: Vector2D): Boolean {
        return portalVector.y > 2 &&
                portalVector.x > 2 &&
                portalVector.y < pathDistances.size - 3 &&
                portalVector.x < pathDistances[portalVector.y].size - 3
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

    internal fun buildRecTorusGraph(): RecursiveTorusGraph = RecursiveTorusGraph(pathMap, portalsPoints, 0, null)

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