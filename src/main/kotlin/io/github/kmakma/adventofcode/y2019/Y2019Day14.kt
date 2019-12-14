package io.github.kmakma.adventofcode.y2019

import kotlin.time.ExperimentalTime

@ExperimentalTime
internal class Y2019Day14 : Y2019Day(14, "Space Stoichiometry") {
    private lateinit var nanofactory: Nanofactory
    override fun initializeDay() {
        nanofactory = Nanofactory.buildFactory(linesToList())
    }

    override suspend fun solveTask1(): Int {
        return nanofactory.oreNeededFor(1, "FUEL")
    }

    override suspend fun solveTask2(): Any? {
//        TODO("not implemented")
        return null
    }
}

internal class Nanofactory(private val reactions: Map<String, Reaction>) {
    private val ore = "ORE"
    internal fun oreNeededFor(amount:Int, chemical: String): Int {
        if (chemical==ore) return amount
        val reaction = reactions[chemical]

    }

    companion object {
        internal fun buildFactory(reactionStrings: List<String>): Nanofactory {
            return Nanofactory(reactionStrings.map { it.toReaction() }.associateBy { it.output })
        }


        private fun String.toReaction(): Reaction {
            val inAndOut = this.split(" => ")
            val output = inAndOut[1].split(" ")
            val input = inAndOut[0].split(", ").associate {
                val inp = it.split(" ")
                Pair(inp[1], inp[0].toInt())
            }
            return Reaction(output[1], output[0].toInt(), input)
        }

    }

    internal data class Reaction(val output: String, val outputAmount: Int, val input: Map<String, Int>)
}

