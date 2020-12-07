package io.github.kmakma.adventofcode.y2020

import io.github.kmakma.adventofcode.utils.Day

fun main() {
    Y2020Day07().solveAndPrint()
}

class Y2020Day07 : Day(2020, 7, "Handy Haversacks") {
    private lateinit var inputLines: List<String>
    private lateinit var bagMap: Map<String, Map<String, Int>>

    override fun initializeDay() {
        inputLines = inputInStringLines()
        bagMap = inputLines.map { line ->
            val (bag, content) = line.split(" contain ")
            val contentMap = parseContent(content.substring(0, content.lastIndex))
            bag.replace("bags", "bag") to contentMap
        }.toMap()
    }

    private fun parseContent(contentString: String): Map<String, Int> {
        if (contentString == "no other bags") return emptyMap()
        val contentList = contentString.split(", ")
        return contentList.map { contentItem ->
            val (count, bag) = contentItem.split(delimiters = arrayOf(" "), limit = 2)
            bag.replace("bags", "bag") to count.toInt()
        }.toMap()
    }

    override suspend fun solveTask1(): Any? {
        val myBag = "shiny gold bag"
        val checkedBags = mutableSetOf<String>()
        val bagContainers = bagContainers(myBag).toMutableList()
        while (bagContainers.isNotEmpty()) {
            val containerBag = bagContainers.removeFirst()
            if (checkedBags.add(containerBag)) {
                bagContainers.addAll(bagContainers(containerBag))
            }
        }
        return checkedBags.size
    }

    private fun bagContainers(bag: String): Set<String> {
        return bagMap.filter { (_, content) ->
            content[bag] != null
        }.keys
    }

    override suspend fun solveTask2(): Any? {
        val myBag = "shiny gold bag"
        val bagsToSort = mutableListOf(myBag)
        var neededBags = 0
        while (bagsToSort.isNotEmpty()) {
            val contents = bagMap[bagsToSort.removeFirst()]
            if (contents != null) {
                neededBags += contents.values.sum()
                contents.forEach { (bag, number) ->
                    repeat(number) { bagsToSort.add(bag) }
                }
            }
        }
        return neededBags
    }
}