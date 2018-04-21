/*
 * Copyright (c) jGuru Europe AB.
 * All rights reserved.
 */

package se.jguru.codestyle.projects.enforcer

import java.io.File
import java.io.FileFilter

/**
 * An incorrect PackageExtractor implementation, which does not have a default constructor.
 *
 * @author [Lennart Jörelid](mailto:lj@jguru.se), jGuru Europe AB
 */
class IncorrectNoDefaultConstructorPackageExtractor(
    @Suppress("UNUSED_PARAMETER") someConstructorArgument: String) : PackageExtractor {

    override val sourceFileFilter: FileFilter = FileFilter { true }

    override fun getPackage(sourceFile: File): String = "unimportant"
}

/**
 * A silly - but correctly implemented PackageExtractor, having a default constructor.
 *
 * @author [Lennart Jörelid](mailto:lj@jguru.se), jGuru Europe AB
 */
class SillyPackageExtractor : PackageExtractor {

    override val sourceFileFilter: FileFilter = FileFilter { aFile -> aFile.isFile }

    override fun getPackage(sourceFile: File): String = "silly"
}