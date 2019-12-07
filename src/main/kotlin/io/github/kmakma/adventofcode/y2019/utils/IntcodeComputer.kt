package io.github.kmakma.adventofcode.y2019.utils

import io.github.kmakma.adventofcode.y2019.utils.ComputerVersion.*
import io.github.kmakma.adventofcode.y2019.utils.IntcodeComputer.ParameterMode.IMMEDIATE
import io.github.kmakma.adventofcode.y2019.utils.IntcodeComputer.ParameterMode.POSITION

class IntcodeComputer(private val intcodeProgram: List<Int>, private val version: ComputerVersion) {

    private lateinit var runningProgram: MutableList<Int>

    private var inputList: List<Int> = listOf()
        set(value) {
            field = value
            inputIterator = value.listIterator()
        }
    private lateinit var inputIterator: ListIterator<Int>
    private lateinit var outputList: MutableList<Int>

    private val startPoint = 0
    private val terminationCode = -1
    private val unknownCode = -2

    // Public functions

    /**
     * Run [intcodeProgram] with replacing parameters at index 1 and 2 with [noun] and [verb]
     */
    fun run(noun: Int? = null, verb: Int? = null, input: List<Int>? = null): ExecutedProgram {
        // TODO make run protected, do an overhaul
        runningProgram = intcodeProgram.toMutableList() // TODO array instead
        outputList = mutableListOf()
        if (input != null) {
            inputList = input
        }
        if (noun != null) {
            runningProgram[1] = noun
        }
        if (verb != null) {
            runningProgram[2] = verb
        }
        val inputCode = buildInputCode(noun, verb)
        var pointer = startPoint
        while (pointer != terminationCode) {
            pointer = executeOpcode(pointer)
            if (pointer == unknownCode) {
                error("IntcodeProgram: encountered an unknown opcode")
            }
        }
        return ExecutedProgram(
            intcodeProgram,
            inputCode,
            runningProgram
        )
    }

    /**
     * find input (100 * noun + verb) where index=0 of executed program is [result]
     */
    fun calculateInputForResult(result: Int): Int? {
        for (noun in 0..99) {
            for (verb in 0..99) {
                val executedProgram = run(noun, verb)
                if (executedProgram.result() == result) {
                    return executedProgram.inputCode
                }
            }
        }
        return null
    }

    fun runInput(input: List<Int>): List<Int> {
        run(input = input)
        return outputList
    }

    // utility functions

    private fun buildInputCode(noun: Int?, verb: Int?): Int? {
        return if (noun == null || verb == null) {
            null
        } else {
            100 * noun + verb
        }
    }

    private fun buildOpcode(pointer: Int): Opcode {
        val value = runningProgram[pointer]
        val opcode = value % 100
        val p1 = value % 1000 / 100
        val p2 = value % 10000 / 1000
        val p3 = value % 100000 / 10000
        if (p1 > 1 || p2 > 1 || p3 > 1)
            println("error")
        return Opcode(
            pointer,
            opcode,
            ParameterMode.get(p1),
            ParameterMode.get(p2),
            ParameterMode.get(p3)
        )
    }

    private fun programPointer(pointer: Int, parameterMode: ParameterMode, isWriting: Boolean = false): Int {
        return when (parameterMode) {
            POSITION -> runningProgram[pointer]
            IMMEDIATE -> {
                if (isWriting) {
                    error("program is trying to write in IMMEDIATE mode")
                }
                pointer
            }
        }
    }

    private fun getInput(): Int {
//        if(!inputIterator.hasNext()) {
            return inputIterator.next()
//        } else
    }


    // opcode functions - action decider

    private fun executeOpcode(pointer: Int): Int {
        val opcode = buildOpcode(pointer)
        return when (version) {
            BASIC -> executeBasicOpcode(opcode)
            IO -> executeIOOpcode(opcode)
            JUMPS_COMPARISONS -> executeJumpsCompsOpcode(opcode)
        }
    }

    private fun executeBasicOpcode(opcode: Opcode): Int {
        return when (opcode.opcode) {
            1 -> opcodeSum(opcode)
            2 -> opcodeMultiplication(opcode)
            99 -> terminationCode
            else -> unknownCode
        }
    }

    private fun executeIOOpcode(opcode: Opcode): Int {
        val newPointer = executeBasicOpcode(opcode)
        if (newPointer != unknownCode || newPointer == terminationCode) {
            return newPointer
        }
        return when (opcode.opcode) {
            3 -> opcodeInput(opcode)
            4 -> opcodeOutput(opcode)
            else -> unknownCode
        }
    }

    private fun executeJumpsCompsOpcode(opcode: Opcode): Int {
        val newPointer = executeIOOpcode(opcode)
        if (newPointer != unknownCode || newPointer == terminationCode) {
            return newPointer
        }
        return when (opcode.opcode) {
            5 -> opcodeJumpIfTrue(opcode)
            6 -> opcodeJumpIfFalse(opcode)
            7 -> opcodeLessThan(opcode)
            8 -> opcodeEquals(opcode)
            else -> unknownCode
        }
    }

