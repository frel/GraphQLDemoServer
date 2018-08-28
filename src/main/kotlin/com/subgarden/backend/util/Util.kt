package com.subgarden.backend.util

import java.io.File

/**
 * Attempts to load and return the string as a resource file.
 * Returning its content.
 * This should not be used with huge files.
 */
val String.asResourceFile : File get() {
    val classLoader = ClassLoader.getSystemClassLoader()
    return File(classLoader.getResource(this).file)
}

