/*
 * Copyright (c) jGuru Europe AB.
 * All rights reserved.
 */

package se.jguru.codestyle.projects.enforcer

import org.apache.maven.artifact.Artifact
import org.apache.maven.artifact.DefaultArtifact
import org.apache.maven.artifact.handler.DefaultArtifactHandler
import org.apache.maven.project.MavenProject
import se.jguru.codestyle.projects.CommonProjectType
import se.jguru.codestyle.projects.ProjectType
import javax.inject.Named

/**
 * Enforcer rule ensuring that [CommonProjectType.IMPLEMENTATION] and [CommonProjectType.TEST]
 * JARs are not used as compile-scope dependencies in API, SPI, or model projects.
 *
 * [CommonProjectType.BILL_OF_MATERIALS] artifacts must not appear in the `<dependencies>` block
 * (use `<dependencyManagement>` with `import` scope instead).
 *
 * @param ignoredProjectTypes Project types for which this rule is entirely skipped.
 * @param evaluateGroupIds Regex strings selecting groupIds that are subject to enforcement.
 * @param dontEvaluateGroupIds Regex strings selecting groupIds that are excluded from enforcement.
 * @param projectConverter Converts a [MavenProject] to its [ProjectType]. Defaults to
 * [CommonProjectType.getProjectType].
 * @param artifactConverter Converts an [Artifact] to its [ProjectType]. Defaults to
 * [CommonProjectType.getProjectType].
 *
 * @author <a href="mailto:lj@jguru.se">Lennart Jörelid</a>, jGuru Europe AB
 */
@Named("validateCorrectDependencies")
open class CorrectDependenciesRule @JvmOverloads constructor(

    val ignoredProjectTypes: List<ProjectType> = DEFAULT_IGNORED_PROJECT_TYPES,

    val evaluateGroupIds: List<String> = listOf("^se\\.jguru\\..*"),

    val dontEvaluateGroupIds: List<String> = listOf(
        "^se\\.jguru\\..*\\.generated\\..*",
        "^se\\.jguru\\.codestyle\\..*"
    ),

    val projectConverter: (MavenProject) -> ProjectType = { CommonProjectType.getProjectType(it) },

    val artifactConverter: (Artifact) -> ProjectType = { CommonProjectType.getProjectType(it) }

) : AbstractNonCacheableEnforcerRule() {

    // Compiled once — pattern lists are immutable after construction.
    private val compiledEvaluationPatterns: List<Regex> by lazy { evaluateGroupIds.map { Regex(it) } }
    private val compiledIgnorePatterns: List<Regex> by lazy { dontEvaluateGroupIds.map { Regex(it) } }

    /** Returns the compiled inclusion patterns (exposed for subclass use and testing). */
    fun getEvaluationPatterns(): List<Regex> = compiledEvaluationPatterns

    override fun getShortRuleDescription(): String = "Incorrect Dependency found within project."

    override fun performValidation(project: MavenProject) {

        // Resolve the project type; bail out early for ignored types.
        val projectType: ProjectType = try {
            projectConverter(project)
        } catch (e: IllegalStateException) {
            throw RuleFailureException(e.message ?: "Unknown")
        }
        if (projectType in ignoredProjectTypes) return

        // Skip if the project's groupId is explicitly excluded.
        if (matches(project.groupId, compiledIgnorePatterns)) {
            log.debug("Ignored [${project.groupId}:${project.artifactId}] — groupId is excluded from enforcement.")
            return
        }

        // Skip if the project's groupId is not included.
        if (!matches(project.groupId, compiledEvaluationPatterns)) {
            log.debug("Ignored [${project.groupId}:${project.artifactId}] — groupId is not included in enforcement.")
            return
        }

        // Gather dependency artifacts, falling back to model dependencies when the resolved set is unavailable.
        val artifactList: Collection<Artifact> = project.dependencyArtifacts
            ?: project.model.dependencies?.map {
                DefaultArtifact(it.groupId, it.artifactId, it.version, it.scope, it.type, it.classifier,
                                DefaultArtifactHandler())
            } ?: emptyList()

        artifactList
            .filterNot { Artifact.SCOPE_TEST.equals(it.scope, ignoreCase = true) }
            .filter { matches(it.groupId, compiledEvaluationPatterns) && !matches(it.groupId, compiledIgnorePatterns) }
            .forEach { current ->
                val artifactType = artifactConverter(current)
                val prefix = "Don't use $artifactType dependencies "
                when (artifactType) {
                    CommonProjectType.IMPLEMENTATION                                      ->
                        throw RuleFailureException(prefix + "outside of application projects.",
                                                   offendingArtifact = current)

                    CommonProjectType.TEST                                                ->
                        throw RuleFailureException(prefix + "in compile scope for non-test artifacts.",
                                                   offendingArtifact = current)

                    CommonProjectType.JEE_APPLICATION, CommonProjectType.PROOF_OF_CONCEPT ->
                        throw RuleFailureException(prefix + "in bundles.", offendingArtifact = current)

                    CommonProjectType.BILL_OF_MATERIALS                                   ->
                        throw RuleFailureException(
                            prefix + "in Dependency block. " +
                                "(Use only as DependencyManagement import-scoped dependencies)."
                        )

                    else                                                                  -> { /* compliant */
                    }
                }
            }
    }

    companion object {

        /**
         * Project types that are exempt from dependency-scope enforcement by default.
         */
        @JvmStatic
        val DEFAULT_IGNORED_PROJECT_TYPES: List<ProjectType> = listOf(
            CommonProjectType.JEE_APPLICATION,
            CommonProjectType.PARENT,
            CommonProjectType.ASSEMBLY,
            CommonProjectType.REACTOR,
            CommonProjectType.PROOF_OF_CONCEPT,
            CommonProjectType.EXAMPLE,
            CommonProjectType.TEST,
            CommonProjectType.JAVA_AGENT,
            CommonProjectType.STANDALONE_APPLICATION
        )
    }
}
