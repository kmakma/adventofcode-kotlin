package io.github.kmakma.adventofcode.y2020

import io.github.kmakma.adventofcode.utils.Day

fun main() {
    Y2020Day22().solveAndPrint()
}

class Y2020Day22 : Day(2020, 22, "Crab Combat") {
    private lateinit var decks: List<List<Int>>
    private lateinit var inputList: List<String>

    override fun initializeDay() {
        inputList = inputInStringLines()
        decks = inputList
            .filter { it.isNotEmpty() && !it.startsWith("Player ") }
            .map { it.toInt() }
            .run { this.chunked(this.size / 2) }
    }

    override suspend fun solveTask1(): Any? {
        val (d1, d2) = decks.map { it.toMutableList() }
        return scoreOf(if (combatGame(d1, d2)) d1 else d2)
    }

    override suspend fun solveTask2(): Any? {
        val (d1, d2) = decks.map { it.toMutableList() }
        return scoreOf(if (recursiveCombatGame(d1, d2)) d1 else d2)
    }

    private fun combatGame(d1: MutableList<Int>, d2: MutableList<Int>): Boolean {
        while (d1.isNotEmpty() && d2.isNotEmpty()) {
            combatRound(d1, d2, false)
        }
        return d1.isNotEmpty()
    }

    private fun recursiveCombatGame(
        d1: MutableList<Int>,
        d2: MutableList<Int>,
        rounds: MutableList<Pair<List<Int>, List<Int>>> = mutableListOf()
    ): Boolean {
        while (d1.isNotEmpty() && d2.isNotEmpty()) {
            with(Pair(d1.toList(), d2.toList())) {
                if (rounds.contains(this)) {
                    return true
                }
                rounds.add(this)
            }
            combatRound(d1, d2, true)
        }
        return d1.isNotEmpty()
    }

    private fun combatRound(d1: MutableList<Int>, d2: MutableList<Int>, recursive: Boolean) {
        val c1 = d1.removeFirst()
        val c2 = d2.removeFirst()
        when {
            recursive && c1 <= d1.size && c2 <= d2.size -> {
                if (recursiveCombatGame(d1.take(c1).toMutableList(), d2.take(c2).toMutableList())) {
                    d1.add(c1)
                    d1.add(c2)
                } else {
                    d2.add(c2)
                    d2.add(c1)
                }
            }
            c1 > c2 -> {
                d1.add(c1)
                d1.add(c2)
            }
            c2 > c1 -> {
                d2.add(c2)
                d2.add(c1)
            }
            else -> error("card have the same value: $c1")
        }
    }

    private fun scoreOf(deck: List<Int>): Int {
        return deck.foldIndexed(0) { index, score, card -> score + card * (deck.size - index) }
    }
}