package io.github.kmakma.adventofcode.y2020

import io.github.kmakma.adventofcode.utils.Day

fun main() {
    Y2020Day14().solveAndPrint()
}

class Y2020Day14 : Day(2020, 14, "Docking Data") {

    private lateinit var inputList: List<String>

    override fun initializeDay() {
        inputList = inputInStringLines()
    }

    override suspend fun solveTask1(): Any? {
        var andMask = 1L
        var orMask = 0L
        val mem = mutableMapOf<Int, Long>()
        for (line in inputList) {
            val (op, value) = line.split(" = ")
            if (op == "mask") {
                andMask = andMask(value)
                orMask = orMask(value)
            } else {
                val index = op.substring(4, op.lastIndex).toInt()
                mem[index] = value.toLong() and andMask or orMask
            }
        }
        return mem.values.sum()
    }

    override suspend fun solveTask2(): Any? {
        var mask = ""
        val mem = mutableMapOf<Long, Long>()
        for (line in inputList) {
            val (op, value) = line.split(" = ")
            if (op == "mask") {
                mask = value
            } else {
                val index = op.substring(4, op.lastIndex).toInt()
                val longValue = value.toLong()
                adressess(mask, index).forEach { i -> mem[i] = longValue }
            }
        }
        return mem.values.sum()
    }

    private fun andMask(value: String): Long {
        val mask = StringBuilder()
        for (c in value) {
            if (c == '0') {
                mask.append(0)
            } else {
                mask.append(1)
            }
        }
        return mask.toString().toLong(2)
    }

    private fun orMask(value: String): Long {
        val mask = StringBuilder()
        for (c in value) {
            if (c == '1') {
                mask.append(1)
            } else {
                mask.append(0)
            }
        }
        return mask.toString().toLong(2)
    }

    private fun adressess(mask: String, address: Int): List<Long> {
        val stringAdr = address.toString(2).padStart(36, '0')
        val xMask = floatingMask(mask, stringAdr)
        return fillFloatingMask(xMask)
    }

    private fun fillFloatingMask(mask: String): MutableList<Long> {
        return if (!mask.contains('X')) {
            mutableListOf(mask.toLong(2))
        } else {
            fillFloatingMask(mask.replaceFirst('X', '1')).apply {
                addAll(fillFloatingMask(mask.replaceFirst('X', '0')))
            }
        }
    }

    private fun floatingMask(mask: String, address: String): String {
        return StringBuilder().apply {
            mask.forEachIndexed { index, c ->
                if (c == '0') {
                    append(address[index])
                } else {
                    append(c)
                }
            }
        }.toString()
    }
}