package io.github.kmakma.adventofcode.y2020

import io.github.kmakma.adventofcode.utils.Day
import io.github.kmakma.adventofcode.utils.product

fun main() {
    Y2020Day18().solveAndPrint()
}

private enum class Operation { ADDITION, MULTIPLICATION }

class Y2020Day18 : Day(2020, 18, "Operation Order") {

    private lateinit var inputLines: List<List<Char>>

    override fun initializeDay() {
        inputLines = inputInStringLines().map { line -> line.toList().filter { it != ' ' } }
    }

    override suspend fun solveTask1(): Any? {
        return inputLines.map { line -> parseSimple(line.toMutableList()) }.sum()
    }

    override suspend fun solveTask2(): Any? {
        return inputLines.map { line -> parseAdvanced(line.toMutableList()) }.sum()
    }

    private fun parseSimple(expr: MutableList<Char>): Long {
        var value: Long? = null
        var operation: Operation? = null
        while (expr.isNotEmpty()) {
            when (val char = expr.removeFirst()) {
                '(' -> value = calculate(value, operation, parseSimple(expr))
                ')' -> return value!!
                '+' -> operation = Operation.ADDITION
                '*' -> operation = Operation.MULTIPLICATION
                else -> value = calculate(value, operation, Character.getNumericValue(char).toLong())
            }
        }
        return value!!
    }

    private fun calculate(first: Long?, operation: Operation?, second: Long): Long {
        return when {
            first == null -> second
            operation == Operation.ADDITION -> first + second
            operation == Operation.MULTIPLICATION -> first * second
            else -> throw IllegalArgumentException("missing operator: $first, $operation, $second")
        }
    }

    private fun parseAdvanced(expr: MutableList<Char>, subExpr: StringBuilder = StringBuilder()): Long {
        while (expr.isNotEmpty()) {
            when (val char = expr.removeFirst()) {
                '(' -> subExpr.append(parseAdvanced(expr))
                ')' -> return calculate(subExpr)
                else -> subExpr.append(char)
            }
        }
        return calculate(subExpr)
    }

    private fun calculate(expr: StringBuilder): Long {
        return expr
            .split("*")
            .map { sumString ->
                sumString
                    .split("+")
                    .map { it.toLong() }
                    .sum()
            }
            .product()
    }
}
