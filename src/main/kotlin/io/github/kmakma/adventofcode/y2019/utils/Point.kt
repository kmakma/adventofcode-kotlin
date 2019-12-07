package io.github.kmakma.adventofcode.y2019.utils

import kotlin.math.abs

data class Point(val x: Int, val y: Int) : Comparable<Point> {
    val length = abs(x) + abs(y)

    override fun compareTo(other: Point): Int {
        return if (this.x != other.x) {
            this.x - other.x
        } else {
            this.y - other.y
        }
    }

    override fun toString(): String {
        return "($x,$y)"
    }

    operator fun plus(vector: Point) =
        Point(x + vector.x, y + vector.y)

    operator fun rangeTo(other: Point) =
        PointProgression(this, other)

    infix fun until(to: Point): PointProgression {
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
            Point(newX, newY)
        )
    }
}

class PointIterator(
    startPoint: Point,
    private val endPointInclusive: Point,
    private val steps: Int
) : Iterator<Point> {
    private var currentPoint: Point = startPoint
    private var hasNext = currentPoint != endPointInclusive

    override fun hasNext() = hasNext

    override fun next(): Point {
        val next = currentPoint
        if (currentPoint == endPointInclusive) {
            hasNext = false
        } else {
            nextCurrentPoint()
        }
        return next
    }

    private fun nextCurrentPoint() {
        var newX = currentPoint.x
        var newY = currentPoint.y
        when {
            currentPoint.x == endPointInclusive.x -> {
                newY = stepOverY(steps)
            }
            abs(endPointInclusive.x - currentPoint.x) >= steps -> {
                newX = stepOverX(steps)
            }
            else -> {
                // more steps to take then left to endPointInclusive
                newX = endPointInclusive.x
                // left over steps done over y
                newY = stepOverY(steps - abs(endPointInclusive.x - currentPoint.x))
            }
        }
        currentPoint = Point(newX, newY)
    }

    private fun stepOverX(xSteps: Int): Int {
        return if (currentPoint.x <= endPointInclusive.x) {
            currentPoint.x + xSteps
        } else {
            currentPoint.x - xSteps
        }
    }

    private fun stepOverY(ySteps: Int): Int {
        return if (currentPoint.y <= endPointInclusive.y) {
            currentPoint.y + ySteps
        } else {
            currentPoint.y - ySteps
        }
    }
}

class PointProgression(
    override val start: Point,
    override val endInclusive: Point,
    private val steps: Int = 1
) : Iterable<Point>, ClosedRange<Point> {
    override fun iterator(): Iterator<Point> =
        PointIterator(start, endInclusive, steps)

    infix fun step(stepSteps: Int) =
        PointProgression(start, endInclusive, stepSteps)
}
