/*
 * Copyright (c) jGuru Europe AB.
 * All rights reserved.
 */

package se.jguru.codestyle.projects.enforcer

import org.apache.maven.artifact.Artifact
import org.apache.maven.enforcer.rule.api.EnforcerRuleHelper
import org.apache.maven.project.MavenProject
import se.jguru.codestyle.projects.CommonProjectTypes
import se.jguru.codestyle.projects.ProjectType

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
class CorrectDependenciesRule(

    ignoredProjectTypes: List<ProjectType>?,

    evaluateGroupIds: List<String>?,

    dontEvaluateGroupIds: List<String>?,

    projectConverter: ((theProject: MavenProject) -> ProjectType)?,

    artifactConverter: ((theArtifact: Artifact) -> ProjectType)?) : AbstractNonCacheableEnforcerRule() {

    /**
     * Default constructor using default values for all arguments.
     */
    constructor() : this(null, null, null, null, null)

    // Internal state
    private val ignoredProjectTypes: List<ProjectType> = ignoredProjectTypes ?: listOf(
        CommonProjectTypes.JEE_APPLICATION,
        CommonProjectTypes.PARENT,
        CommonProjectTypes.ASSEMBLY,
        CommonProjectTypes.REACTOR,
        CommonProjectTypes.PROOF_OF_CONCEPT,
        CommonProjectTypes.EXAMPLE,
        CommonProjectTypes.TEST,
        CommonProjectTypes.JAVA_AGENT,
        CommonProjectTypes.STANDALONE_APPLICATION)

    /**
     * List containing [Regex]ps which indicate which Maven GroupIDs should be included in this Rule's evaluation.
     * Defaults to [EVALUATE_GROUPIDS] unless explicitly given.
     */
    private var evaluateGroupIds: List<String> = evaluateGroupIds ?: listOf("^se\\.jguru\\.nazgul\\..*")
    private var dontEvaluateGroupIds: List<String> = dontEvaluateGroupIds
        ?: listOf("^se\\.jguru\\..*\\.generated\\..*", "^se\\.jguru\\.codestyle\\..*")

    private var projectConverter: (theProject: MavenProject) -> ProjectType = projectConverter
        ?: { CommonProjectTypes.getProjectType(it) }

    private var artifactConverter: (theArtifact: Artifact) -> ProjectType = artifactConverter
        ?: { CommonProjectTypes.getProjectType(it) }

    private fun getEvaluationPatterns(): List<Regex> = evaluateGroupIds.map { Regex(it) }
    private fun getIgnoreEvaluationPatterns(): List<Regex> = dontEvaluateGroupIds.map { Regex(it) }

    /**
     * @return A human-readable short description for this AbstractEnforcerRule.
     * (Example: "No -impl dependencies permitted in this project")
     */
    override fun getShortRuleDescription(): String = "Impl projects should only be injected in applications (not in " +
        "Models, APIs or SPIs)."

    override fun performValidation(project: MavenProject, helper: EnforcerRuleHelper) {

        // Acquire the ProjectType, and don't evaluate for ignored ProjectTypes.
        val projectType = projectConverter.invoke(project)
        if (projectType in ignoredProjectTypes) {
            return
        }

        // Don't evaluate if told not to.
        if (matches(project.groupId, getIgnoreEvaluationPatterns())) {

            // Log somewhat
            helper.log.debug("Ignored [" + project.groupId + ":" + project.artifactId
                + "] since its groupId was excluded from enforcement.")
            return

        }

        // Don't evaluate if not told to.
        if (!matches(project.groupId, getEvaluationPatterns())) {

            // Log somewhat
            helper.log.debug("Ignored [" + project.groupId + ":" + project.artifactId
                + "] since its groupId was not included in enforcement.")
            return
        }

        // Acquire all project dependencies.
        for (current in project.dependencyArtifacts) {

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

                if (artifactProjectType === CommonProjectTypes.IMPLEMENTATION) {
                    throw RuleFailureException(prefix + "outside of application projects.",
                        offendingArtifact = current)
                }

                if (artifactProjectType === CommonProjectTypes.TEST) {
                    throw RuleFailureException(prefix + "in compile scope for non-test artifacts.",
                        offendingArtifact = current)
                }

                if (artifactProjectType === CommonProjectTypes.JEE_APPLICATION
                    || artifactProjectType === CommonProjectTypes.PROOF_OF_CONCEPT) {
                    throw RuleFailureException(prefix + "in bundles.",
                        offendingArtifact = current)
                }
            }
        }
    }
}