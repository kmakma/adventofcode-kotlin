package io.github.kmakma.adventofcode.y2019.utils

import io.github.kmakma.adventofcode.utils.gcd
import kotlin.math.*

/**
 * integer vector, careful using [rotated]
 */
internal data class Vector2D(val x: Int, val y: Int) : Comparable<Vector2D> { // TODO rename to simpleVector
    val length = sqrt((x * x + y * y).toDouble())
    val manhattanDistance = abs(x) + abs(y)

    fun shortest(): Vector2D {
        val gcd = abs(gcd(x, y))
        return if (gcd != 0) {
            this / gcd
        } else {
            Vector2D(x, y)
        }
    }

    fun rotated(degree: Int): Vector2D {
        val radians = degree * PI / 180
        val cos = cos(radians)
        val sin = sin(radians)
        val turnedX = x * cos - y * sin
        val turnedY = x * sin + y * cos
        return Vector2D(turnedX.toInt(), turnedY.toInt())
    }

    override fun compareTo(other: Vector2D): Int {
        return if (this.x != other.x) {
            this.x - other.x
        } else {
            this.y - other.y
        }
    }

    override fun toString(): String = "($x,$y)"

    operator fun plus(vector2D: Vector2D) = Vector2D(x + vector2D.x, y + vector2D.y)

    operator fun minus(vector2D: Vector2D) = Vector2D(x - vector2D.x, y - vector2D.y)

    operator fun div(number: Int): Vector2D = Vector2D(x / number, y / number)

    operator fun rangeTo(other: Vector2D) = PointProgression2D(this, other)

    operator fun inc(): Vector2D {
        val gcd = gcd(x, y)
        return this + this / gcd
    }

    operator fun dec(): Vector2D {
        val gcd = gcd(x, y)
        return this - this / gcd
    }

    infix fun until(to: Vector2D): PointProgression2D {
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
        return PointProgression2D(
            this,
            Vector2D(newX, newY)
        )
    }
}

internal class PointIterator2D(
    startVector: Vector2D,
    private val endVectorInclusive: Vector2D,
    private val steps: Int
) : Iterator<Vector2D> {
    private var currentVector: Vector2D = startVector
    private var hasNext = currentVector != endVectorInclusive

    override fun hasNext() = hasNext

    override fun next(): Vector2D {
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
        currentVector = Vector2D(newX, newY)
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

internal class PointProgression2D(
    override val start: Vector2D,
    override val endInclusive: Vector2D,
    private val steps: Int = 1
) : Iterable<Vector2D>, ClosedRange<Vector2D> {
    override fun iterator(): Iterator<Vector2D> =
        PointIterator2D(start, endInclusive, steps)

    infix fun step(stepSteps: Int) =
        PointProgression2D(start, endInclusive, stepSteps)
}
