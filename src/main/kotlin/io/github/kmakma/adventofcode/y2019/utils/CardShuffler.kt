package io.github.kmakma.adventofcode.y2019.utils

import io.github.kmakma.adventofcode.y2019.utils.ShuffleTechnique.*
import kotlin.math.abs

internal class CardShuffler(inputLines: List<String>, private val deckSize: Int) {

    private val instructions: List<ShuffleInstruction>
    val deck = IntArray(deckSize) { index -> index }

    init {
        instructions = inputLines.map { line -> line.toShuffleInstruction() }
    }

    fun shuffle() {
        for (instruction in instructions) {
            instruction(deck)
        }
    }

    fun indexOf(card: Int): Int {
        if (card !in 0 until deckSize) throw IllegalArgumentException("card $card is not part of the deck")
        for (i in deck.indices) {
            if (deck[i] == card) return i
        }
        error("card $card not found")
    }
}

private fun String.toShuffleInstruction(): ShuffleInstruction {
    if (this == "deal into new stack") return ShuffleInstruction(NEW_STACK)
    val techniqueString = this.substringBeforeLast(' ')
    val n = this.substringAfterLast(' ').toIntOrNull() ?: error("missing N with instruction")
    return when (techniqueString) {
        "cut" -> ShuffleInstruction(CUT, n)
        "deal with increment" -> ShuffleInstruction(INCREMENT, n)
        else -> error("unknown shuffle instruction $this")
    }
}

internal data class ShuffleInstruction(val technique: ShuffleTechnique, val n: Int = 0) {
    operator fun invoke(deck: IntArray) {
        when (technique) {
            NEW_STACK -> dealIntoNewStack(deck)
            CUT -> cut(deck)
            INCREMENT -> dealWithIncrement(deck)
        }
    }

    private fun dealIntoNewStack(deck: IntArray) {
        deck.reverse()
    }

    private fun cut(deck: IntArray) {
        when {
            n > 0 -> deck.leftShift(n)
            n < 0 -> deck.rightShift(abs(n))
            else -> {
            }
        }
    }

    private fun dealWithIncrement(deck: IntArray) {
        val deckSize = deck.size
        val tempDeck = deck.copyOf()
        var incrIndex = 0
        for (i in deck.indices) {
            deck[incrIndex] = tempDeck[i]
            incrIndex = (incrIndex + n) % deckSize
        }
    }
}

private fun IntArray.leftShift(n: Int) {
    val tempArray = this.copyOf()
    val shift = n % size
    for (i in tempArray.indices) {
        val newIndex = (i + (size - shift)) % size
        this[newIndex] = tempArray[i]
    }
}

private fun IntArray.rightShift(n: Int) {
    val tempArray = this.copyOf()
    val shift = n % size
    for (i in tempArray.indices) {
        val newIndex = (i + shift) % size
        this[newIndex] = tempArray[i]
    }
}

/**
 * Known techniques:
 *  - deal into [NEW_STACK]: basically flips whole deck, first card <-> last card and so on
 *  - [CUT] N cards: take first (or last if N < 0) N and put it at the end/top without changing their order
 *  - deal with [INCREMENT] N: from top to bottom, put every card at N intervals (look it up)
 */
internal enum class ShuffleTechnique {
    NEW_STACK, CUT, INCREMENT
}