package io.github.kmakma.adventofcode.y2020

import io.github.kmakma.adventofcode.utils.Day
import io.github.kmakma.adventofcode.utils.product

fun main() {
    Y2020Day20().solveAndPrint()
}

class Y2020Day20 : Day(2020, 20, "Jurassic Jigsaw") {
    private val monster = listOf(
        "                  # ".toCharArray(),
        "#    ##    ##    ###".toCharArray(),
        " #  #  #  #  #  #   ".toCharArray()
    )

    // number of lines a puzzle piece occupies in the input (1. line: id, 2-11.: 10x10 puzzle piece)
    private val pieceInputSize = 11
    private val nonDigit = "\\D".toRegex()


    private lateinit var inputList: List<String>

    override fun initializeDay() {
        inputList = inputInStringLines()

    }

    private fun pieceBorderWithReversed(input: List<String>): Pair<Int, List<String>> {
        val id = input.first().replace(nonDigit, "").toInt()
        val borders = mutableListOf<String>()
        val leftSb = StringBuilder()
        val rightSb = StringBuilder()
        for (i in 1..input.lastIndex) {
            leftSb.append(input[i].first())
            rightSb.append(input[i].last())
        }
        borders.apply {
            add(input[1])
            add(input[1].reversed())
            add(rightSb.toString())
            add(rightSb.reverse().toString())
            add(input.last().reversed())
            add(input.last().reversed())
            add(leftSb.toString())
            add(leftSb.reverse().toString())
        }
        return (id to borders.toList())
    }

    override suspend fun solveTask1(): Any? {
        val puzzle = inputList
            .filter { it.isNotBlank() }
            .chunked(pieceInputSize)
            .map { pieceBorderWithReversed(it) }
            .toMap()
        val corners = mutableListOf<Int>()
        val matchmap = mutableMapOf<Int, Int>()
        for ((id, borders) in puzzle) {
            var matches = 0
            for ((id2, borders2) in puzzle) {
                if (id != id2 && doesMatch(borders, borders2))
                    matches++
            }
            matchmap[id] = matches
            if (matches == 2) corners.add(id)
        }
        return corners.product()
    }

    private fun doesMatch(borders: List<String>, borders2: List<String>): Boolean {
        borders.forEach { b1 -> if (borders2.contains(b1)) return true }
        return false
    }

    override suspend fun solveTask2(): Any? {
        val puzzle = inputList
            .filter { it.isNotBlank() }
            .chunked(pieceInputSize)
            .map { PuzzlePiece.create(it) }
            .toMutableList()
        val setPuzzle = mutableListOf(puzzle.removeFirst())
        setPuzzle.first().fixOrientation()
        while (puzzle.size > 0) {
            val matchedIndex = matchPuzzlePiece(puzzle, setPuzzle)
            if (matchedIndex >= 0) {
                setPuzzle.add(puzzle.removeAt(matchedIndex))
            }
        }
        // puzzle "solved", now: find monsters
        val image = setPuzzle.first().getTotalImage()
        val waterPounds = image.map { line -> line.count { c -> c == '#' } }.sum()
        val monsters = countMonsters(image)
        return waterPounds - monsters * 15 // one monster = 15*#
    }

    private fun countMonsters(originalImage: List<String>): Int {
        var image = originalImage
        for (i in 0..3) {
            findMonsters(image).let { if (it > 0) return it }
            image = rotate(image)
        }
        image = image.reversed()
        for (i in 0..3) {
            findMonsters(image).let { if (it > 0) return it }
            image = rotate(image)
        }
        return 0
    }

    private fun findMonsters(image: List<String>): Int {
        var monsters = 0
        val lastCol = image.first().length - monster.first().size
        for (line in 0..(image.lastIndex - 2)) {
            for (i in 0..lastCol) {
                if (isMonster(image, line, i)) monsters++
            }
        }
        return monsters
    }

    private fun isMonster(image: List<String>, row: Int, col: Int): Boolean {
        monster.forEachIndexed { mRow, mLine ->
            mLine.forEachIndexed { mCol, mChar ->
                if (mChar == '#')
                    if (image[row + mRow][col + mCol] != '#') return false
            }
        }
        return true
    }

    private fun rotate(image: List<String>): List<String> {
        return mutableListOf<String>().apply {
            val lastIndex = image.lastIndex
            for (newRow in 0..lastIndex) {
                val sb = StringBuilder()
                for (i in image.indices.reversed()) {
                    sb.append(image[i][newRow])
                }
                this.add(sb.toString())
            }
        }
    }

