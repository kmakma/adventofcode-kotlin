package io.github.kmakma.adventofcode.y2019.utils

import io.github.kmakma.adventofcode.y2019.utils.AsyncComputerStatus.*
import io.github.kmakma.adventofcode.y2019.utils.IntcodeComputer.OpCode.*
import io.github.kmakma.adventofcode.y2019.utils.IntcodeComputer.ParameterMode.*

internal class IntcodeComputer(
    private val initialIntcodeProgram: List<Long>,
    private val input: ComputerIO = ComputerIO(),
    private val output: ComputerIO = ComputerIO()
) {
    private var instruction = Instruction(END, emptyList())
    private var instructionPointer = 0L
    private var computerStatus: AsyncComputerStatus = IDLE
    private lateinit var currentProgramMap: MutableMap<Long, Long>
    private var relativeBase = 0L

    init {
        resetCurrentProgram()
    }

    // internal functions

    internal suspend fun run(): IntcodeComputer {
        when (computerStatus) {
            IDLE -> computerStatus = RUNNING
            TERMINATED -> return this@IntcodeComputer
            RUNNING, TERMINATING -> error("IntcodeComputer is already running!")
        }
        while (computerStatus != TERMINATING && computerStatus != TERMINATED) {
            updateInstruction()
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

    internal fun output(): List<Long> {
        when (computerStatus) {
            IDLE -> println("Warning! IntcodeComputer requested, but has not run yet!")
            RUNNING -> error("Output not possible on running IntcodeComputer")
            TERMINATING -> error("Output not possible on terminating IntcodeComputer")
            TERMINATED -> { // proceed
            }
        }
        return output.getOutput()
    }

    internal fun currentProgram(): List<Long> {
        when (computerStatus) {
            IDLE -> println("Warning! IntcodeComputer program requested, but computer didn't run yet!")
            RUNNING -> error("Output not possible on running IntcodeComputer")
            TERMINATING -> error("Output not possible on terminating IntcodeComputer")
            TERMINATED -> { // proceed
            }
        }
        return currentProgramMap
            .keys
            .sorted()
            .mapNotNull { key -> currentProgramMap[key] }
    }

    // Utility functions

    private fun updateInstruction() {
        var instructionCode: Long = currentProgramMap.getOrDefault(instructionPointer, 0)
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
            error("instruction at [${instructionPointer}] trying to write in immediate mode")
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
        output.close()
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
        else -> error("unknown opcode: $this")
    }

    private fun Number.toParameterMode() = when (this.toInt()) {
        0 -> POSITION
        1 -> IMMEDIATE
        2 -> RELATIVE
        else -> error("unknown parameter mode: $this")
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
        if (input.isClosedForReceive()) error("Input is closed and there are no values left")
        currentProgramMap[pointer(1, true)] = input.receive()
        instructionPointer += IN.values
    }

    private suspend fun writeOutput() {
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
        var initialProgram: List<Long>,
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
            if (input == null) {
                input = ComputerIO()
            }
            if (output == null) {
                output = ComputerIO()
            }
            return IntcodeComputer(initialProgram, input!!, output!!)
        }
    }
}
