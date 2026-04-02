/*
 * Copyright (c) jGuru Europe AB.
 * All rights reserved.
 */

package se.jguru.codestyle.projects.enforcer

import java.io.File
import java.io.FileFilter

/**
 * Extracts the package declaration from a source file of a specific language.
 *
 * @author [Lennart Jörelid](mailto:lj@jguru.se), jGuru Europe AB
 */
interface PackageExtractor {

    /**
     * [FileFilter] that accepts the source file types handled by this extractor.
     */
    val sourceFileFilter: FileFilter

    /**
     * Extracts and returns the package name declared in [sourceFile].
     * Returns an empty string when no package declaration is found (default/unnamed package).
     */
    fun getPackage(sourceFile: File): String
}

/**
 * Shared constants and regex builders for [PackageExtractor] implementations.
 *
 * @author [Lennart Jörelid](mailto:lj@jguru.se), jGuru Europe AB
 */
abstract class AbstractSimplePackageExtractor : PackageExtractor {

    companion object {

        /** The `package` reserved word used in Java and Kotlin source files. */
        const val PACKAGE_WORD = "package"

        /**
         * Builds a [Regex] that matches a package declaration line.
         *
         * @param optionalSemicolonTermination When `true` the trailing semicolon is optional
         * (Kotlin syntax); when `false` it is required (Java syntax).
         */
        fun getPackageRegExp(optionalSemicolonTermination: Boolean): Regex {
            val semicolonSuffix = if (optionalSemicolonTermination) ";?" else ";"
            return Regex(
                "^\\s*$PACKAGE_WORD\\s*" +
                    "([a-zA-Z_][a-zA-Z0-9_]*(\\.[a-zA-Z_][a-zA-Z0-9_]*)*)?\\s*?$semicolonSuffix\\s*$"
            )
        }

        /**
         * Returns a [FileFilter] that accepts regular files whose name ends with [requiredSuffix],
         * using case-insensitive comparison.
         */
        fun getSuffixFileFilter(requiredSuffix: String) = FileFilter { aFile ->
            aFile != null && aFile.isFile && aFile.name.endsWith(requiredSuffix, ignoreCase = true)
        }
    }
}

/**
 * [PackageExtractor] for Kotlin source files (`.kt`).
 *
 * Kotlin package declarations may omit the trailing semicolon.
 *
 * @author [Lennart Jörelid](mailto:lj@jguru.se), jGuru Europe AB
 */
class KotlinPackageExtractor : AbstractSimplePackageExtractor() {

    private val packageRegEx = getPackageRegExp(optionalSemicolonTermination = true)

    override val sourceFileFilter: FileFilter = getSuffixFileFilter(".kt")

    override fun getPackage(sourceFile: File): String {
        check(!sourceFile.isDirectory) { "Expected a file, but received directory [${sourceFile.path}]" }

        if (!sourceFile.isFile) return ""

        return sourceFile.readLines(Charsets.UTF_8)
            .firstOrNull { packageRegEx.matches(it) }
            ?.let { line ->
                val end = if (line.contains(';')) line.indexOfFirst { it == ';' } else line.length
                line.trim().substring(PACKAGE_WORD.length, end).trim()
            } ?: ""
    }
}

/**
 * [PackageExtractor] for Java source files (`.java`).
 *
 * Java package declarations must end with a semicolon.
 *
 * @author [Lennart Jörelid](mailto:lj@jguru.se), jGuru Europe AB
 */
class JavaPackageExtractor : AbstractSimplePackageExtractor() {

    private val packageRegEx = getPackageRegExp(optionalSemicolonTermination = false)

    override val sourceFileFilter: FileFilter = getSuffixFileFilter(".java")

    override fun getPackage(sourceFile: File): String =
        sourceFile.readLines(Charsets.UTF_8)
            .firstOrNull { packageRegEx.matches(it) }
            ?.let { line ->
                val end = if (line.contains(';')) line.indexOfFirst { it == ';' } else line.length
                line.trim().substring(PACKAGE_WORD.length, end).trim()
            } ?: ""
}
