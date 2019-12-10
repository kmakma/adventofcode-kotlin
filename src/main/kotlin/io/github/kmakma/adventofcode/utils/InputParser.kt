package io.github.kmakma.adventofcode.utils

import java.io.File

abstract class InputParser {
    protected abstract fun getFilePath(): String

    protected fun linesToList(): List<String> {
        val uri = javaClass.classLoader.getResource(getFilePath())
        if (uri != null) {
            return File(uri.file).useLines { it.toList() }
        }
        return emptyList()
    }

    protected fun firstLine(): String {
        return linesToList().first()
    }

    protected fun csvLinesToLists(vararg delimiters: String = arrayOf(",")): List<List<String>> {
        return linesToList().map { it.split(*delimiters) }
    }

    protected fun firstCsvLineToList(vararg delimiters: String = arrayOf(",")): List<String> {
        return linesToList().first().split(*delimiters)
    }
}