package io.github.kmakma.adventofcode.y2020

import io.github.kmakma.adventofcode.utils.Day

fun main() {
    Y2020Day18().solveAndPrint()
}

private enum class Operation { ADDITION, MULTIPLICATION }

class Y2020Day18 : Day(2020, 18, "???") {

    private lateinit var inputLines: List<List<Char>>

    override fun initializeDay() {
        inputLines = inputInCharLines()
    }

    override suspend fun solveTask1(): Any? {
        return inputLines.map { line -> ExpressionParser.calculateSimple(line) }.sum()
    }

    override suspend fun solveTask2(): Any? {
        return inputLines.map { line -> ExpressionParser.calculateAdvanced(line) }.sum()
    }
}

private class ExpressionParser private constructor(expression: List<Char>) {
    val expr = expression.filter { it != ' ' }

    private fun parseSimple() = parseSimple(expr.toMutableList())

    private fun parseSimple(expr: MutableList<Char>): Long {
        var value: Long? = null
        var operation: Operation? = null
        while (expr.isNotEmpty()) {
            when (val char = expr.removeFirst()) {
                '(' -> value = if (value == null) {
                    parseSimple(expr)
                } else {
                    calculate(value, operation, parseSimple(expr))
                }
                ')' -> return value!!
                '+' -> operation = Operation.ADDITION
                '*' -> operation = Operation.MULTIPLICATION
                else -> {
                    value = if (value == null) {
                        Character.getNumericValue(char).toLong()
                    } else {
                        calculate(value, operation, Character.getNumericValue(char).toLong())
                    }
                }
            }
        }
        return value!!
    }

    private fun parseAdvanced() = parseAdvanced(expr.toMutableList())

    private fun parseAdvanced(expr: MutableList<Char>, subExpr: StringBuilder = StringBuilder()): String {
        while (expr.isNotEmpty()) {
            when (val char = expr.removeFirst()) {
                '(' -> subExpr.append(parseAdvanced(expr))
                ')' -> return calculate(subExpr)
                else -> subExpr.append(char)
            }
        }
        return calculate(subExpr)
    }

    private fun calculate(expr: StringBuilder): String {
        return expr
            .split("*")
            .map { sumString ->
                sumString
                    .split("+")
                    .map { it.toLong() }
                    .sum()
            }
            .reduce { acc, l -> acc * l }
            .toString()
    }

    private fun calculate(first: Long, operation: Operation?, second: Long): Long {
        return when (operation) {
            Operation.ADDITION -> first + second
            Operation.MULTIPLICATION -> first * second
            else -> throw IllegalArgumentException("calculation of: $first, $operation, $second")
        }
    }

    companion object {
        fun calculateSimple(expr: List<Char>): Long {
            return ExpressionParser(expr).parseSimple()
        }

        fun calculateAdvanced(expr: List<Char>): Long {
            return ExpressionParser(expr).parseAdvanced().toLong()
        }
    }
}
