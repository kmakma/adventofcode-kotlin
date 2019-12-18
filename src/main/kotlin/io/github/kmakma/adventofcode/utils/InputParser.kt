package io.github.kmakma.adventofcode.utils

import java.io.File

abstract class InputParser {

    protected abstract fun getFilePath(): String

    private fun getUri() = javaClass.classLoader.getResource(getFilePath())

    /**
     * All lines of input file to a list of strings (line=string)
     */
    protected fun inputInStringLines(): List<String> {
        getUri()?.let { url ->
            return File(url.file).useLines { sequence -> sequence.toList() }
        }
        return emptyList()
    }

    /**
     * All lines of input file to basically a char matrix.. excepts they are lists
     */
    protected fun inputInCharLines(): List<List<Char>> {
        getUri()?.let {
            val lines = mutableListOf<List<Char>>()
            File(it.file).forEachLine { line -> lines.add(line.toList()) }
            return lines
        }
        return emptyList()
    }

    /**
     * The whole file as one string
     */
    protected fun inputInOneLine(): String {
        getUri()?.let {
            return File(it.file).readText()
        }
        return ""
    }

    /**
     * All lines of input file to lists of strings separated by [delimiters] (which is by default ",")
     */
    protected fun inputInSplitLines(vararg delimiters: String = arrayOf(",")): List<List<String>> {
        return inputInStringLines().map { it.split(*delimiters) }
    }


    /**
     * Whole input file as one line to list of strings separated by [delimiters] (which is by default ",")
     */
    protected fun inputInOneSplitLine(vararg delimiters: String = arrayOf(",")): List<String> {
        return inputInOneLine().split(*delimiters)
    }
}