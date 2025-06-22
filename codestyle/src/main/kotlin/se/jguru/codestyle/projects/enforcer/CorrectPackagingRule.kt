/*
 * Copyright (c) jGuru Europe AB.
 * All rights reserved.
 */

package se.jguru.codestyle.projects.enforcer

import org.apache.maven.enforcer.rule.api.EnforcerLevel
import org.apache.maven.project.MavenProject
import se.jguru.codestyle.projects.DefaultProjectType
import se.jguru.codestyle.projects.enforcer.CorrectPackagingRule.Companion.DEFAULT_PACKAGE_EXTRACTORS
import java.io.File
import java.io.FileFilter
import java.util.SortedMap
import java.util.SortedSet
import java.util.TreeMap
import java.util.TreeSet
import javax.inject.Named

/**
 * Enforcer rule to enforce correct packaging for all source files within a project,
 * implying that all source files should be located within or under a package identical
 * to the groupId of the project itself.
 *
 * @param enforcerLevel The level of enforcement within this Rule. Defaults to `EnforcerLevel.ERROR`.
 * @param packageExtractors The PackageExtractor implementations used to find packages from source code.
 * Defaults to [DEFAULT_PACKAGE_EXTRACTORS].
 * @param ignoredFileNames Optional List of RegExp strings implying
 *
 * @author [Lennart Jörelid](mailto:lj@jguru.se), jGuru Europe AB
 */
