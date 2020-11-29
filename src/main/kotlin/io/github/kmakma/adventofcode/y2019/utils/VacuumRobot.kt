package io.github.kmakma.adventofcode.y2019.utils

private const val NEW_LINE = 10L
private const val SCAFFOLDING: Char = '#'

class VacuumRobot(intcodeProgram: List<Long>) {
    private val intcodeComputerV2 = IntcodeComputerV2(intcodeProgram)
    private lateinit var exterior: Map<Vector2D, Char>
    private val directions = Direction.values().map { it.vector2D }


    private fun activateCamera() {
        intcodeComputerV2.run()
        val asciiOutput = intcodeComputerV2.output
        exterior = outputAsMap(asciiOutput)
    }

    internal fun sumOfAlignmentParameters(): Int {
        activateCamera()
        val scaffolding = exterior.filter { it.value == SCAFFOLDING }.keys
        val intersections = scaffolding.filter {
            var neighbors = 0
            for (dir in directions) {
                if (scaffolding.contains(it + dir)) {
                    neighbors++
                }
            }
            neighbors >= 3
        }
        return intersections.map { it.x * it.y }.sum()
    }

    private fun outputAsMap(output: List<Long>): Map<Vector2D, Char> {
        val extScaffolding = mutableMapOf<Vector2D, Char>()
        var x = 0
        var y = 0
        for (out in output) {
            if (out == NEW_LINE) {
                x = 0
                y++
            } else {
                extScaffolding[Vector2D(x, y)] = out.toChar()
                x++
            }
        }
        return extScaffolding
    }

    private fun notifyRobots(movementLogic: List<List<Char>>) {
        intcodeComputerV2.setAtAddress(0L, 2L)
        intcodeComputerV2.run()
        for (movRoutine in movementLogic) {
            for (char in movRoutine) {
                intcodeComputerV2.runWith(char.toLong())
            }
            intcodeComputerV2.runWith(10L) // 10L = new line
        }
        intcodeComputerV2.runWith('n'.toLong()) // y/n = continues video feed
        intcodeComputerV2.runWith(10L) // 10L = newLine
//        printOutput(intcodeComputerV2)
    }

    internal fun countDust(manualMovementLogic: List<List<Char>>): Long {
        notifyRobots(manualMovementLogic)
        // TODO full automatic
        return intcodeComputerV2.latestOutput()
    }

    private fun printOutput(intcodeComputerV2: IntcodeComputerV2): Long {
        with(intcodeComputerV2) {
            var lastOutput = -1L
            while (hasOutput()) {
                lastOutput = nextOutput()
                if (lastOutput in 0..127) {
                    print(lastOutput.toChar())
                } else {
                    println(lastOutput)
                }
            }
            return lastOutput
        }
    }

//    private fun Long.toTile(): Tile {
//        when(this) {
//
//            else -> error("unknown tile: $this; ${this.toChar()}")
//        }
//        TODO("not implemented")
//    }
}

internal enum class Direction(val vector2D: Vector2D) {
    UP(Vector2D(0, 1)), DOWN(Vector2D(0, -1)),
    RIGHT(Vector2D(1, 0)), LEFT(Vector2D(-1, 0))
}
