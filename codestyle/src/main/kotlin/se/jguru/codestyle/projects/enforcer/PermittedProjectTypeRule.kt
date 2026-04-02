/*
 * Copyright (c) jGuru Europe AB.
 * All rights reserved.
 */

package se.jguru.codestyle.projects.enforcer

import org.apache.maven.project.MavenProject
import se.jguru.codestyle.projects.CommonProjectType
import se.jguru.codestyle.projects.DefaultProjectType.Companion.getDefaultRegexFor
import se.jguru.codestyle.projects.ProjectType
import javax.inject.Named

/**
 * Enforcer rule that validates a project's `groupId`, `artifactId`, and `packaging` against a
 * catalogue of permitted [ProjectType]s, and additionally checks internal structural constraints
 * such as module and dependency declarations.
 *
 * When no type matches, the rule emits a diagnostic listing the closest candidates ranked by
 * [se.jguru.codestyle.projects.ComplianceStatusHolder.complianceDistance].
 *
 * @param dontEvaluateGroupIds Patterns for groupIds that should bypass evaluation.
 * @param permittedProjectTypes The catalogue of types that a project may match.
 * Defaults to all [CommonProjectType] values.
 *
 * @see ProjectType
 * @author [Lennart Jörelid](mailto:lj@jguru.se), jGuru Europe AB
 */
@Suppress("LeakingThis")
@Named("validatePermittedProjectTypes")
open class PermittedProjectTypeRule(

    @Suppress("WeakerAccess")
    open var dontEvaluateGroupIds: List<Regex>,

    @Suppress("WeakerAccess")
    open var permittedProjectTypes: List<ProjectType>

) : AbstractNonCacheableEnforcerRule() {

    /** Creates the rule with no exclusions and the full [CommonProjectType] catalogue. */
    constructor() : this(emptyList(), CommonProjectType.entries)

    /** Creates the rule with the given exclusion pattern strings and the full [CommonProjectType] catalogue. */
    constructor(dontEvaluateGroupIdPatterns: List<String>) : this(
        dontEvaluateGroupIdPatterns.map { getDefaultRegexFor(it) },
        CommonProjectType.entries
    )

    // Pre-built for inclusion in toString().
    private val partialDescription = permittedProjectTypes
        .mapIndexed { index, current -> "\n[$index/${permittedProjectTypes.size}]: $current" }
        .joinToString("")

    override fun getShortRuleDescription(): String =
        "POM groupId, artifactId and packaging must comply with defined standard"

    override fun performValidation(project: MavenProject) {

        val firstMatch = permittedProjectTypes
            .firstOrNull { it.getComplianceStatus(project, dontEvaluateGroupIds).isCompliant }

        if (firstMatch == null) {
            // Rank all types by how closely they match and report the nearest ones.
            val distanceMap = permittedProjectTypes.associateWith { it.getComplianceStatus(project) }
            val minDistance = distanceMap.values.minOf { it.complianceDistance }
            val closestTypes = distanceMap
                .filterValues { it.complianceDistance == minDistance }
                .entries
                .joinToString("\n") { (pt, status) -> " [${pt.getIdentifier()}]: $status" }

            throw RuleFailureException(
                "None of the permitted ProjectTypes matched ${prettyPrint(project)}. " +
                    "\n Failure reasons per similar ProjectType:\n$closestTypes"
            )
        } else {
            if (log.isDebugEnabled) {
                log.debug("Found matching ProjectType [$firstMatch] for project ${prettyPrint(project)}")
            }
        }
    }

    override fun toString(): String {
        val ignoreDescription = if (dontEvaluateGroupIds.isEmpty()) {
            "ignoring no artifacts."
        } else {
            "ignoring artifacts matching [${dontEvaluateGroupIds.size}] groupIDs: [${
                dontEvaluateGroupIds.joinToString(", ") { it.pattern }
            }]"
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
