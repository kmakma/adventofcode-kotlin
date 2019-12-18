package io.github.kmakma.adventofcode.y2019

import kotlin.math.ceil
import kotlin.time.ExperimentalTime

@ExperimentalTime
internal class Y2019Day14 : Y2019Day(14, "Space Stoichiometry") {
    private lateinit var reactions: List<Reaction>
    override fun initializeDay() {
        reactions = inputInStringLines().map { Reaction.parse(it) }
    }

    override suspend fun solveTask1(): Long {
        val graph = ReactionDependencyGraph.buildGraph(reactions)
        return graph.neededOreForXFuel(1)
    }

    override suspend fun solveTask2(): Long {
        val graph = ReactionDependencyGraph.buildGraph(reactions)
        return graph.fuelOutOfXOre(1000000000000)
    }
}

private const val ORE = "ORE"
private const val FUEL = "FUEL"

internal data class Reaction(val output: String, val outputAmount: Long, val input: Map<String, Long>) {
    companion object {
        internal fun parse(reactionString: String): Reaction {
            val inAndOut = reactionString.split(" => ")
            val output = inAndOut[1].split(" ")
            val input = inAndOut[0].split(", ").associate {
                val inp = it.split(" ")
                Pair(inp[1], inp[0].toLong())
            }
            return Reaction(output[1], output[0].toLong(), input)
        }
    }
}

internal class ReactionDependencyGraph(private val graph: Map<String, ReactionNode>) {

    private val fuelNeedNode = FuelNeedNode(0L)

    init {
        fuelNeedNode.dependsOn(graph[FUEL] ?: error("no fuel found"), 1)
    }

    fun neededOreForXFuel(fuelAmount: Long = 1L): Long {
        return if (fuelAmount > 0L) {
            fuelNeedNode.fuelNeed = fuelAmount
            fuelNeedNode.updateNeeds()
            (graph[ORE] ?: error("no ore found")).thisNeeded()
        } else {
            0L
        }
    }

    fun fuelOutOfXOre(oreAmount: Long): Long {
        var oreNeeded = neededOreForXFuel(1)
        var fuel = oreAmount / oreNeeded
        while (oreNeeded < oreAmount) {
            oreNeeded = neededOreForXFuel(fuel)
            fuel++
        }
        return fuel-2
    }


    companion object {
        fun buildGraph(reactions: List<Reaction>): ReactionDependencyGraph {
            val graph = mutableMapOf<String, ReactionNode>()
            graph[ORE] = ReactionNode(1)
            reactions.forEach { graph[it.output] = ReactionNode(it.outputAmount) }
            reactions.forEach { reaction ->
                reaction.input.forEach { (nodeString, amount) ->
                    graph[reaction.output]!!.dependsOn(
                        graph[nodeString]!!,
                        amount
                    )
                }
            }
            return ReactionDependencyGraph(graph)
        }
    }
}

internal open class ReactionNode(val amountPerBatch: Long) {
    private val dependsOn: MutableMap<ReactionNode, Long> = mutableMapOf()
    private val dependedOnBy: MutableMap<ReactionNode, Long> = mutableMapOf()


    internal fun dependsOn(node: ReactionNode, amount: Long) {
        dependsOn[node] = amount
        node.dependedOnBy(this)
    }

    private fun dependedOnBy(node: ReactionNode) {
        dependedOnBy[node] = 0
    }

    internal fun updateNeeds() {
        dependsOn.forEach { it.key.updateDependant(this, batchesNeeded() * it.value) }
    }

    internal open fun thisNeeded(): Long {
        return dependedOnBy.values.sum()
    }

    private fun updateDependant(reactionNode: ReactionNode, amount: Long) {
        dependedOnBy[reactionNode] = amount
        updateNeeds()
    }

    private fun batchesNeeded(): Long {
        return ceil((1.0 * thisNeeded() / amountPerBatch)).toLong()
    }
}

internal class FuelNeedNode(internal var fuelNeed: Long) : ReactionNode(1) {

    override fun thisNeeded(): Long = fuelNeed
}