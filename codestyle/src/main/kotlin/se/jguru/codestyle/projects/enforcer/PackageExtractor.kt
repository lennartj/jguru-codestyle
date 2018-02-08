/*
 * Copyright (c) jGuru Europe AB.
 * All rights reserved.
 */

package se.jguru.codestyle.projects.enforcer

import java.io.File
import java.io.FileFilter

/**
 * Specification for extracting a package definition from the supplied sourceFile.
 *
 * @author [Lennart Jörelid](mailto:lj@jguru.se), jGuru Europe AB
 */
interface PackageExtractor {

    /**
     * Retrieves a FileFilter which identifies the source files that can be handled by this PackageExtractor.
     *
     * @return a non-null FileFilter which identifies the source files that can be handled by this PackageExtractor.
     */
    val sourceFileFilter: FileFilter

    /**
     * Retrieves the package definition from the supplied sourceFile.
     *
     * @param sourceFile The sourceFile from which the package definition should be extracted.
     * @return The package of the sourceFile.
     */
    fun getPackage(sourceFile: File): String
}

/**
 * Utility class containing constants and generic Pattern definitions.
 *
 * @author [Lennart Jörelid](mailto:lj@jguru.se), jGuru Europe AB
 */
abstract class AbstractSimplePackageExtractor : PackageExtractor {

    companion object {

        /**
         * The "package" reserved word.
         */
        const val PACKAGE_WORD = "package"

        /**
         * Retrieves the RegExp able to match a Package statement within a java, kotlin or C++ file.
         * @param optionalSemicolonTermination if `true` the package statement may optionally be terminated by a
         * semicolon. Otherwise this termination is required.
         */
        fun getPackageRegExp(optionalSemicolonTermination: Boolean) = Regex("^\\s*$PACKAGE_WORD\\s*" +
                "([a-zA-Z_][a-zA-Z0-9_]*(\\.[a-zA-Z_][a-zA-Z0-9_]*)*)?\\s*?;" +
                when (optionalSemicolonTermination) {
                    true -> "?"
                    false -> ""
                } +
                "\\s*$");

        /**
         * Utility method which retrieves a FileFilter which accepts Files whose name ends
         * with the given suffix, case insensitive matching.
         */
        fun getSuffixFileFilter(requiredLowerCaseSuffix: String) = FileFilter { aFile ->
            aFile != null &&
                    aFile.isFile &&
                    aFile.name.toLowerCase().trim().endsWith(requiredLowerCaseSuffix.toLowerCase())
        }
    }
}

/**
 * [PackageExtractor] for Kotlin source files.
 *
 * @author [Lennart Jörelid](mailto:lj@jguru.se), jGuru Europe AB
 */
class KotlinPackageExtractor : AbstractSimplePackageExtractor() {

    // Internal state
    private val packageRegEx = getPackageRegExp(true)

    override val sourceFileFilter: FileFilter
        get() = getSuffixFileFilter(".kt")

    override fun getPackage(sourceFile: File): String {

        for (aLine: String in sourceFile.readLines(Charsets.UTF_8)) {

            if(packageRegEx.matches(aLine)) {

                val lastIndexInLine = when (aLine.contains(";")) {
                    true -> aLine.indexOfFirst { it == ';' }
                    false -> aLine.length
                }

                // All Done.
                return aLine.trim().substring(PACKAGE_WORD.length, lastIndexInLine).trim()
            }
        }

        // Complain
        throw IllegalArgumentException("Could not acquire package from file [${sourceFile.path}]")
    }
}

/**
 * [PackageExtractor] for Java source files.
 *
 * @author [Lennart Jörelid](mailto:lj@jguru.se), jGuru Europe AB
 */
class JavaPackageExtractor : AbstractSimplePackageExtractor() {

    // Internal state
    private val packageRegEx = getPackageRegExp(false)

    override val sourceFileFilter: FileFilter
        get() = getSuffixFileFilter(".java")

    override fun getPackage(sourceFile: File): String {

        for (aLine: String in sourceFile.readLines(Charsets.UTF_8)) {

            if(packageRegEx.matches(aLine)) {

                val lastIndexInLine = when (aLine.contains(";")) {
                    true -> aLine.indexOfFirst { it == ';' }
                    false -> aLine.length
                }

                // All Done.
                return aLine.trim().substring(PACKAGE_WORD.length, lastIndexInLine).trim()
            }
        }

        // Complain
        throw IllegalArgumentException("Could not acquire package from file [${sourceFile.path}]")
    }
}
