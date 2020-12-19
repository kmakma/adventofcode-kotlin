package io.github.kmakma.adventofcode.y2020

import io.github.kmakma.adventofcode.utils.Day

fun main() {
    Y2020Day19().solveAndPrint()
}

class Y2020Day19 : Day(2020, 19, "Monster Messages") {
    private lateinit var ruleList: List<String>
    private lateinit var messages: List<String>
    private lateinit var inputLines: List<String>


    override fun initializeDay() {
        inputLines = inputInStringLines()
        val input = inputLines
            .filterNot { it.isBlank() }
            .partition { line -> (line.startsWith("a") || line.startsWith("b")) }
        messages = input.first
        ruleList = input.second
    }

    override suspend fun solveTask1(): Any? {
        return countMatches(false)
    }

    override suspend fun solveTask2(): Any? {
        return countMatches(true)
    }

    private fun countMatches(specialRules: Boolean): Int {
        val rules = ruleList.map { line ->
            val (no, rule) = line.split(": ")
            no.toInt() to rule.split(" ").toMutableList()
        }.toMap().toMutableMap()
        reduceRule(rules, 11, specialRules)
        for (rule in rules.keys.filter { it != 0 }) {
            reduceRule(rules, rule, specialRules)
        }
        val regex = (rules[0] ?: error("missing rule no 0"))
            .fold(StringBuilder()) { sb, s -> sb.append(s) }
            .toString()
            .toRegex()
        var matches = 0
        for (message in messages) {
            if (message.matches(regex)) matches++
        }
        return matches
    }

    private val tokenRegex = Regex(""""[ab]"""")

    private fun reduceRule(rules: MutableMap<Int, MutableList<String>>, ruleNo: Int, specialRules: Boolean) {

        val rule: MutableList<String> = if (ruleNo == 11 && specialRules) {
            // assumption: longest message has 88 chars
            // rule 11 consists of at least 4 chars (11: 42 31 with 42 and 31 at least 2)
            // worst case for 11: 42 11 31 -> 22x42 and 22x31
            rules.remove(ruleNo)
            val tempRule = mutableListOf<String>("(")
            for (i in 1..22) {
                tempRule.addAll(listOf("(", "(", "42", ")", "{$i}", "(", "31", ")", "{$i}", ")", "|"))
            }
            tempRule.removeLast()
            tempRule.add(")")
            tempRule
        } else {
            rules.remove(ruleNo).apply {
                when {
                    isNullOrEmpty() -> return
                    first().matches(tokenRegex) -> replaceAll { it.substring(1, it.lastIndex) }
                    size > 1 -> apply { add(0, "("); add(")") }
                }
            }!!
        }
        if (ruleNo == 8 && specialRules) {
            rule.add("+")
        }

        for ((_, oldRule) in rules) {
            var index = oldRule.indexOf(ruleNo.toString())
            while (index > -1) {
                oldRule.removeAt(index)
                oldRule.addAll(index, rule)
                index = oldRule.indexOf(ruleNo.toString())
            }
        }
    }
}