    // opcode function - operations/actions

    /**
     * opcode == 1
     */
    private fun opcodeSum(opcode: Opcode): Int {
        val pointerToOperand1 = programPointer(opcode.pointer + 1, opcode.firstParameter)
        val pointerToOperand2 = programPointer(opcode.pointer + 2, opcode.secondParameter)
        val pointerToResult = programPointer(opcode.pointer + 3, opcode.thirdParameter, true)
        runningProgram[pointerToResult] = runningProgram[pointerToOperand1] + runningProgram[pointerToOperand2]
        return opcode.pointer + 4
    }

    /**
     * opcode == 2
     */
    private fun opcodeMultiplication(opcode: Opcode): Int {
        val pointerToOperand1 = programPointer(opcode.pointer + 1, opcode.firstParameter)
        val pointerToOperand2 = programPointer(opcode.pointer + 2, opcode.secondParameter)
        val pointerToResult = programPointer(opcode.pointer + 3, opcode.thirdParameter, true)
        runningProgram[pointerToResult] = runningProgram[pointerToOperand1] * runningProgram[pointerToOperand2]
        return opcode.pointer + 4
    }

    /**
     * opcode == 3
     */
    private fun opcodeInput(opcode: Opcode): Int {
        // must not be [IMMEDIATE]
        val pointerIn = programPointer(opcode.pointer + 1, opcode.firstParameter, true)
        runningProgram[pointerIn] = getInput()
        return opcode.pointer + 2
    }

    /**
     * opcode == 4
     */
    private fun opcodeOutput(opcode: Opcode): Int {
        val pointerOut = programPointer(opcode.pointer + 1, opcode.firstParameter)
//        println(runningProgram[pointerOut])
        outputList.add(runningProgram[pointerOut])
        return opcode.pointer + 2
    }

    /**
     * opcode == 5
     */
    private fun opcodeJumpIfTrue(opcode: Opcode): Int {
        return if (runningProgram[programPointer(opcode.pointer + 1, opcode.firstParameter)] != 0) {
            runningProgram[programPointer(opcode.pointer + 2, opcode.secondParameter)]
        } else {
            opcode.pointer + 3
        }
    }

    /**
     * opcode == 6
     */
    private fun opcodeJumpIfFalse(opcode: Opcode): Int {
        return if (runningProgram[programPointer(opcode.pointer + 1, opcode.firstParameter)] == 0) {
            runningProgram[programPointer(opcode.pointer + 2, opcode.secondParameter)]
        } else {
            opcode.pointer + 3
        }
    }

    /**
     * opcode == 7
     */
    private fun opcodeLessThan(opcode: Opcode): Int {
        val newPointer = programPointer(opcode.pointer + 3, opcode.thirdParameter, true)
//        if (runningProgram[opcode.pointer + 1] < runningProgram[opcode.pointer + 2]) {
        if (runningProgram[programPointer(
                opcode.pointer + 1,
                opcode.firstParameter
            )] < runningProgram[programPointer(opcode.pointer + 2, opcode.secondParameter)]
        ) {
            runningProgram[newPointer] = 1
        } else {
            runningProgram[newPointer] = 0
        }
        return opcode.pointer + 4
    }

    /**
     * opcode == 8
     */
    private fun opcodeEquals(opcode: Opcode): Int {
        val newPointer = programPointer(opcode.pointer + 3, opcode.thirdParameter, true)
        if (runningProgram[programPointer(
                opcode.pointer + 1,
                opcode.firstParameter
            )] == runningProgram[programPointer(opcode.pointer + 2, opcode.secondParameter)]
        ) {
            runningProgram[newPointer] = 1
        } else {
            runningProgram[newPointer] = 0
        }
        return opcode.pointer + 4
    }


    // enum and data classes

    enum class ParameterMode {
        POSITION,
        IMMEDIATE;

        companion object {
            fun get(pmCode: Int): ParameterMode {
                return when (pmCode) {
                    0 -> POSITION
                    1 -> IMMEDIATE
                    else -> error("unknown parameter mode code ($pmCode)")
                }
            }
        }

    }

    data class Opcode(
        val pointer: Int,
        val opcode: Int,
        val firstParameter: ParameterMode = POSITION,
        val secondParameter: ParameterMode = POSITION,
        val thirdParameter: ParameterMode = POSITION
    )

    companion object {
        fun parse(program: String, version: ComputerVersion = BASIC): IntcodeComputer {
            return IntcodeComputer(
                program.split(",").map { it.toInt() },
                version
            )
        }

        fun parse(program: List<String>, version: ComputerVersion): IntcodeComputer {
            return IntcodeComputer(
                program.map { it.toInt() },
                version
            )
        }
    }
}

class ExecutedProgram(val baseProgram: List<Int>, val inputCode: Int? = null, val outputProgram: List<Int>) {
    fun result() = outputProgram.first()
    fun inputCode() = inputCode.toString().padStart(4, '0')
}

enum class ComputerVersion {
    BASIC, IO, JUMPS_COMPARISONS
}