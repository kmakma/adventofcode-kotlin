package io.github.kmakma.adventofcode.y2020

import io.github.kmakma.adventofcode.utils.Day
import io.github.kmakma.adventofcode.utils.generateStringPermutations

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
        val ruleStrings = mutableMapOf<Int, String>()
        val rules = ruleList.map { line ->
            val (no, rule) = line.split(": ")
            ruleStrings[no.toInt()] = rule
            no.toInt() to MessageRule()
        }.toMap()

        val regex = Regex(""""\w+"""")
        for ((index, pattern) in ruleStrings) {
            val rule = rules[index] ?: error("missing rule no index")
            when {
                pattern.matches(regex) -> rule.value = pattern.substring(1, pattern.lastIndex)
                pattern.contains(" | ") -> {
                    val subRules = pattern
                        .split(" | ")
                        .map { subRules(rules, it) }
                    rule.addAllSubRules(subRules)
                }
                else -> rule.addSubRules(subRules(rules, pattern))
            }
        }

        val matches = (rules[0] ?: error("missing rule no. 0")).matches()
        val dud = messages.mapNotNull { message ->
            for (rule in matches) {
                if (message == rule) return@mapNotNull 1
            }
            null
        }
        return dud.sum()
    }

    private fun subRules(rules: Map<Int, MessageRule>, subRules: String): List<MessageRule> {
        return subRules.split(" ").map { rules[it.toInt()] ?: error("missing rule no. $it") }
    }

    override suspend fun solveTask2(): Any? {
        val ruleStrings = mutableMapOf<Int, String>()
        val rules = ruleList.map { line ->
            val (no, rule) = line.split(": ")
            ruleStrings[no.toInt()] = rule
            no.toInt() to MessageRule()
        }.toMap()

        val regex = Regex(""""\w+"""")
        for ((index, pattern) in ruleStrings) {
            val rule = rules[index] ?: error("missing rule no index")
            when {
                pattern.matches(regex) -> rule.value = pattern.substring(1, pattern.lastIndex)
                pattern.contains(" | ") -> {
                    val subRules = pattern
                        .split(" | ")
                        .map { subRules(rules, it) }
                    rule.addAllSubRules(subRules)
                }
                else -> rule.addSubRules(subRules(rules, pattern))
            }
        }
        val rule8 = rules[8] ?: error("missing rule no. 8")
        val rules8 = "42 | 42 8"
            .split(" | ")
            .map { subRules(rules, it) }
        rule8.clearSubRules()
        rule8.addAllSubRules(rules8)
        val rule11 = rules[11] ?: error("missing rule no. 11")
        val rules11 = "42 | 42 8"
            .split(" | ")
            .map { subRules(rules, it) }
        rule11.clearSubRules()
        rule11.addAllSubRules(rules11)

        val matches = (rules[0] ?: error("missing rule no. 0")).matches()
        val dud = messages.mapNotNull { message ->
            for (rule in matches) {
                if (message == rule) return@mapNotNull 1
            }
            null
        }
        return dud.sum()
    }

    private class MessageRule {
        var value: String? = null
        private var matches: List<String> = emptyList()
        val subRules = mutableListOf<List<MessageRule>>()

        fun matches(): List<String> {
            matches = when {
                matches.isNotEmpty() -> return matches
                subRules.isEmpty() && value == null -> error("broken rule: no subrules and no value")
                subRules.isEmpty() && value != null -> listOf(value!!)
                else -> {
                    evaluateSubRules()
                }
            }
            return matches
        }

        private fun evaluateSubRules(): List<String> {
            return subRules.flatMap { rules -> generateStringPermutations(rules.map { it.matches() }) }
        }

        fun addSubRules(subRules: List<MessageRule>) {
            matches = emptyList()
            this.subRules.add(subRules)
        }

        fun addAllSubRules(subRules: List<List<MessageRule>>) {
            matches = emptyList()
            this.subRules.addAll(subRules)
        }

        fun clearSubRules() {
            matches = emptyList()
            subRules.clear()
        }
    }
}
