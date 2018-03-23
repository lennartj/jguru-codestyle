/*
 * Copyright (c) jGuru Europe AB.
 * All rights reserved.
 */

package se.jguru.codestyle.projects.enforcer

import org.apache.maven.enforcer.rule.api.EnforcerRuleHelper
import org.apache.maven.project.MavenProject
import se.jguru.codestyle.projects.CommonProjectTypes
import se.jguru.codestyle.projects.ProjectType

/**
 * Enforcer rule to validate ProjectType compliance, to harmonize the pom structure in terms
 * of groupId, artifactId, packaging and (rudimentary) content checks.
 *
 * @param permittedProjectTypes A List containing the ProjectTypes permitted.
 * Defaults to `CommonProjectTypes.values().asList()`.
 * @see ProjectType
 * @author [Lennart Jörelid](mailto:lj@jguru.se), jGuru Europe AB
 */
class PermittedProjectTypeRule(val permittedProjectTypes: List<ProjectType> = CommonProjectTypes.values().asList())
    : AbstractNonCacheableEnforcerRule() {

    // Internal state
    private val partialDescription = permittedProjectTypes
        .mapIndexed { index, current -> "\n[$index/${permittedProjectTypes.size}]: ${current.javaClass.simpleName}" }
        .reduce { l, r -> l + r }

    private fun prettyPrint(project: MavenProject): String =
        "GAV [${project.groupId}:${project.artifactId}:${project.packaging}]"

    /**
     * Supplies the short rule description for this MavenEnforcerRule.
     */
    override fun getShortRuleDescription(): String = "POM groupId, artifactId and packaging " +
        "must comply with defined standard"

    /**
     * Delegate method, implemented by concrete subclasses.
     *
     * @param project The active MavenProject.
     * @param helper  The EnforcerRuleHelper instance, from which the MavenProject has been retrieved.
     * @throws RuleFailureException If the enforcer rule was not satisfied.
     */
    override fun performValidation(project: MavenProject, helper: EnforcerRuleHelper) {

        // Does any of the supplied project types match?
        val firstMatchingProjectType = permittedProjectTypes.firstOrNull { it.isCompliantWith(project) }

        if (firstMatchingProjectType == null) {

            throw RuleFailureException(
                "None of the permitted ProjectTypes matched ${prettyPrint(project)}. " +
                    "Permitted ProjectTypes:\n$partialDescription")

        } else if (helper.log.isDebugEnabled) {

            helper.log.debug("Found matching ProjectType [$firstMatchingProjectType] " +
                "for project ${prettyPrint(project)}")
        }
    }

    override fun toString(): String {

        // All Done.
        return javaClass.simpleName + " with [${permittedProjectTypes.size}] ProjectTypes:\n$partialDescription"
    }
}