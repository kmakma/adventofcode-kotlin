package io.github.kmakma.adventofcode.y2020

import io.github.kmakma.adventofcode.utils.Day

fun main() {
    Y2020Day21().solveAndPrint()
}

class Y2020Day21 : Day(2020, 21, "Allergen Assessment") {
    private lateinit var inputList: List<String>
    private lateinit var allIngredients: List<String>
    private lateinit var allergens: Map<String, String>


    override fun initializeDay() {
        inputList = inputInStringLines()
        val ingredientsByAllergens = mutableMapOf<String, Set<String>>()
        allIngredients = inputList.flatMap { line ->
            val (ingredients, allergens) = getIngredientsAndAllergens(line)
            for (allergen in allergens) {
                ingredientsByAllergens.merge(allergen, ingredients.toSet(), Set<String>::intersect)
            }
            return@flatMap ingredients
        }
        allergens = mutableMapOf<String, String>().apply {
            while (ingredientsByAllergens.isNotEmpty()) {
                val definiteAllergens = ingredientsByAllergens
                    .filter { (_, ingredients) -> ingredients.size == 1 }
                    .mapValues { (_, ingredients) -> ingredients.first() }
                putAll(definiteAllergens)
                definiteAllergens.keys.forEach { ingredientsByAllergens.remove(it) }
                val knownIngredients = definiteAllergens.values
                ingredientsByAllergens.replaceAll { _, ingredients ->
                    ingredients.filter { !knownIngredients.contains(it) }.toSet()
                }
            }
        }
    }

    private fun getIngredientsAndAllergens(line: String): Pair<List<String>, List<String>> {
        val (ingredients, allergens) = line.removeSuffix(")").split(" (contains ")
        return Pair(ingredients.split(" "), allergens.split(", "))
    }

    override suspend fun solveTask1(): Any? {
        return with(allergens.values) { allIngredients.filter { !contains(it) } }.size
    }

    override suspend fun solveTask2(): Any? {
        val sb = StringBuilder()
        allergens.keys.sorted().forEach { sb.append("${allergens[it]},") }
        return sb.removeSuffix(",")
    }
}