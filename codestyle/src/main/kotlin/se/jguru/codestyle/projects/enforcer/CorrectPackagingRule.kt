/*
 * Copyright (c) jGuru Europe AB.
 * All rights reserved.
 */

package se.jguru.codestyle.projects.enforcer

import org.apache.maven.enforcer.rule.api.EnforcerLevel
import org.apache.maven.project.MavenProject
import se.jguru.codestyle.projects.DefaultProjectType
import se.jguru.codestyle.projects.enforcer.CorrectPackagingRule.Companion.DEFAULT_IGNORED_FILENAMES
import se.jguru.codestyle.projects.enforcer.CorrectPackagingRule.Companion.DEFAULT_PACKAGE_EXTRACTORS
import java.io.File
import java.io.FileFilter
import java.util.SortedMap
import java.util.SortedSet
import javax.inject.Named

/**
 * Enforcer rule verifying that every source file in a project is placed under a package that
 * starts with the project's groupId.
 *
 * Source files matched by the [packageExtractors] are scanned recursively under each compile
 * source root. Files whose base names match [ignoredFileNames] (e.g. `module-info`, `package-info`)
 * are skipped.
 *
 * @param enforcerLevel Enforcement level. Defaults to [EnforcerLevel.ERROR].
 * @param packageExtractors Language-specific extractors used to read package declarations.
 * Defaults to [DEFAULT_PACKAGE_EXTRACTORS].
 * @param ignoredFileNames Base-name prefixes of files to skip during scanning.
 * Defaults to [DEFAULT_IGNORED_FILENAMES].
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

    override fun getShortRuleDescription(): String = "Topmost source package must be identical to project groupId."

    @Throws(RuleFailureException::class)
    override fun performValidation(project: MavenProject) {

        val compileSourceRoots = project.compileSourceRoots
        if (compileSourceRoots.isEmpty()) return

        val pkg2SourceFilesMap = sortedMapOf<String, SortedSet<String>>()
        compileSourceRoots.forEach { root -> addPackages(File(root), pkg2SourceFilesMap) }

        val groupId = project.groupId
        if (groupId.isNullOrEmpty()) {
            throw RuleFailureException(
                message = "Maven groupId cannot be null or empty.",
                offendingArtifact = project.artifact
            )
        }

        val incorrectPackages = pkg2SourceFilesMap.filterKeys { !it.startsWith(groupId) }
        if (incorrectPackages.isNotEmpty()) {
            throw RuleFailureException(
                message = "Incorrect packaging detected; required [$groupId] but found package to file names: " +
                    incorrectPackages,
                offendingArtifact = project.artifact
            )
        }
    }

    /**
     * Accepts a comma-separated list of fully-qualified [PackageExtractor] implementation class names
     * and replaces the current [packageExtractors] with freshly instantiated instances.
     *
     * Each class must have a public no-argument constructor.
     *
     * @throws IllegalArgumentException if any class cannot be loaded or instantiated.
     */
    @Throws(IllegalArgumentException::class)
    fun setPackageExtractors(packageExtractorImplementations: String) {
        val extractors = splice(packageExtractorImplementations).map { className ->
            try {
                javaClass.classLoader.loadClass(className)
                    .getDeclaredConstructor()
                    .newInstance() as PackageExtractor
            } catch (_: Exception) {
                throw IllegalArgumentException(
                    "Could not instantiate PackageExtractor from class [$className]. " +
                        "Validate that the implementation has a default constructor and implements the " +
                        PackageExtractor::class.java.simpleName + " interface."
                )
            }
        }
        if (extractors.isNotEmpty()) {
            this.packageExtractors = extractors
        }
    }

    //
    // Private helpers
    //

    /**
     * Recursively walks [fileOrDirectory], running each [PackageExtractor] over matching source
     * files and populating [package2FileNamesMap] with the discovered package-to-filename mappings.
     */
    private fun addPackages(
        fileOrDirectory: File,
        package2FileNamesMap: SortedMap<String, SortedSet<String>>
    ) {
        packageExtractors.forEach { extractor ->
            when {
                fileOrDirectory.isFile &&
                    extractor.sourceFileFilter.accept(fileOrDirectory) &&
                    !isIgnored(fileOrDirectory, ignoredFileNamePatterns) -> {

                    package2FileNamesMap
                        .getOrPut(extractor.getPackage(fileOrDirectory)) { sortedSetOf() }
                        .add(fileOrDirectory.name)
                }
                fileOrDirectory.isDirectory -> {
                    fileOrDirectory.listFiles(extractor.sourceFileFilter)
                        ?.filter { it.isFile && it.canRead() && !isIgnored(it, ignoredFileNamePatterns) }
                        ?.forEach { addPackages(it, package2FileNamesMap) }

                    fileOrDirectory.listFiles(DIRECTORY_FILTER)
                        ?.forEach { addPackages(it, package2FileNamesMap) }
                }
            }
        }
    }

    companion object {

        @JvmStatic
        private val DIRECTORY_FILTER = FileFilter { it.isDirectory }

        /**
         * Default [PackageExtractor] list: Java and Kotlin extractors.
         */
        @JvmStatic
        val DEFAULT_PACKAGE_EXTRACTORS: List<PackageExtractor> = listOf(
            JavaPackageExtractor(),
            KotlinPackageExtractor()
        )

        /**
         * Default file base-name prefixes that are excluded from package-compliance checking.
         */
        @JvmStatic
        val DEFAULT_IGNORED_FILENAMES: List<String> = listOf("module-info", "package-info")

        @JvmStatic
        internal fun isIgnored(file: File, ignoredFileNamePatterns: List<Regex>): Boolean =
            !file.isDirectory && ignoredFileNamePatterns.any { it.matches(file.name) }

        /**
         * Converts each filename in [fileNames] to a [Regex] by appending `.*`, so that any file
         * whose base name starts with one of the entries is considered ignored.
         */
        @JvmStatic
        internal fun synthesizeRegExpsFor(fileNames: List<String>): List<Regex> =
            fileNames.map { DefaultProjectType.getDefaultRegexFor("${it}.*") }
    }
}
