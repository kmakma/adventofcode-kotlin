package io.github.kmakma.adventofcode.y2019

class Y2019Day08 : Y2019Day(
    8,
    "T1", "T2"
) {
    private lateinit var encodedImage: List<Int>
    private lateinit var encodedImageString: String
    private val imageWidth = 25
    private val imageHeight = 6
    private lateinit var layers: List<List<Int>>
    private val image = mutableListOf<Int>()

    override fun solve() {
        encodedImageString = getInput()
        encodedImage = encodedImageString.map(Character::getNumericValue)
        resultTask1 = calculateChecksum()
        resultTask2 = drawImage()
//        printImage(image)
    }

    private fun printImage(img: List<Int>) {
        print("#-------------------------#\n|")
        for (i in img.indices) {
            if (i > 0 && i % 25 == 0) print("|\n|")
            when (img[i]) {
                0 -> print("\u25A0")
                1 -> print("\u25A1")
                2 -> print(" ")
                else -> print(img[i])
            }
        }
        println("|\n#-------------------------#")
    }

    private fun calculateChecksum(): Int {
        layers = encodedImageString
            .withIndex()
            .groupBy { it.index / (imageHeight * imageWidth) }
            .map { layer -> layer.value.map { Character.getNumericValue(it.value) } }

        val zeroCounts = layers.map { layer -> layer.count { it == 0 } }
        var index = -1
        for (i in zeroCounts.indices) {
            if (index < 0) {
                index = i
            } else if (zeroCounts[i] < zeroCounts[index]) {
                index = i
            }
        }
        val targetLayer = layers[index]
        return targetLayer.count { it == 1 } * targetLayer.count { it == 2 }
    }

    private fun drawImage(): String {
        for (layer in layers) {
            drawLayer(layer)
//            printImage(image)
        }

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
                0, 1 -> {
                }
                else -> error("bad picture")
            }
        }
    }

    override fun getInput(): String {
        return firstLine()
    }
}