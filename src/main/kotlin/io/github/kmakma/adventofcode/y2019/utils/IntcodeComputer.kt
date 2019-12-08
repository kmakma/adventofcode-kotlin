package io.github.kmakma.adventofcode.y2019.utils

import io.github.kmakma.adventofcode.y2019.utils.IntcodeComputer.ComputerStatus.*
import io.github.kmakma.adventofcode.y2019.utils.IntcodeComputer.OpCode.*
import io.github.kmakma.adventofcode.y2019.utils.IntcodeComputer.ParameterMode.IMMEDIATE
import io.github.kmakma.adventofcode.y2019.utils.IntcodeComputer.ParameterMode.POSITION
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking

internal class IntcodeComputer private constructor(
    private val initialIntcodeProgram: List<Int>,
    private val input: ComputerIO,
    private val output: ComputerIO
) {
    private var instruction = Instruction(END, emptyList())
    private var instructionPointer = 0
        set(value) {
            field = value
            updateInstruction()
        }
    private var computerStatus: ComputerStatus = IDLE
    private var currentProgram: MutableList<Int> = initialIntcodeProgram.toMutableList()

    // Public functions

    suspend fun run(): IntcodeComputer = coroutineScope {
        when (computerStatus) {
            IDLE -> computerStatus = RUNNING
            TERMINATED, TERMINATING -> return@coroutineScope this@IntcodeComputer
            RUNNING -> error("IntcodeComputer is already running!")
        }
        // TODO save program before running similar to intialIntcodeProgram
        updateInstruction()
        while (computerStatus != TERMINATING && computerStatus != TERMINATED) {
            when (instruction.opCode) {
                ADD -> add()
                MUL -> multiply()
                IN -> readInput()
                OUT -> writeOutput()
                JNZ -> jumpIfNotZero() // "jump-if-true"
                JZ -> jumpIfZero() // "jump-if-false"
                SETL -> setIfLessThen()
                SETE -> setIfEquals()
                END -> end()
            }
        }
        finish()
        return@coroutineScope this@IntcodeComputer
    }

    fun reset() {
        // TODO computerStatus check
        currentProgram = initialIntcodeProgram.toMutableList()
        instructionPointer = 0
        instruction = Instruction(END, emptyList())
        computerStatus = IDLE
    }

    fun output(): List<Int> {
        // TODO computerStatus check
        return output.getOutput()
    }

    // TODO isRunning()

    // Utility functions

    private fun updateInstruction(pointer: Int? = null) {
        var instructionCode = pointer ?: currentProgram[instructionPointer]
        val opCode = (instructionCode % 100).toOpCode()
        val parameterModes: MutableList<ParameterMode> = mutableListOf()
        instructionCode /= 100
        while (instructionCode > 0) {
            parameterModes.add((instructionCode % 10).toParameterMode())
            instructionCode /= 10
        }
        instruction = Instruction(opCode, parameterModes)
    }

    private fun pointer(offset: Int, writing: Boolean = false): Int {
        val pMode = parameterMode(offset)
        if (writing && pMode == IMMEDIATE) {
            error("instruction at [${instructionPointer}] trying to write in immediate mode")
        }
        return when (pMode) {
            POSITION -> currentProgram[instructionPointer + offset]
            IMMEDIATE -> instructionPointer + offset
        }
    }

    private fun parameterMode(parameter: Int): ParameterMode {
        return instruction.parameterModes.getOrElse(parameter - 1) { POSITION }
    }

    private fun terminate() {
        computerStatus = TERMINATING
    }

    private fun finish() {
        // TODO close output, maybe input?
        computerStatus = TERMINATED
    }

    private fun Int.toOpCode() = when (this) {
        1 -> ADD
        2 -> MUL
        3 -> IN
        4 -> OUT
        5 -> JNZ
        6 -> JZ
        7 -> SETL
        8 -> SETE
        99 -> END
        else -> error("unknown opcode: $this")
    }

    private fun Int.toParameterMode() = when (this) {
        0 -> POSITION
        1 -> IMMEDIATE
        else -> error("unknown parameter mode: $this")
    }

    // OpCode functions

    private fun add() {
        currentProgram[pointer(3, true)] = currentProgram[pointer(1)] + currentProgram[pointer(2)]
        instructionPointer += ADD.values
    }

    private fun multiply() {
        currentProgram[pointer(3, true)] = currentProgram[pointer(1)] * currentProgram[pointer(2)]
        instructionPointer += MUL.values
    }

    private suspend fun readInput() {
        // TODO check if open
        currentProgram[pointer(1, true)] = input.receive()
        instructionPointer += IN.values
    }

    private suspend fun writeOutput() {
        output.send(currentProgram[pointer(1)])
        instructionPointer += OUT.values
    }

    private fun jumpIfNotZero() {
        instructionPointer = if (currentProgram[pointer(1)] != 0) {
            currentProgram[pointer(2)]
        } else {
            instructionPointer + JNZ.values
        }
    }

    private fun jumpIfZero() {
        instructionPointer = if (currentProgram[pointer(1)] == 0) {
            currentProgram[pointer(2)]
        } else {
            instructionPointer + JZ.values
        }
    }

    private fun setIfLessThen() {
        currentProgram[pointer(3, true)] = if (currentProgram[pointer(1)] < currentProgram[pointer(2)]) {
            1
        } else {
            0
        }
    }

    private fun setIfEquals() {
        currentProgram[pointer(3, true)] = if (currentProgram[pointer(1)] == currentProgram[pointer(2)]) {
            1
        } else {
            0
        }
    }

    private fun end() {
        terminate()
        instructionPointer += END.values
    }

    // Classes

    private enum class ComputerStatus { // TODO ComputerStatus necessary? or is running/terminated enough
        IDLE, RUNNING, TERMINATING, TERMINATED
    }

    private enum class OpCode(val values: Int) {
        // add or multiply p1 and p2 to p3.POSITION
        ADD(4),
        MUL(4),
        // input to p1.POS / output from p1
        IN(2),
        OUT(2),
        // jump to p2 if p1 is not/is zero (true/false)
        JNZ(3),
        JZ(3),
        // set 1 if true, else 0 to p3.POS: p1 < p2 / p1 == p2
        SETL(4),
        SETE(4),
        // program termination
        END(1)
    }

    private enum class ParameterMode {
        // parameter is pointer to value
        POSITION,
        // parameter is value
        IMMEDIATE
    }

    private data class Instruction(val opCode: OpCode, val parameterModes: List<ParameterMode>)

    internal data class Builder(
        var initialProgram: List<Int>? = null,
        var input: ComputerIO? = null,
        var output: ComputerIO? = null
    ) {
        fun program(initialProgram: List<Int>) = apply { this.initialProgram = initialProgram }
        fun input(input: ComputerIO) = apply { this.input = input }
        fun input(input: List<Int>) = apply { this.input = ComputerIO(input) }
        fun output(output: ComputerIO) = apply { this.output = output }
        fun parseProgram(initialProgram: List<String>) =
            apply { this.initialProgram = initialProgram.map { it.toInt() } }

        fun build(): IntcodeComputer {
            if (initialProgram == null) {
                error("IntcodeComputerV2.Builder: can't build computer without program!")
            }
            if (input == null) {
                input = ComputerIO()
            }
            if (output == null) {
                output = ComputerIO()
            }
            return IntcodeComputer(initialProgram!!, input!!, output!!)
        }
    }
}

internal class ComputerIO(input: List<Int>? = null) {
    private val channel: Channel<Int> = Channel(Channel.UNLIMITED)
    private val output: MutableList<Int> = mutableListOf()

    init {
        if (!input.isNullOrEmpty()) {
            runBlocking { for (x in input) channel.send(x) }
        }
    }

    internal fun getOutput() = output.toList()

    internal suspend fun send(send: Int) {
        channel.send(send)
        output.add(send)
    }

    internal suspend fun receive(): Int {
        return channel.receive()
    }
}