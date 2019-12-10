package io.github.kmakma.adventofcode.y2019.utils

import io.github.kmakma.adventofcode.utils.gcd
import kotlin.math.abs

data class Vector(val x: Int, val y: Int) : Comparable<Vector> {
    val length = abs(x) + abs(y)

    fun shortest(): Vector {
        val gcd = gcd(x, y)
        return if (gcd != 0) {
            this / gcd
        } else {
            Vector(x, y)
        }
    }

    override fun compareTo(other: Vector): Int {
        return if (this.x != other.x) {
            this.x - other.x
        } else {
            this.y - other.y
        }
    }

    override fun toString(): String = "($x,$y)"

    operator fun plus(vector: Vector) = Vector(x + vector.x, y + vector.y)

    operator fun minus(vector: Vector) = Vector(x - vector.x, y - vector.y)

    operator fun div(number: Int): Vector = Vector(x / number, y / number)

    operator fun rangeTo(other: Vector) = PointProgression(this, other)

    operator fun inc(): Vector {
        val gcd = gcd(x, y)
        return this + this / gcd
    }

    operator fun dec(): Vector {
        val gcd = gcd(x, y)
        return this - this / gcd
    }

    infix fun until(to: Vector): PointProgression {
        var newX = to.x
        var newY = to.y
        if (y != to.y) {
            if (y > to.y) {
                newY++
            } else {
                newY--
            }
        } else {
            when {
                x == to.x -> {
                }
                x > to.x -> newX++
                x < to.x -> newX--
            }
        }
        return PointProgression(
            this,
            Vector(newX, newY)
        )
    }
}

class PointIterator(
    startVector: Vector,
    private val endVectorInclusive: Vector,
    private val steps: Int
) : Iterator<Vector> {
    private var currentVector: Vector = startVector
    private var hasNext = currentVector != endVectorInclusive

    override fun hasNext() = hasNext

    override fun next(): Vector {
        val next = currentVector
        if (currentVector == endVectorInclusive) {
            hasNext = false
        } else {
            nextCurrentPoint()
        }
        return next
    }

    private fun nextCurrentPoint() {
        var newX = currentVector.x
        var newY = currentVector.y
        when {
            currentVector.x == endVectorInclusive.x -> {
                newY = stepOverY(steps)
            }
            abs(endVectorInclusive.x - currentVector.x) >= steps -> {
                newX = stepOverX(steps)
            }
            else -> {
                // more steps to take then left to endPointInclusive
                newX = endVectorInclusive.x
                // left over steps done over y
                newY = stepOverY(steps - abs(endVectorInclusive.x - currentVector.x))
            }
        }
        currentVector = Vector(newX, newY)
    }

    private fun stepOverX(xSteps: Int): Int {
        return if (currentVector.x <= endVectorInclusive.x) {
            currentVector.x + xSteps
        } else {
            currentVector.x - xSteps
        }
    }

    private fun stepOverY(ySteps: Int): Int {
        return if (currentVector.y <= endVectorInclusive.y) {
            currentVector.y + ySteps
        } else {
            currentVector.y - ySteps
        }
    }
}

class PointProgression(
    override val start: Vector,
    override val endInclusive: Vector,
    private val steps: Int = 1
) : Iterable<Vector>, ClosedRange<Vector> {
    override fun iterator(): Iterator<Vector> =
        PointIterator(start, endInclusive, steps)

    infix fun step(stepSteps: Int) =
        PointProgression(start, endInclusive, stepSteps)
}
