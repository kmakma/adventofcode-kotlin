package io.github.kmakma.adventofcode.y2019

import kotlin.time.ExperimentalTime

@ExperimentalTime
internal class Y2019Day08 : Y2019Day(8, "Space Image Format") {
    private val imageWidth = 25
    private val imageHeight = 6
    private lateinit var layers: List<List<Int>> // TODO preferred as array, #sorting #ui
    private lateinit var image: MutableList<Int>

    override fun initializeDay() {
        layers = inputInOneLine()
            .withIndex()
            .groupBy { it.index / (imageHeight * imageWidth) }
            .map { layer -> layer.value.map { Character.getNumericValue(it.value) } }
        image = mutableListOf()
    }

    override suspend fun solveTask1(): Int {
        val zeroCounters = layers.map { layer -> layer.count { it == 0 } }
        var index = -1
        for (i in zeroCounters.indices) {
            if (index < 0 || zeroCounters[i] < zeroCounters[index]) {
                index = i
            }
        }
        val targetLayer = layers[index]
        return targetLayer.count { it == 1 } * targetLayer.count { it == 2 }
    }

    override suspend fun solveTask2(): String {
        // draw layers onto image
        for (layer in layers) {
            drawLayer(layer)
        }
        // convert image to string, with white/black squares
        val sb = StringBuilder()
        for (i in image.indices) {
            if (i > 0 && i % 25 == 0) sb.append("\n")
            if (image[i] == 0) {
                sb.append("\u25A0")
            } else {
                sb.append("\u25A1")
            }
        }
        return sb.toString()
    }

    private fun drawLayer(layer: List<Int>) {
        for (i in layer.indices) {
            when (image.getOrElse(i) { -1 }) {
                -1 -> image.add(i, layer[i])
                2 -> image[i] = layer[i]
                0, 1 -> {// overridden by a previous layer
                }
                else -> error("bad picture")
            }
        }
    }
}