package io.github.kmakma.adventofcode.y2020

import io.github.kmakma.adventofcode.utils.Day

fun main() {
    Y2020Day21().solveAndPrint()
}

class Y2020Day21 : Day(2020, 21, "Allergen Assessment") {
    private val allIngredients: MutableList<Pair<List<String>, List<String>>> = mutableListOf()
    private lateinit var allergeneIngredients: MutableMap<String, Set<String>>
    private lateinit var inputList: List<String>

    override fun initializeDay() {
        inputList = inputInStringLines()
        allergeneIngredients = mutableMapOf()

        for (line in inputList) {
            val allergens = getAllergens(line)
            val ingredients = getIngredients(line)
            allIngredients.add(Pair(ingredients, allergens))
            for (allergen in allergens) {
                intersectIngredients(allergen, ingredients)
            }
        }

    }

    private fun getAllergens(line: String): List<String> {
        val index = line.indexOf("(")
        if (index > 0) {
            return line.substring(index + 10, line.lastIndex).split(", ")
        }
        return emptyList()
    }

    private fun getIngredients(line: String): List<String> {
        val index = line.indexOf(" (")
        if (index > 0) {
            return line.substring(0, index).split(" ")
        }
        return line.split(" ")
    }

    private fun intersectIngredients(allergen: String, ingredients: List<String>): Set<String> {
        val newIngredients = allergeneIngredients[allergen]?.intersect(ingredients) ?: ingredients.toSet()
        allergeneIngredients[allergen] = newIngredients
        return newIngredients
    }

    private fun simplifyAllergenIngredients(): Map<String, String> {
        val allgenIng = mutableMapOf<String, String>()
        while (allgenIng.size < allergeneIngredients.size) {
            allergeneIngredients.mapValues { (allergen, ingredients) ->
                if (ingredients.size == 1) {
                    allgenIng.putIfAbsent(allergen, ingredients.first())
                    return@mapValues ingredients
                } else {
                    val newIng = ingredients.filter { !allgenIng.values.contains(it) }
                    if (newIng.size == 1) allgenIng.put(allergen, newIng.first())
                    return@mapValues newIng
                }
            }
        }
        return allgenIng
    }

    override suspend fun solveTask1(): Any? {
        val allergenIng = allergeneIngredients.values.flatten()
        val a = allIngredients.flatMap { it.first }.filter { !allergenIng.contains(it) }
        return a.size
    }

    override suspend fun solveTask2(): Any? {
        val allgenIng = simplifyAllergenIngredients()
        val sb = StringBuilder()
        allgenIng.keys.sorted().forEach { sb.append("${allgenIng[it]},") }
        return sb.removeSuffix(",")
    }
}