/*
 * Copyright (c) jGuru Europe AB.
 * All rights reserved.
 */

package se.jguru.codestyle.projects.enforcer

import org.apache.maven.enforcer.rule.api.EnforcerLevel
import org.apache.maven.enforcer.rule.api.EnforcerRuleHelper
import org.apache.maven.project.MavenProject
import java.io.File
import java.io.FileFilter
import java.util.ArrayList
import java.util.SortedMap
import java.util.SortedSet
import java.util.TreeMap
import java.util.TreeSet

/**
 * Enforcer rule to enforce correct packaging for all source files within a project,
 * implying that all source files should be located within or under a package identical
 * to the groupId of the project itself.
 *
 * @author [Lennart Jörelid](mailto:lj@jguru.se), jGuru Europe AB
 */
class CorrectPackagingRule(lvl: EnforcerLevel = EnforcerLevel.ERROR,
                           private var packageExtractors: List<PackageExtractor> = DEFAULT_PACKAGE_EXTRACTORS)
    : AbstractNonCacheableEnforcerRule(lvl) {

    /**
     * The description of this CorrectPackagingRule.
     */
    override fun getShortRuleDescription(): String = "Topmost source package must be identical to project groupId."

    /**
     * Delegate method, implemented by concrete subclasses.
     *
     * @param project The active MavenProject.
     * @param helper  The EnforcerRuleHelper instance, from which the MavenProject has been retrieved.
     * @throws RuleFailureException If the enforcer rule was not satisfied.
     */
    @Throws(RuleFailureException::class)
    override fun performValidation(project: MavenProject, helper: EnforcerRuleHelper) {

        // #1) Find all java source files, and map their packages to their names.
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
        if (groupId == null || groupId == "") {

            // Don't accept empty groupIds
            throw RuleFailureException("Maven groupId cannot be null or empty.", project.artifact)

        } else {

            // Correct packaging everywhere?
            val incorrectPackages = pkg2SourceFilesMap.keys
                .filter { !it.startsWith(groupId) }
                .toCollection(TreeSet())

            if (incorrectPackages.isNotEmpty()) {

                val result = TreeMap<String, SortedSet<String>>()
                for (current in incorrectPackages) {

                    val sourceFiles = pkg2SourceFilesMap[current]
                    if(sourceFiles != null) {
                        result[current] = sourceFiles
                    }
                }

                throw RuleFailureException("Incorrect packaging detected; required [" + groupId
                    + "] but found package to file names: " + result, project.artifact)
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
                // Fire, and handle any exceptions.
                extractors.add(aClass.newInstance() as PackageExtractor)
            } catch (e: Exception) {
                throw IllegalArgumentException("Could not instantiate PackageExtractor from class ["
                    + current + "]. Validate that implementation has a default constructor, and implements the"
                    + PackageExtractor::class.java.simpleName + " interface.")
            }

        }

        // Assign if non-null.
        if (extractors.size > 0) {
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
    private fun addPackages(fileOrDirectory: File,
                            package2FileNamesMap: SortedMap<String, SortedSet<String>>) {

        packageExtractors.forEach { current ->

            // Process Files first
            //
            if (fileOrDirectory.isFile && current.sourceFileFilter.accept(fileOrDirectory)) {

                // Single source file to add.
                val thePackage = current.getPackage(fileOrDirectory)

                // Done.
                val sourceFileNames = package2FileNamesMap.getOrDefault(thePackage, sortedSetOf())
                sourceFileNames.add(fileOrDirectory.name)

            } else if (fileOrDirectory.isDirectory) {

                // Add the immediate source files
                for (currentChild in fileOrDirectory.listFiles(current.sourceFileFilter)) {
                    addPackages(currentChild, package2FileNamesMap)
                }

                // Recurse into subdirectories
                for (currentSubdirectory in fileOrDirectory.listFiles(DIRECTORY_FILTER)) {
                    addPackages(currentSubdirectory, package2FileNamesMap)
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
    }
}