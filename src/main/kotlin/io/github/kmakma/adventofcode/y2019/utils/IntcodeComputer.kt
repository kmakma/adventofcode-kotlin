package io.github.kmakma.adventofcode.y2019.utils

import io.github.kmakma.adventofcode.y2019.utils.IntcodeComputer.ComputerStatus.*
import io.github.kmakma.adventofcode.y2019.utils.IntcodeComputer.OpCode.*
import io.github.kmakma.adventofcode.y2019.utils.IntcodeComputer.ParameterMode.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.runBlocking

internal class IntcodeComputer private constructor(
    private val initialIntcodeProgram: List<Long>,
    private val input: ComputerIO,
    private val output: ComputerIO
) {
    // all vars these vars have to be reset (on reset)
    private var instruction = Instruction(END, emptyList())
    private var instructionPointer = 0L
    private var computerStatus: ComputerStatus = IDLE
    private lateinit var currentProgramMap: MutableMap<Long, Long>
    private var relativeBase = 0L

    init {
        resetCurrentProgram()
    }

    // Public functions

    suspend fun run(): IntcodeComputer {
        when (computerStatus) {
            IDLE -> computerStatus = RUNNING
            TERMINATED, TERMINATING -> return this@IntcodeComputer
            RUNNING -> error("IntcodeComputer is already running!")
        }
        // TODO save program before running similar to intialIntcodeProgram
        while (computerStatus != TERMINATING && computerStatus != TERMINATED) {
            updateInstruction()
//            println("$this; point:$instructionPointer, $instruction") // todo delete line
            when (instruction.opCode) {
                ADD -> add()
                MUL -> multiply()
                IN -> readInput()
                OUT -> writeOutput()
                JNZ -> jumpIfNotZero() // "jump-if-true"
                JZ -> jumpIfZero() // "jump-if-false"
                SETL -> setIfLessThen()
                SETE -> setIfEquals()
                REL -> offsetRelativeBase()
                END -> end()
            }
        }
        finish()
        return this@IntcodeComputer
    }

    fun reset() {
        // TODO computerStatus check
        resetCurrentProgram()
        instructionPointer = 0
        relativeBase = 0
        instruction = Instruction(END, emptyList())
        computerStatus = IDLE
    }

    fun output(): List<Long> {
        // TODO computerStatus check
        return output.getOutput()
    }

    // TODO status()

    // Utility functions

    private fun updateInstruction() {
        var instructionCode: Long = currentProgramMap.getOrDefault(instructionPointer, 0)
        if (instructionCode % 100 == 0L) {
            println("fehler")
        }
        val opCode = (instructionCode % 100).toOpCode()
        val parameterModes: MutableList<ParameterMode> = mutableListOf()
        instructionCode /= 100
        while (instructionCode > 0) {
            parameterModes.add((instructionCode % 10).toParameterMode())
            instructionCode /= 10
        }
        instruction = Instruction(opCode, parameterModes)
    }

    private fun resetCurrentProgram() {
        currentProgramMap =
            initialIntcodeProgram.withIndex().map { it.index.toLong() to it.value }.toMap().toMutableMap()
    }

    private fun pointer(offset: Int, writing: Boolean = false): Long {
        val pMode = parameterMode(offset)
        if (writing && pMode == IMMEDIATE) {
            error("instruction at [${instructionPointer}] trying to write in immediate mode") // TODO set error message and status = ABORT
        }
        return when (pMode) {
            POSITION -> currentProgramMap.getOrDefault(instructionPointer + offset, 0)
            IMMEDIATE -> instructionPointer + offset
            RELATIVE -> currentProgramMap.getOrDefault(instructionPointer + offset, 0) + relativeBase
        }
    }

    private fun value(offset: Int): Long {
        return currentProgramMap.getOrDefault(pointer(offset), 0)
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

    private fun Number.toOpCode() = when (this.toInt()) {
        1 -> ADD
        2 -> MUL
        3 -> IN
        4 -> OUT
        5 -> JNZ
        6 -> JZ
        7 -> SETL
        8 -> SETE
        9 -> REL
        99 -> END
        else -> error("unknown opcode: $this") // TODO seterrormessage and status aborted
    }

    private fun Number.toParameterMode() = when (this.toInt()) {
        0 -> POSITION
        1 -> IMMEDIATE
        2 -> RELATIVE
        else -> error("unknown parameter mode: $this") // TODO seterrormessage and status aborted
    }

    // OpCode functions

    private fun add() {
        currentProgramMap[pointer(3, true)] = value(1) + value(2)
        instructionPointer += ADD.values
    }

    private fun multiply() {
        currentProgramMap[pointer(3, true)] = value(1) * value(2)
        instructionPointer += MUL.values
    }

    private suspend fun readInput() {
        // TODO check if open
        println("$this input from $input")
        currentProgramMap[pointer(1, true)] = input.receive()
        instructionPointer += IN.values
    }

    private suspend fun writeOutput() {
        println("$this output to $output")
        output.send(value(1))
        instructionPointer += OUT.values
    }

    private fun jumpIfNotZero() {
        instructionPointer = if (value(1) != 0L) {
            value(2)
        } else {
            instructionPointer + JNZ.values
        }
    }

    private fun jumpIfZero() {
        instructionPointer = if (value(1) == 0L) {
            value(2)
        } else {
            instructionPointer + JZ.values
        }
    }

    private fun setIfLessThen() {
        currentProgramMap[pointer(3, true)] =
            if (value(1) < value(2)) {
                1L
            } else {
                0L
            }
        instructionPointer += SETL.values
    }

    private fun setIfEquals() {
        currentProgramMap[pointer(3, true)] =
            if (value(1) == value(2)) {
                1L
            } else {
                0L
            }
        instructionPointer += SETE.values
    }

    private fun offsetRelativeBase() {
        relativeBase += value(1)
        instructionPointer += REL.values
    }

    private fun end() {
        terminate()
        instructionPointer += END.values
    }

    // Classes

    private enum class ComputerStatus {
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
        // offset relativeBase by p1
        REL(2),
        // program termination
        END(1)
    }

    private enum class ParameterMode {
        // parameter is pointer to value
        POSITION,
        // parameter is value
        IMMEDIATE,
        // parameter is pointer to value + [relativeBase]
        RELATIVE
    }

    private data class Instruction(val opCode: OpCode, val parameterModes: List<ParameterMode>)

    internal data class Builder(
        var initialProgram: List<Long>? = null,
        var input: ComputerIO? = null,
        var output: ComputerIO? = null
    ) {
        fun program(initialProgram: List<Long>) = apply { this.initialProgram = initialProgram }
        fun input(input: ComputerIO) = apply { this.input = input }
        fun input(input: List<Long>) = apply { this.input = ComputerIO(input) }
        fun output(output: ComputerIO) = apply { this.output = output }
        fun parseProgram(initialProgram: List<String>) =
            apply { this.initialProgram = initialProgram.map { it.toLong() } }

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

internal class ComputerIO(input: List<Long>? = null) {
    private val channel: Channel<Long> = Channel(Channel.UNLIMITED)
    private val output: MutableList<Long> = mutableListOf()

    init {
        if (!input.isNullOrEmpty()) {
            runBlocking { for (x in input) channel.send(x) }
        }
    }

    internal fun getOutput() = output.toList()

    internal suspend fun send(send: Long) {
        channel.send(send)
        output.add(send)
    }

    internal suspend fun receive(): Long {
        return channel.receive()
    }
}