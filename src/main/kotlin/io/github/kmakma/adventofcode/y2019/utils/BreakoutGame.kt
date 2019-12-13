package io.github.kmakma.adventofcode.y2019.utils

import com.github.ajalt.mordant.TermColors
import io.github.kmakma.adventofcode.y2019.utils.BreakoutGame.BreakoutTile.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val LEFT = -1L
private const val RIGHT = 1L
private const val NEUTRAL = 0L
private const val PLAY2FREE = 2L

@ExperimentalCoroutinesApi
internal class BreakoutGame(arcadeProgram: List<Long>) {
    private val arcadeInput: ComputerIO by lazy { ComputerIO(null, 1) }
    private val arcadeOutput: ComputerIO by lazy { ComputerIO() }
    private val arcade: IntcodeComputer by lazy {
        val freePlayProgram = arcadeProgram.toMutableList()
        freePlayProgram[0] = PLAY2FREE
        IntcodeComputer(freePlayProgram, arcadeInput, arcadeOutput)
    }
    private val game = mutableMapOf<Vector2D, BreakoutTile>()
    private var started = false
    private var steps = 0L
    private var inputSteps = 0L

    internal var score = 0
        private set

    suspend fun win(): BreakoutGame = coroutineScope {
        launch { arcade.run() }
        launch { parseOutput() }//constantPrintOutput() }//
//        launch { sendInput() }
        return@coroutineScope this@BreakoutGame
    }

    suspend fun play(): BreakoutGame = coroutineScope {
        launch { arcade.run() }
        launch { constantPrintOutput() }
        launch { readUserInput() }
        return@coroutineScope this@BreakoutGame
    }

    private suspend fun parseOutput() {
        while (!arcadeOutput.isClosedForReceive()) {
            val posAndValue = intArrayOf(0, 0, 0)
            for (i in posAndValue.indices) {
                posAndValue[i] = arcadeOutput.receive().toInt()
            }
            parseOutput(posAndValue, true)
        }
    }

    private suspend fun parseOutput(output: IntArray, win: Boolean = false) {
        require(output.size == 3)
        val x = output[0]
        val y = output[1]
        val v = output[2]
        when {
            x == -1 && y == 0 -> {
                score = output[2]
                started = true
            }
            x >= 0 && y >= 0 -> {
                val tile = v.toBreakoutTile()
                game[Vector2D(x, y)] = tile
                if(win && started && tile==BALL) steps++
            }
            else -> error("unknown output")
        }
        if (win && started && inputSteps <= steps) sendInput()
    }

    private suspend fun constantPrintOutput() {
        while (!arcadeOutput.isClosedForReceive()) {
            val posAndValue = intArrayOf(0, 0, 0)
            for (i in posAndValue.indices) {
                posAndValue[i] = arcadeOutput.receive().toInt()
            }
            printOutput(posAndValue)
        }
    }

    private suspend fun printOutput(output: IntArray) {
        parseOutput(output)
        printGame()
        println("Score: $score")
    }

    private suspend fun sendInput() {
        val ballVectors = game.filterValues { it == BALL }.keys
        val paddleVectors = game.filterValues { it == PADDLE }.keys
        val ballXpos = if (ballVectors.isNotEmpty()) ballVectors.first().x else return
        val paddleXpos = if (paddleVectors.isNotEmpty()) paddleVectors.first().x else return
        val input = when {
            ballXpos < paddleXpos -> LEFT
            ballXpos > paddleXpos -> RIGHT
            else -> NEUTRAL
        }
        inputSteps++
        arcadeInput.send(input)
    }

    private suspend fun readUserInput() {
        while (!arcadeOutput.isClosedForReceive()) {
            delay(1)
            val input: Long? = readLine()?.toLongOrNull()
            if (input == -1L || input == 1L || input == 0L) {
                arcadeInput.send(input)
            }
        }
    }

    private fun printGame() {
        val minV = game.keys.min()
        val maxV = game.keys.max()
        if (minV == null || maxV == null) return
        for (y in minV.y..maxV.y) {
            for (x in minV.x..maxV.x) {
                printTile(game[Vector2D(x, y)]!!)
            }
            println()
        }
    }

    private fun printTile(tile: BreakoutTile) {
        with(TermColors()) {
            val tileString = when (tile) {
                EMPTY -> black.bg(" ")
                WALL -> white.bg(" ")
                BLOCK -> brightWhite.bg(" ")
                PADDLE -> magenta.bg(" ")
                BALL -> brightMagenta.bg(" ")
            }
            print(tileString)
        }
    }

    private fun Int.toBreakoutTile(): BreakoutTile {
        return when (this) {
            0 -> EMPTY
            1 -> WALL
            2 -> BLOCK
            3 -> PADDLE
            4 -> BALL
            else -> error("unknown BreakoutTile id: $this")
        }
    }

    private enum class BreakoutTile(val id: Int) {
        EMPTY(0),
        WALL(1),
        BLOCK(2),
        PADDLE(3),
        BALL(4)
    }
}
