package io.github.kmakma.adventofcode.y2020

import io.github.kmakma.adventofcode.utils.Day

fun main() {
    Y2020Day17().solveAndPrint()
}

class Y2020Day17 : Day(2020, 17, "Conway Cubes") {

    private lateinit var startCubes: Map<ConwayCube, Boolean>
    private lateinit var inputList: List<List<Char>>

    override fun initializeDay() {
        inputList = inputInCharLines()
        startCubes = inputList.flatMapIndexed { x, list ->
            list.mapIndexed { y, c -> ConwayCube(x = x, y = y, z = 0, w = 0) to (c == '#') }
        }.toMap()
    }

    override suspend fun solveTask1(): Any? {
        val cubes = computeCycles(startCubes, false)
        var active = 0
        for (status in cubes.values) {
            if (status) active++
        }
        return active
    }

    override suspend fun solveTask2(): Any? {
        val cubes = computeCycles(startCubes, true)
        var active = 0
        for (status in cubes.values) {
            if (status) active++
        }
        return active
    }

    private fun computeCycles(cubeMap: Map<ConwayCube, Boolean>, hyper: Boolean): Map<ConwayCube, Boolean> {
        var cubes = cubeMap
        for (i in 0..5) {
            val neighbourCubes = getAllNeighbours(cubes.keys, hyper)
            val tempCubes = mutableMapOf<ConwayCube, Boolean>()
            for (cube in neighbourCubes) {
                val isActive = cubes.getOrDefault(cube, false)
                val neighbours = activeNeighbours(cubes, cube, hyper)
                when {
                    !isActive && neighbours == 3 -> tempCubes[cube] = true
                    isActive && (neighbours < 2 || neighbours > 3) -> tempCubes[cube] = false
                    else -> tempCubes[cube] = isActive
                }
            }
            cubes = tempCubes
        }
        return cubes
    }

    private fun getAllNeighbours(cubes: Set<ConwayCube>, hyper: Boolean = false): Set<ConwayCube> {
        return if (hyper) {
            cubes.flatMap { getBigHyperCube(it) }.toSet()
        } else {
            cubes.flatMap { getBigCube(it) }.toSet()
        }
    }

    private fun getBigCube(cube: ConwayCube): Set<ConwayCube> {
        val cubes = mutableSetOf<ConwayCube>()
        for (x in cube.x - 1..cube.x + 1) {
            for (y in cube.y - 1..cube.y + 1) {
                for (z in cube.z - 1..cube.z + 1) {
                    cubes.add(ConwayCube(x, y, z, 0))
                }
            }
        }
        return cubes
    }

    private fun getBigHyperCube(cube: ConwayCube): Set<ConwayCube> {
        val cubes = mutableSetOf<ConwayCube>()
        for (x in cube.x - 1..cube.x + 1) {
            for (y in cube.y - 1..cube.y + 1) {
                for (z in cube.z - 1..cube.z + 1) {
                    for (w in cube.w - 1..cube.w + 1) {
                        cubes.add(ConwayCube(x, y, z, w))
                    }
                }
            }
        }
        return cubes
    }

    private fun activeNeighbours(cubeMap: Map<ConwayCube, Boolean>, cube: ConwayCube, hyper: Boolean = false): Int {
        var neighbours = 0
        val cubes = if (hyper) getBigHyperCube(cube) else getBigCube(cube)
        for (neighbour in cubes) {
            if (neighbour != cube && cubeMap.getOrDefault(neighbour, false)) neighbours++
        }
        return neighbours
    }
}

private data class ConwayCube(val x: Int, val y: Int, val z: Int, val w: Int = 0)