@Named("validateCorrectPackaging")
class CorrectPackagingRule @JvmOverloads constructor(
    enforcerLevel: EnforcerLevel = EnforcerLevel.ERROR,
    private var packageExtractors: List<PackageExtractor> = DEFAULT_PACKAGE_EXTRACTORS,
    private val ignoredFileNames: List<String> = DEFAULT_IGNORED_FILENAMES
) : AbstractNonCacheableEnforcerRule(enforcerLevel) {

    private val ignoredFileNamePatterns: List<Regex> by lazy {
        synthesizeRegExpsFor(ignoredFileNames)
    }

    /**
     * The description of this CorrectPackagingRule.
     */
    override fun getShortRuleDescription(): String = "Topmost source package must be identical to project groupId."

    /**
     * Delegate method, implemented by concrete subclasses.
     *
     * @param project The active MavenProject.
     * @throws RuleFailureException If the enforcer rule was not satisfied.
     */
    @Throws(RuleFailureException::class)
    override fun performValidation(project: MavenProject) {

        // #1) Find all java source files and map their packages to their names.
        //     No source roots ==> no complaining.
        //
        val compileSourceRoots = project.compileSourceRoots
        if (compileSourceRoots.isEmpty()) {
            return
        }

        val pkg2SourceFilesMap = sortedMapOf<String, SortedSet<String>>()
        compileSourceRoots.forEach { current -> addPackages(File(current), pkg2SourceFilesMap) }

        // Retrieve the groupId of this project
        val groupId = project.groupId
        if (groupId.isNullOrEmpty()) {

            // Don't accept empty groupIds
            throw RuleFailureException(
                message = "Maven groupId cannot be null or empty.",
                offendingArtifact = project.artifact)

        } else {

            // Correct packaging everywhere?
            val incorrectPackages = pkg2SourceFilesMap.keys
                .filter { !it.startsWith(groupId) }
                .toCollection(TreeSet())

            if (incorrectPackages.isNotEmpty()) {

                val result = TreeMap<String, SortedSet<String>>()
                for (current in incorrectPackages) {

                    val sourceFiles = pkg2SourceFilesMap[current]
                    if (sourceFiles != null) {
                        result[current] = sourceFiles
                    }
                }

                throw RuleFailureException(
                    message = "Incorrect packaging detected; required [" + groupId
                        + "] but found package to file names: " + result,
                    offendingArtifact = project.artifact)
            }
        }
    }

    /**
     * Splices the supplied packageExtractorImplementations argument, which is assumed to be a comma-separated
     * string holding fully qualified class names of the PackageExtractor implementations which should be used
     * by this CorrectPackagingRule.
     *
     * @param packageExtractorImplementations a comma-separated string holding fully qualified class names of the
     * PackageExtractor implementations. Each such class must have a default
     * (i.e. no-argument) constructor.
     * @throws IllegalArgumentException if the supplied packageExtractorImplementations argument could not yield an
     * instantiated PackageExtractor instance.
     */
    @Throws(IllegalArgumentException::class)
    fun setPackageExtractors(packageExtractorImplementations: String) {

        // Instantiate the PackageExtractor instances.
        val extractors = ArrayList<PackageExtractor>()
        for (current in splice(packageExtractorImplementations)) {
            try {

                // Load the current PackageExtractor implementation class
                val aClass = javaClass.classLoader.loadClass(current)

                // The PackageExtractor implementation must have a default constructor.
                // Fire and handle any exceptions.
                extractors.add(aClass.getDeclaredConstructor().newInstance() as PackageExtractor)

            } catch (_: Exception) {

                throw IllegalArgumentException(
                    "Could not instantiate PackageExtractor from class ["
                        + current + "]. Validate that implementation has a default constructor, and implements the"
                        + PackageExtractor::class.java.simpleName + " interface.")
            }

        }

        // Assign if non-null.
        if (extractors.isNotEmpty()) {
            this.packageExtractors = extractors
        }
    }

    //
    // Private helpers
    //

    /**
     * Adds all source file found by recursive search under sourceRoot to the
     * toPopulate List, using a width-first approach.
     *
     * @param fileOrDirectory      The file or directory to search for packages and [if a directory]
     * recursively search for further source files.
     * @param package2FileNamesMap A Map relating package names extracted by the PackageExtractors.
     */
    private fun addPackages(
        fileOrDirectory: File,
        package2FileNamesMap: SortedMap<String, SortedSet<String>>) {

        packageExtractors.forEach { current ->

            // Process Files first
            //
            if (fileOrDirectory.isFile
                && current.sourceFileFilter.accept(fileOrDirectory)
                && !isIgnored(fileOrDirectory, ignoredFileNamePatterns)) {

                // Single source file to add.
                val thePackage = current.getPackage(fileOrDirectory)

                // Done.
                val sourceFileNames: SortedSet<String> = if (package2FileNamesMap[thePackage] == null) {

                    // Create a new SortedSet and add the file names to it.
                    val toReturn: SortedSet<String> = sortedSetOf()
                    package2FileNamesMap[thePackage] = toReturn

                    // All Done
                    toReturn

                } else {
                    package2FileNamesMap[thePackage]!!
                }

                sourceFileNames.add(fileOrDirectory.name)

            } else if (fileOrDirectory.isDirectory) {

                // Add the immediate source files
                fileOrDirectory.listFiles(current.sourceFileFilter)
                    ?.filter { it.isFile && it.canRead() && !isIgnored(it, ignoredFileNamePatterns) }
                    ?.forEach {
                        addPackages(it, package2FileNamesMap)
                    }

                // Recurse into subdirectories
                fileOrDirectory.listFiles(DIRECTORY_FILTER)?.forEach {
                    addPackages(it, package2FileNamesMap)
                }
            }
        }
    }

    companion object {

        // Constants
        @JvmStatic
        private val DIRECTORY_FILTER = FileFilter { candidate -> candidate.isDirectory }

        /**
         * The default List of PackageExtractors used to identify packages within found source files.
         */
        @JvmStatic
        val DEFAULT_PACKAGE_EXTRACTORS = listOf(JavaPackageExtractor(), KotlinPackageExtractor())

        /**
         * The default List of PackageExtractors used to identify packages within found source files.
         */
        @JvmStatic
        val DEFAULT_IGNORED_FILENAMES = listOf("module-info", "package-info")

        @JvmStatic
        internal fun isIgnored(file: File, ignoredFileNamePatterns: List<Regex>) : Boolean = when {
            file.isDirectory -> false
            else -> ignoredFileNamePatterns.any { it.matches(file.name) }
        }

        /**
         * Converts the supplied list of filenames to corresponding Regex
         *
         * @param fileNames File names to convert by adding <code>.*</code>, so pre-matching.
         * @return A list of Regexs for the filenames.
         */
        @JvmStatic
        internal fun synthesizeRegExpsFor(fileNames: List<String>) : List<Regex> {
            val toReturn = mutableListOf<Regex>()

            if (fileNames.isNotEmpty()) {

                fileNames
                    .map { DefaultProjectType.getDefaultRegexFor("${it}.*") }
                    .forEach { toReturn.add(it) }
            }

            // All Done.
            return toReturn
        }
    }
}