package io.github.kmakma.adventofcode.y2019

import io.github.kmakma.adventofcode.y2019.utils.IntcodeComputer
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.UNLIMITED
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

internal class Y2019Day02 : Y2019Day(2, "1202 Program Alarm") {
    private lateinit var initialProgram: List<Long>

    override fun initializeDay() {
        initialProgram = inputAsIntcodeProgram()
    }

    override suspend fun solveTask1(): Long {
        return resultOfProgramWith(12, 2)
    }

    override suspend fun solveTask2(): Long? = coroutineScope {
        val resultChannel: Channel<Long> = Channel(UNLIMITED)
        val jobList = mutableListOf<Job>()
        for (noun in 0L..99L) {
            for (verb in 0L..99L) {
                jobList.add(launch {
                    if (resultOfProgramWith(noun, verb) == 19690720L) {
                        resultChannel.send(noun * 100 + verb)
                    }
                })
            }
        }
        val result = resultChannel.receive()
        for (job in jobList) job.cancel()
        return@coroutineScope result
    }

    private suspend fun resultOfProgramWith(noun: Long, verb: Long): Long {
        val mutableProgram = initialProgram.toMutableList()
        mutableProgram[1] = noun
        mutableProgram[2] = verb
        return IntcodeComputer(mutableProgram).run().currentProgram().first()
    }
}