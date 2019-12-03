package io.github.kmakma.adventofcode

import java.io.File

abstract class InputParser {
    abstract fun getFilePath(): String

    fun linesToList(): List<String> {
        val uri = javaClass.classLoader.getResource(getFilePath())
        if (uri != null) {
            return File(uri.file).useLines { it.toList() }
        }
        return ArrayList()
    }
}