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
 * Maven enforcement rule which ensures that Implementation [Artifact]s are not used as dependencies within
 * API, SPI or Model projects.
 *
 * @param ignoredProjectTypes List containing [ProjectType]s for which this rule should be ignored.
 * @param evaluateGroupIds List containing [Regex]ps which indicate which Maven GroupIDs should be included in this Rule
 * @param dontEvaluateGroupIds List containing [Regex]ps which indicate which Maven GroupIDs should not be included
 * (a.k.a. "ignored") in this Rule's evaluation.
 * @param projectConverter A projectConverter method to convert each [MavenProject] to a [ProjectType].
 * Defaults to `CommonProjectTypes#getProjectType`.
 * @param artifactConverter A Maven [Artifact] to [ProjectType] converter function.
 *
 * @author <a href="mailto:lj@jguru.se">Lennart J&ouml;relid</a>, jGuru Europe AB
 */
@Named("validateCorrectDependencies")
open class CorrectDependenciesRule @JvmOverloads constructor(

    val ignoredProjectTypes: List<ProjectType> = DEFAULT_IGNORED_PROJECT_TYPES,

    val evaluateGroupIds: List<String> = listOf("^se\\.jguru\\..*"),

    val dontEvaluateGroupIds: List<String> = listOf("^se\\.jguru\\..*\\.generated\\..*", "^se\\.jguru\\.codestyle\\..*"),

    val projectConverter: ((theProject: MavenProject) -> ProjectType) = { CommonProjectType.getProjectType(it) },

    val artifactConverter: ((theArtifact: Artifact) -> ProjectType) = { CommonProjectType.getProjectType(it) }
) : AbstractNonCacheableEnforcerRule() {

    fun getEvaluationPatterns(): List<Regex> = evaluateGroupIds.map { Regex(it) }
    private fun getIgnoreEvaluationPatterns(): List<Regex> = dontEvaluateGroupIds.map { Regex(it) }

    override fun getShortRuleDescription(): String = "Incorrect Dependency found within project."

    override fun performValidation(project: MavenProject) {

        // Acquire the ProjectType, and don't evaluate for ignored ProjectTypes.
        val projectType: ProjectType
        try {
            projectType = projectConverter.invoke(project)
        } catch (e: IllegalStateException) {
            throw RuleFailureException(e.message ?: "Unknown")
        }
        if (projectType in ignoredProjectTypes) {
            return
        }

        // Don't evaluate if told not to.
        if (matches(project.groupId, getIgnoreEvaluationPatterns())) {

            // Log somewhat
            log.debug("Ignored [" + project.groupId + ":" + project.artifactId
                + "] since its groupId was excluded from enforcement.")
            return

        }

        // Don't evaluate if not told to.
        if (!matches(project.groupId, getEvaluationPatterns())) {

            // Log somewhat
            log.debug("Ignored [" + project.groupId + ":" + project.artifactId
                + "] since its groupId was not included in enforcement.")
            return
        }

        // Acquire all project dependencies.
        val artifactList = project.dependencyArtifacts
            ?: project.model.dependencies?.map {
                DefaultArtifact(it.groupId,
                    it.artifactId,
                    it.version,
                    it.scope,
                    it.type,
                    it.classifier,
                    DefaultArtifactHandler())
            } ?: emptyList()

        // Acquire all project dependencies.
        for (current in artifactList) {

            // Don't evaluate for test-scope dependencies.
            if (Artifact.SCOPE_TEST.equals(current.scope, ignoreCase = true)) {
                continue
            }

            // Should this Artifact be evaluated?
            val isIncludedInEvaluation = matches(current.groupId, getEvaluationPatterns())
            val isNotExplicitlyExcludedFromEvaluation = !matches(current.groupId, getIgnoreEvaluationPatterns())
            if (isIncludedInEvaluation && isNotExplicitlyExcludedFromEvaluation) {

                val artifactProjectType = artifactConverter(current)
                val prefix = "Don't use $artifactProjectType dependencies "

                if (artifactProjectType === CommonProjectType.IMPLEMENTATION) {
                    throw RuleFailureException(prefix + "outside of application projects.",
                        offendingArtifact = current)
                }

                if (artifactProjectType === CommonProjectType.TEST) {
                    throw RuleFailureException(prefix + "in compile scope for non-test artifacts.",
                        offendingArtifact = current)
                }

                if (artifactProjectType === CommonProjectType.JEE_APPLICATION
                    || artifactProjectType === CommonProjectType.PROOF_OF_CONCEPT) {
                    throw RuleFailureException(prefix + "in bundles.",
                        offendingArtifact = current)
                }

                if (artifactProjectType === CommonProjectType.BILL_OF_MATERIALS) {
                    throw RuleFailureException(prefix + "in Dependency block. (Use only as DependencyManagement " +
                        "import-scoped dependencies).")
                }
            }
        }
    }

    companion object {

        /**
         * Default ignored project types.
         */
        @JvmStatic
        val DEFAULT_IGNORED_PROJECT_TYPES = listOf(
            CommonProjectType.JEE_APPLICATION,
            CommonProjectType.PARENT,
            CommonProjectType.ASSEMBLY,
            CommonProjectType.REACTOR,
            CommonProjectType.PROOF_OF_CONCEPT,
            CommonProjectType.EXAMPLE,
            CommonProjectType.TEST,
            CommonProjectType.JAVA_AGENT,
            CommonProjectType.STANDALONE_APPLICATION)
    }
}