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
import se.jguru.codestyle.projects.enforcer.CorrectDependenciesRule.Companion.EVALUATE_GROUPIDS
import se.jguru.codestyle.projects.enforcer.CorrectDependenciesRule.Companion.IGNORED_PROJECT_TYPES
import se.jguru.codestyle.projects.enforcer.CorrectDependenciesRule.Companion.IGNORE_GROUPIDS

/**
 * Maven enforcement rule which ensures that Implementation [Artifact]s are not used as dependencies within
 * API, SPI or Model projects.
 *
 * @author <a href="mailto:lj@jguru.se">Lennart J&ouml;relid</a>, jGuru Europe AB
 */
class CorrectDependenciesRule(

    /**
     * List containing [ProjectType]s for which this rule should be ignored.
     * Defaults to [IGNORED_PROJECT_TYPES] unless explicitly given.
     */
    var ignoredProjectTypes: List<ProjectType> = IGNORED_PROJECT_TYPES,

    /**
     * List containing [Regex]ps which indicate which Maven GroupIDs should be included in this Rule's evaluation.
     * Defaults to [EVALUATE_GROUPIDS] unless explicitly given.
     */
    var evaluateGroupIds: List<Regex> = listOf(EVALUATE_GROUPIDS).map { Regex(it) },

    /**
     * List containing [Regex]ps which indicate which Maven GroupIDs should not be included ("ignored") in this Rule's
     * evaluation. Defaults to [IGNORE_GROUPIDS] unless explicitly given.
     */
    var dontEvaluateGroupIds: List<Regex> = listOf(IGNORE_GROUPIDS).map { Regex(it) },

    /**
     * A projectConverter method to convert each [MavenProject] to a [ProjectType].
     * Defaults to `CommonProjectTypes#getProjectType`.
     *
     * @see CommonProjectTypes
     */
    var projectConverter: (theProject: MavenProject) -> ProjectType = { CommonProjectTypes.getProjectType(it) },

    /**
     * A Maven [Artifact] to [ProjectType] converter function.
     *
     * @see CommonProjectTypes
     */
    var artifactConverter: (theArtifact: Artifact) -> ProjectType = { CommonProjectTypes.getProjectType(it) }) :
    AbstractNonCacheableEnforcerRule() {

    companion object {

        /**
         * Pattern defining groupID for artifacts that should be evaluated by this EnforcerRule instance.
         * This default value will be used unless overridden by [explicit] configuration.
         */
        val EVALUATE_GROUPIDS = "^se\\.jguru\\.nazgul\\..*"

        /**
         * Pattern defining patterns for groupIDs that should be ignored by this EnforcerRule instance.
         * This default value will be used unless overridden by [explicit] configuration.
         */
        val IGNORE_GROUPIDS = "^se\\.jguru\\..*\\.generated\\..*," +
            "^se\\.jguru\\.codestyle\\..*"

        /**
         * ProjectTypes for which this rule should be ignored.
         */
        val IGNORED_PROJECT_TYPES: List<ProjectType> = listOf(
            CommonProjectTypes.JEE_APPLICATION,
            CommonProjectTypes.PARENT,
            CommonProjectTypes.ASSEMBLY,
            CommonProjectTypes.REACTOR,
            CommonProjectTypes.PROOF_OF_CONCEPT,
            CommonProjectTypes.EXAMPLE,
            CommonProjectTypes.TEST,
            CommonProjectTypes.JAVA_AGENT,
            CommonProjectTypes.STANDALONE_APPLICATION)
    }

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
        if (matches(project.groupId, this.dontEvaluateGroupIds)) {

            // Log somewhat
            helper.log.debug("Ignored [" + project.groupId + ":" + project.artifactId
                + "] since its groupId was excluded from enforcement.")
            return

        }

        // Don't evaluate if not told to.
        if (!matches(project.groupId, evaluateGroupIds)) {

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
            val isIncludedInEvaluation = matches(current.groupId, evaluateGroupIds)
            val isNotExplicitlyExcludedFromEvaluation = !matches(current.groupId, dontEvaluateGroupIds)
            if (isIncludedInEvaluation && isNotExplicitlyExcludedFromEvaluation) {

                val artifactProjectType = artifactConverter(current)
                val prefix = "Don't use $artifactProjectType dependencies "

                if (artifactProjectType === CommonProjectTypes.IMPLEMENTATION) {
                    throw RuleFailureException(prefix + "outside of application projects.", current)
                }

                if (artifactProjectType === CommonProjectTypes.TEST) {
                    throw RuleFailureException(prefix + "in compile scope for non-test artifacts.", current)
                }

                if (artifactProjectType === CommonProjectTypes.JEE_APPLICATION
                    || artifactProjectType === CommonProjectTypes.PROOF_OF_CONCEPT) {
                    throw RuleFailureException(prefix + "in bundles.", current)
                }
            }
        }
    }
}