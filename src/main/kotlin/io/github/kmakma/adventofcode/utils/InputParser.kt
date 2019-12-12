package io.github.kmakma.adventofcode.utils

import java.io.File

abstract class InputParser {
    protected abstract fun getFilePath(): String

    /**
     * All lines of input file to a list of strings (line=string)
     */
    protected fun linesToList(): List<String> {
        val uri = javaClass.classLoader.getResource(getFilePath())
        if (uri != null) {
            return File(uri.file).useLines { it.toList() }
        }
        return emptyList()
    }

    /**
     * First line of input file as string
     */
    protected fun firstLine(): String {
        return linesToList().first()
    }

    /**
     * All lines of input file to lists of strings separated by [delimiters] (which is by default ",")
     */
    protected fun csvLinesToLists(vararg delimiters: String = arrayOf(",")): List<List<String>> {
        return linesToList().map { it.split(*delimiters) }
    }


    /**
     * First line of input file to list of strings separated by [delimiters] (which is by default ",")
     */
    protected fun firstCsvLineToList(vararg delimiters: String = arrayOf(",")): List<String> {
        return linesToList().first().split(*delimiters)
    }
}