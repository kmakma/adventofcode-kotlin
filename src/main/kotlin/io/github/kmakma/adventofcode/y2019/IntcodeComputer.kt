package io.github.kmakma.adventofcode.y2019

class IntcodeComputer(private val intcodeProgram: List<Int>) {

    private lateinit var runningProgram: MutableList<Int>

    /**
     * Run [intcodeProgram] with replacing parameters at index 1 and 2 with [noun] and [verb]
     */
    fun run(noun: Int? = null, verb: Int? = null): ExecutedProgram {
        runningProgram = intcodeProgram.toMutableList()
        if (noun != null) {
            runningProgram[1] = noun
        }
        if (verb != null) {
            runningProgram[2] = verb
        }
        val inputCode = buildInputCode(noun, verb)
        var pointer = 0
        while (pointer != -1) {
            pointer = executeOpcode(pointer)
        }
        return ExecutedProgram(intcodeProgram, inputCode, runningProgram)
    }

    private fun buildInputCode(noun: Int?, verb: Int?): Int? {
        return if (noun == null || verb == null) {
            null
        } else {
            100 * noun + verb
        }
    }

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

    private fun executeOpcode(pointer: Int): Int {
        return when (runningProgram[pointer]) {
            1 -> opcodeSum(pointer) + pointer
            2 -> opcodeMultiplication(pointer) + pointer
            99 -> -1
            else -> error("IntcodeProgram: unknown opcode [${runningProgram[pointer]}] at index [$pointer]")
        }
    }

    private fun opcodeSum(pointer: Int): Int {
        val pointerOperand1 = runningProgram[pointer + 1]
        val pointerOperand2 = runningProgram[pointer + 2]
        val pointerResult = runningProgram[pointer + 3]
        runningProgram[pointerResult] = runningProgram[pointerOperand1] + runningProgram[pointerOperand2]
        return 4
    }

    private fun opcodeMultiplication(pointer: Int): Int {
        val pointerOperand1 = runningProgram[pointer + 1]
        val pointerOperand2 = runningProgram[pointer + 2]
        val pointerResult = runningProgram[pointer + 3]
        runningProgram[pointerResult] = runningProgram[pointerOperand1] * runningProgram[pointerOperand2]
        return 4
    }
}

class ExecutedProgram(val baseProgram: List<Int>, val inputCode: Int? = null, val outputProgram: List<Int>) {
    fun result() = outputProgram.first()
    fun inputCode() = inputCode.toString().padStart(4, '0')
}
