package io.github.kmakma.adventofcode.y2019

import io.github.kmakma.adventofcode.y2019.utils.DeprecatingIntcodeComputer
import io.github.kmakma.adventofcode.y2019.utils.IntcodeComputer
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
internal class Y2019Day02 : Y2019Day(
    2,
    "1202 Program Alarm"
) {
    private lateinit var deprecatingIntcodeComputer: DeprecatingIntcodeComputer
    private lateinit var initialProgram: List<Long>

    override fun initializeDay() {
        initialProgram = inputAsIntcodeProgram()
    }

    override suspend fun solveTask1(): Long {
        return resultOfProgramWith(12, 2)
    }

    override suspend fun solveTask2(): Long? {
        // TODO d2t2 sync do-over
        for(noun in 0L..99L) {
            for (verb in 0L..99L) {
                if(resultOfProgramWith(noun, verb)==19690720L) {
                    return noun*100+verb
                }
            }
        }
        return null
    }

    private suspend fun resultOfProgramWith(noun: Long, verb: Long): Long {
        val mutableProgram = initialProgram.toMutableList()
        mutableProgram[1] = noun
        mutableProgram[2] = verb
        return IntcodeComputer(mutableProgram).run().currentProgram().first()
    }
}