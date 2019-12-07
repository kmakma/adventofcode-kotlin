package io.github.kmakma.adventofcode

import java.io.File

abstract class InputParser {
    protected abstract fun getFilePath(): String

    protected fun linesToList(): List<String> {
        val uri = javaClass.classLoader.getResource(getFilePath())
        if (uri != null) {
            return File(uri.file).useLines { it.toList() }
        }
        return ArrayList()
    }

    protected fun csvLinesToLists(): List<List<String>> {
        return linesToList().map { it.split(",") }
    }

    protected fun firstCsvLineToList(): List<String> {
        return linesToList().first().split(",")
    }
}