    private fun matchPuzzlePiece(puzzle: List<PuzzlePiece>, setPuzzle: List<PuzzlePiece>): Int {
        puzzle.forEachIndexed { index, piece ->
            var matched = false
            for (i in setPuzzle.indices) {
                if (piece.matchTo(setPuzzle[i])) matched = true
            }
            if (matched) return index
        }
        return -1
    }
}


private class PuzzlePiece(val id: Int, content: List<String>) {
    private var fixed = false
    private var content: List<String> = content
        set(value) {
            if (fixed) error("puzzle piece $id is fixed") else field = value
        }
    private val neighbours = mutableListOf<PuzzlePiece?>(null, null, null, null)
    private val borders: List<String>
        get() = borders(content)

    fun fixOrientation() {
        fixed = true
    }

    fun matchTo(fixedPiece: PuzzlePiece): Boolean {
        val match = borderMatch(fixedPiece) ?: return false
        when {
            match.flipped && match.border % 2 == 0 -> verticalFlip()
            match.flipped && match.border % 2 == 1 -> horizontalFlip()
        }
        matchOrientation(match.border, match.otherBorder)
        fixOrientation()
        fixedPiece.neighbours[match.otherBorder] = this
        neighbours[(match.otherBorder + 2) % 4] = fixedPiece
        return true
    }

    private fun borderMatch(other: PuzzlePiece): BorderMatch? {
        val otherBorders = other.borders
        borders.forEachIndexed { bIndex, border ->
            otherBorders.forEachIndexed { obIndex, otherBorder ->
                if (border.reversed() == otherBorder) return BorderMatch(bIndex, obIndex, false)
                if (border == otherBorder) return BorderMatch(bIndex, obIndex, true)
            }
        }
        return null
    }

    private fun matchOrientation(border: Int, otherBorder: Int) {
        when (border) {
            (otherBorder + 3) % 4 -> {
                horizontalFlip()
                verticalFlip()
                rotate()
            }
            (otherBorder + 1) % 4 -> rotate()
            (otherBorder + 0) % 4 -> {
                horizontalFlip()
                verticalFlip()
            }
        }
    }

    private fun rotate() {
        val tempContent = mutableListOf<String>()
        val lastIndex = content.lastIndex
        for (newRow in 0..lastIndex) {
            val sb = StringBuilder()
            for (line in content.reversed()) {
                sb.append(line[newRow])
            }
            tempContent.add(sb.toString())
        }
        content = tempContent
    }

    private fun horizontalFlip() {
        content = content.reversed()
    }

    private fun verticalFlip() {
        content = content.map { it.reversed() }
    }

    fun getTotalImage(): List<String> {
        return topLeft().getImage()
    }

    private fun getImage(): List<String> {
        return imageRow().also { rows ->
            neighbours[2]?.let { lowerPiece -> rows.addAll(lowerPiece.getImage()) }
        }
    }

    private fun imageRow(): MutableList<String> {
        return coreImage().also { core ->
            neighbours[1]?.let { rightPiece ->
                rightPiece.imageRow().forEachIndexed { index, rightString -> core[index] += rightString }
            }
        }
    }

    private fun coreImage(): MutableList<String> {
        return content.subList(1, content.lastIndex).map { it.substring(1, it.lastIndex) }.toMutableList()
    }

    private fun topLeft(): PuzzlePiece {
        var topLeft = this
        while (topLeft.neighbours[0] != null) {
            topLeft = topLeft.neighbours[0]!!
        }
        while (topLeft.neighbours[3] != null) {
            topLeft = topLeft.neighbours[3]!!
        }
        return topLeft
    }

    companion object {
        private val nonDigit = "\\D".toRegex()
        fun create(input: List<String>): PuzzlePiece {
            val id = input.first().replace(nonDigit, "").toInt()
            val content = input.mapIndexedNotNull { index, s -> if (index == 0) null else s }
            return PuzzlePiece(id, content)
        }

        private fun borders(content: List<String>): List<String> {
            val leftSb = StringBuilder()
            val rightSb = StringBuilder()
            for (s in content) {
                leftSb.append(s.first())
                rightSb.append(s.last())
            }
            return listOf(content.first(), rightSb.toString(), content.last().reversed(), leftSb.reverse().toString())
        }

    }

    private data class BorderMatch(val border: Int, val otherBorder: Int, val flipped: Boolean = false)
}