package io.github.kmakma.adventofcode.y2019.utils

import io.github.kmakma.adventofcode.utils.gcd
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.sign
import kotlin.math.sqrt

internal data class IntVector(val values: List<Int>) : Comparable<IntVector> {
    val length: Double by lazy { sqrt(values.sumByDouble { it.toDouble() * it }) }
    val manhattanDistance: Int by lazy { values.sumBy { abs(it) } }
    val dim = values.size
    val indices = values.indices
    val short: IntVector by lazy {
        val gcd = abs(values.reduce { a, b -> gcd(a, b) })
        if (gcd == 0) {
            IntVector(values)
        } else {
            this / gcd
        }
    }

    constructor(vararg values: Int) : this(values.asList())

    /**
     * Returns vector with each value.[Int.sign]
     */
    fun sign(): IntVector = IntVector(values.map { it.sign })

    override fun compareTo(other: IntVector): Int {
        val sizeCompare = dim.compareTo(other.dim)
        if (sizeCompare != 0) return sizeCompare
        for (i in values.indices) {
            if (values[i] != other.values[i])
                return values[i] - other.values[i]
        }
        return 0
    }

    override fun toString(): String {
        val valuesString = values.toString()
        return "(${valuesString.subSequence(1, valuesString.length)})"
    }

    operator fun plus(intVector: IntVector): IntVector {
        val maxDim = max(dim, intVector.dim)
        return IntVector(List(maxDim) { values.getOrElse(it) { 0 } + intVector.values.getOrElse(it) { 0 } })
    }

    operator fun minus(intVector: IntVector): IntVector {
        val maxDim = max(dim, intVector.dim)
        return IntVector(List(maxDim) { values.getOrElse(it) { 0 } - intVector.values.getOrElse(it) { 0 } })
    }

    operator fun div(number: Int): IntVector {
        return IntVector(values.map { it / number })
    }

    operator fun inc(): IntVector = this + short

    operator fun dec(): IntVector = this - short

    operator fun get(valueIndex: Int) = values[valueIndex]

    // todo    operator fun rangeTo(other: IntVector)

    // todo    operator fun until(other: IntVector)

//    override fun equals(other: Any?): Boolean {
//        if (this === other) return true
//        if (javaClass != other?.javaClass) return false
//
//        other as IntVector
//
//        if (!values.contentEquals(other.values)) return false
//
//        return true
//    }
//
//    override fun hashCode(): Int {
//        return values.contentHashCode()
//    }
}

internal class IntVectorInterator(
    startVector: IntVector,
    private val endVectorInclusive: IntVector,
    private val steps: Int
) : Iterator<IntVector> {
    private var currentVector = startVector
    private var hasNext = currentVector != endVectorInclusive

    override fun hasNext() = hasNext

    override fun next(): IntVector {
        val next = currentVector
        if (currentVector == endVectorInclusive) {
            hasNext = false
        } else {
            iterateCurrentVector()
        }
        return next
    }

    private fun iterateCurrentVector() {
        val newValues = currentVector.values.toMutableList()


    }
}
