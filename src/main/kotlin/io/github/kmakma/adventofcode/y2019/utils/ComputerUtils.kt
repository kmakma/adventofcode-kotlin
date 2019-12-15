package io.github.kmakma.adventofcode.y2019.utils

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.runBlocking


internal class ComputerIO(input: List<Long>? = null, capacity: Int = Channel.UNLIMITED) {
    private val channel: Channel<Long> = Channel(capacity)
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

    internal fun close() = channel.close()

    @ExperimentalCoroutinesApi
    internal fun isClosedForReceive() = channel.isClosedForReceive
}

internal enum class AsyncComputerStatus {
    IDLE, RUNNING, TERMINATING, TERMINATED
}