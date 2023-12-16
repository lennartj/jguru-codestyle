/*
 * Copyright (c) jGuru Europe AB.
 * All rights reserved.
 */

package se.jguru.codestyle.projects.enforcer

import org.apache.maven.project.MavenProject
import se.jguru.codestyle.projects.CommonProjectType
import se.jguru.codestyle.projects.ComplianceStatusHolder
import se.jguru.codestyle.projects.DefaultProjectType.Companion.getDefaultRegexFor
import se.jguru.codestyle.projects.ProjectType
import java.util.TreeMap
import javax.inject.Named

/**
 * Enforcer rule to validate ProjectType compliance, to harmonize the pom structure in terms
 * of groupId, artifactId, packaging and (rudimentary) content checks.
 *
 * @param dontEvaluateGroupIds Ignore any dependencies whose groupIDs match any of the patterns supplied
 * @param permittedProjectTypes A List containing the ProjectTypes permitted.
 * Defaults to `CommonProjectTypes.values().asList()`.
 *
 * @see ProjectType
 * @author [Lennart Jörelid](mailto:lj@jguru.se), jGuru Europe AB
 */
@Suppress("LeakingThis")
@Named("validatePermittedProjectTypes")
open class PermittedProjectTypeRule(

    @SuppressWarnings("WeakerAccess")
    open var dontEvaluateGroupIds: List<Regex>,

    @SuppressWarnings("WeakerAccess")
    open var permittedProjectTypes: List<ProjectType>
) : AbstractNonCacheableEnforcerRule() {

    constructor() : this(mutableListOf(), CommonProjectType.entries)

    constructor(dontEvaluateGroupIdPatterns: List<String>) : this(
        dontEvaluateGroupIdPatterns.map { getDefaultRegexFor(it) }.toList(),
        CommonProjectType.entries)

    // Internal state
    private val partialDescription = permittedProjectTypes
        .mapIndexed { index, current -> "\n[$index/${permittedProjectTypes.size}]: $current" }
        .reduce { l, r -> l + r }

    /**
     * Supplies the short rule description for this MavenEnforcerRule.
     */
    override fun getShortRuleDescription(): String = "POM groupId, artifactId and packaging " +
        "must comply with defined standard"

    /**
     * Delegate method, implemented by concrete subclasses.
     *
     * @param project The active MavenProject.
     * @throws RuleFailureException If the enforcer rule was not satisfied.
     */
    override fun performValidation(project: MavenProject) {

        // Does any of the supplied project types match?
        val firstMatchingProjectType = permittedProjectTypes
            .firstOrNull { it.getComplianceStatus(project, dontEvaluateGroupIds).isCompliant }

        if (firstMatchingProjectType == null) {

            // Emit failure message for the ProjectTypes "closest" to this one
            val projectFailureDistanceMap = TreeMap<Int, MutableList<Pair<ComplianceStatusHolder, ProjectType>>>()
            permittedProjectTypes.forEach { pt ->

                val complianceHolder = pt.getComplianceStatus(project)

                var holderList = projectFailureDistanceMap[complianceHolder.complianceDistance]
                if (holderList == null) {
                    holderList = mutableListOf()
                    projectFailureDistanceMap[complianceHolder.complianceDistance] = holderList
                }

                holderList.add(Pair(complianceHolder, pt))
            }

            val closestProjectTypes = projectFailureDistanceMap
                .firstEntry()
                .value
                .map { " [${it.second.getIdentifier()}]: ${it.first}" }
                .reduce { acc, s -> "$acc\n$s" }

            throw RuleFailureException(
                "None of the permitted ProjectTypes matched ${prettyPrint(project)}. " +
                    "\n Failure reasons per similar ProjectType:\n$closestProjectTypes")

        } else {
            log.debug("Found matching ProjectType [$firstMatchingProjectType] " +
                                 "for project ${prettyPrint(project)}")
        }
    }

    override fun toString(): String {

        val ignoreDescription = when (dontEvaluateGroupIds.isEmpty()) {
            true -> "ignoring no artifacts."
            else -> "ignoring artifacts matching [${dontEvaluateGroupIds.size}] groupIDs: [" +
                dontEvaluateGroupIds.map { it.pattern }
                    .reduce { acc, s -> "$acc, $s" } + "]"
        }

        return "${this::class.java.simpleName} $ignoreDescription" +
            "\n[${permittedProjectTypes.size}] known project types: $partialDescription"
    }

    companion object {

        @JvmStatic
        internal fun prettyPrint(project: MavenProject): String =
            "GAV [${project.groupId}:${project.artifactId}:${project.packaging}]"
    }
}