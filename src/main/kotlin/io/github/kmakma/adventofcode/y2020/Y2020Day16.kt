package io.github.kmakma.adventofcode.y2020

import io.github.kmakma.adventofcode.utils.Day

fun main() {
    Y2020Day16().solveAndPrint()
}

class Y2020Day16 : Day(2020, 16, "Ticket Translation") {
    private lateinit var inputLines: List<String>

    override fun initializeDay() {
        inputLines = inputInStringLines()
    }

    override suspend fun solveTask1(): Any? {
        val ticketRules = TicketRules()
        var ruleLines = true
        var ticketLines = false
        var errorRate = 0
        for (line in inputLines) {
            if (ruleLines && line.isBlank()) {
                ruleLines = false
            } else if (ruleLines) {
                ticketRules.addRule(line)
            } else if (ticketLines) {
                errorRate += ticketRules.errorRate(line)
            } else if (line.contains("nearby tickets:")) {
                ticketLines = true
            }
        }
        return errorRate
    }

    override suspend fun solveTask2(): Any? {
        val ticketRules = TicketRules()
        var ruleLines = true
        var ticketLines = false
        var myTicketLine = false
        val tickets = mutableListOf<Ticket>()
        var myTicket = Ticket(listOf())
        for (line in inputLines) {
            when {
                ruleLines && line.isBlank() -> ruleLines = false
                ticketLines && line.isBlank() -> ticketLines = false
                myTicketLine && line.isBlank() -> myTicketLine = false
                ruleLines -> ticketRules.addRule(line)
                ticketLines -> if (ticketRules.isValidAtAll(line)) tickets.add(Ticket.create(line))
                myTicketLine -> myTicket = Ticket.create(line)
                line.contains("nearby tickets:") -> ticketLines = true
                line.contains("your ticket:") -> myTicketLine = true
            }
        }
        var departureCheck = 1L
        ticketRules.orderRules(tickets).forEach { (rule, index) ->
            if (rule.contains("departure")) departureCheck *= myTicket[index]
        }
        return departureCheck
    }
}

class TicketRules {
    private val rulesList = mutableListOf<IntRange>()
    private val rulesMap = mutableMapOf<String, Pair<IntRange, IntRange>>()

    fun addRule(line: String) {
        val (field, firstStart, firstEnd, scndStart, scndEnd) = line.split(": ", "-", " or ")
        val firstRange = IntRange(firstStart.toInt(), firstEnd.toInt())
        val scndRange = IntRange(scndStart.toInt(), scndEnd.toInt())
        rulesList.add(firstRange)
        rulesList.add(scndRange)
        rulesMap[field] = Pair(firstRange, scndRange)
    }

    fun errorRate(line: String): Int {
        var errorRate = 0
        line.split(",").map { it.toInt() }.forEach { value ->
            if (notValidAtAll(value)) {
                errorRate += value
            }
        }
        return errorRate
    }

    private fun notValidAtAll(value: Int): Boolean {
        for (rule in rulesList) {
            if (value in rule) return false
        }
        return true
    }

    fun isValidAtAll(line: String): Boolean {
        line.split(",").map { it.toInt() }.forEach { value ->
            if (notValidAtAll(value)) return false
        }
        return true
    }

    fun orderRules(tickets: List<Ticket>): Map<String, Int> {
        val matchingRules = mutableMapOf<Int, MutableSet<String>>()
        for (i in tickets.first().indices) {
            val matches = mutableSetOf<String>().apply { addAll(rulesMap.keys) }
            for (ticket in tickets) {
                rulesMap.forEach { (name, ranges) ->
                    val ticketValue = ticket[i]
                    if (ticketValue !in ranges.first && ticketValue !in ranges.second) matches.remove(name)
                }
            }
            matchingRules[i] = matches
        }
        val orderedRules = mutableMapOf<String, Int>()
        while (orderedRules.size < rulesMap.size) {
            for (entry in matchingRules) {
                if (entry.value.size == 1) {
                    val value = entry.value.first()
                    orderedRules[value] = entry.key
                    matchingRules.values.forEach { set -> set.remove(value) }
                    break
                }
            }
        }
        return orderedRules
    }

}

class Ticket(val values: List<Int>) {
    val indices = values.indices

    operator fun get(i: Int): Int {
        return values[i]
    }

    companion object {
        fun create(line: String) = Ticket(line.split(",").map { it.toInt() })
    }
}
