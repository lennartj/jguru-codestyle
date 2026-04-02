/*
 * Copyright (c) jGuru Europe AB.
 * All rights reserved.
 */

package se.jguru.codestyle.projects

import org.apache.maven.artifact.Artifact
import org.apache.maven.model.Dependency
import org.apache.maven.project.MavenProject
import java.io.Serializable

/**
 * Specification for classifying Maven projects by their GAV coordinates and internal structure.
 *
 * Each compliance-check method returns `null` on success and a human-readable reason string on
 * failure, making it easy to collect and report all violations at once.
 *
 * All implementations must provide a meaningful [toString] so that enforcer rule failure messages
 * (e.g. from [se.jguru.codestyle.projects.enforcer.PermittedProjectTypeRule]) are readable.
 *
 * @author [Lennart Jörelid](mailto:lj@jguru.se), jGuru Europe AB
 */
interface ProjectType : Serializable {

    /**
     * Returns an identifier unique to this [ProjectType]. Defaults to the simple class name.
     */
    fun getIdentifier(): String = this::class.java.simpleName

    /**
     * Returns `null` if [artifactID] complies with this [ProjectType]'s naming convention,
     * or a human-readable non-compliance reason otherwise.
     */
    fun artifactIDNonComplianceMessage(artifactID: String?): String?

    /**
     * Returns `null` if [groupID] complies with this [ProjectType]'s naming convention,
     * or a human-readable non-compliance reason otherwise.
     */
    fun groupIDNonComplianceMessage(groupID: String?): String?

    /**
     * Returns `null` if [packaging] complies with this [ProjectType]'s requirements,
     * or a human-readable non-compliance reason otherwise.
     */
    fun packagingNonComplianceMessage(packaging: String?): String?

    /**
     * Returns `null` if [project]'s internal structure complies with this [ProjectType]'s requirements,
     * or a human-readable non-compliance reason otherwise.
     *
     * @param dontEvaluateGroupIds Optional list of [Regex] patterns; artifacts whose groupId matches
     * any pattern are excluded from evaluation.
     */
    fun internalStructureNonComplianceMessage(
        project: MavenProject?,
        dontEvaluateGroupIds: List<Regex>? = null
    ): String?

    /**
     * Checks whether [project] fully complies with this [ProjectType] and returns a
     * [ComplianceStatusHolder] summarising the result.
     *
     * Projects whose groupId matches any pattern in [dontEvaluateGroupIds] are considered
     * immediately compliant and bypass all checks.
     */
    fun getComplianceStatus(project: MavenProject, dontEvaluateGroupIds: List<Regex>? = null): ComplianceStatusHolder {

        // Bypass evaluation for excluded groupIds.
        if (dontEvaluateGroupIds != null && dontEvaluateGroupIds.any { it.matches(project.groupId) }) {
            return ComplianceStatusHolder()
        }

        return ComplianceStatusHolder(
            groupComplianceFailure = groupIDNonComplianceMessage(project.groupId),
            artifactComplianceFailure = artifactIDNonComplianceMessage(project.artifactId),
            packagingComplianceFailure = packagingNonComplianceMessage(project.packaging),
            internalStructureComplianceFailure = internalStructureNonComplianceMessage(project, dontEvaluateGroupIds)
        )
    }

    companion object {

        private fun representation(
            groupId: String?,
            artifactId: String?,
            version: String?,
            type: String?,
            classifier: String?
        ): String {
            val base = "${groupId ?: ""}:${artifactId ?: ""}:${version ?: ""}:${type ?: ""}"
            return if (classifier != null) "$base:$classifier" else base
        }

        /**
         * Compares two [Artifact]s by their canonical string representation.
         */
        @JvmStatic
        val ARTIFACT_COMPARATOR: Comparator<Artifact> = Comparator { l, r ->
            representation(l.groupId, l.artifactId, l.version, l.type, l.classifier)
                .compareTo(representation(r.groupId, r.artifactId, r.version, r.type, r.classifier))
        }

        /**
         * Compares two [Dependency]s by their canonical string representation.
         */
        @JvmStatic
        val DEPENDENCY_COMPARATOR: Comparator<Dependency> = Comparator { l, r ->
            representation(l.groupId, l.artifactId, l.version, l.type, l.classifier)
                .compareTo(representation(r.groupId, r.artifactId, r.version, r.type, r.classifier))
        }
    }
}

/**
 * Default [ProjectType] implementation that validates groupId, artifactId, and packaging against
 * [Regex] patterns, and delegates internal-structure checks to a configurable lambda.
 *
 * All patterns are compiled with [IGNORE_CASE_AND_COMMENTS] unless constructed directly from
 * [Regex] objects.
 *
 * @param groupIdRegex [Regex] matching valid groupIds for this type.
 * @param artifactIdRegex [Regex] matching valid artifactIds for this type.
 * @param packagingRegex [Regex] matching valid packaging values for this type.
 * @param acceptNullValues If `true`, `null` inputs are treated as compliant; if `false` they fail.
 * @param id Optional stable identifier; falls back to the simple class name when `null`.
 * @param structureChecker Lambda receiving a [MavenProject] and optional exclusion patterns,
 * returning a non-compliance reason or `null` when the project is compliant.
 */
open class DefaultProjectType @JvmOverloads constructor(

    protected val groupIdRegex: Regex,

    protected val artifactIdRegex: Regex,

    protected val packagingRegex: Regex,

    protected val acceptNullValues: Boolean = false,

    private val id: String? = null,

    protected open val structureChecker: (MavenProject, List<Regex>?) -> String?
) : ProjectType {

    /**
     * Convenience constructor accepting nullable pattern strings instead of [Regex] objects.
     * A `null` pattern compiles to `.*` (match-all).
     *
     * @see getDefaultRegexFor
     * @see IGNORE_CASE_AND_COMMENTS
     */
    constructor(
        groupIdPattern: String? = null,
        artifactIdPattern: String? = null,
        packagingPattern: String? = null,
        acceptNullValues: Boolean = false,
        id: String? = null,
        structureChecker: (MavenProject, List<Regex>?) -> String? = { _, _ -> null }
    ) : this(
        getDefaultRegexFor(groupIdPattern),
        getDefaultRegexFor(artifactIdPattern),
        getDefaultRegexFor(packagingPattern),
        acceptNullValues,
        id,
        structureChecker
    )

    override fun artifactIDNonComplianceMessage(artifactID: String?): String? = when {
        artifactID == null -> if (acceptNullValues) null else "Got null artifactID. Expected: non-null."
        artifactIdRegex.matches(artifactID) -> null
        else -> "Incorrect artifactId [$artifactID]. Expected: matching pattern [$artifactIdRegex]."
    }

    override fun groupIDNonComplianceMessage(groupID: String?): String? = when {
        groupID == null -> if (acceptNullValues) null else "Got null groupID. Expected: non-null."
        groupIdRegex.matches(groupID) -> null
        else -> "Incorrect GroupId [$groupID]. Expected: matching pattern [$groupIdRegex]."
    }

    override fun packagingNonComplianceMessage(packaging: String?): String? = when {
        packaging == null -> if (acceptNullValues) null else "Got null packaging. Expected: non-null."
        packagingRegex.matches(packaging) -> null
        else -> "Incorrect packaging [$packaging]. Expected: matching pattern [$packagingRegex]."
    }

    override fun internalStructureNonComplianceMessage(
        project: MavenProject?,
        dontEvaluateGroupIds: List<Regex>?
    ): String? = when (project) {
        null -> if (acceptNullValues) null else "Got null MavenProject. Expected: non-null."
        else -> structureChecker(project, dontEvaluateGroupIds)
    }

    override fun toString(): String =
        "[ProjectType: ${javaClass.name}] - GroupIdRegex: ${groupIdRegex.pattern}, " +
            "ArtifactIdRegex: ${artifactIdRegex.pattern}, " +
            "PackagingRegex: ${packagingRegex.pattern}"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is DefaultProjectType) return false
        return groupIdRegex == other.groupIdRegex &&
            artifactIdRegex == other.artifactIdRegex &&
            packagingRegex == other.packagingRegex &&
            acceptNullValues == other.acceptNullValues
    }

    override fun hashCode(): Int {
        var result = groupIdRegex.hashCode()
        result = 31 * result + artifactIdRegex.hashCode()
        result = 31 * result + packagingRegex.hashCode()
        result = 31 * result + acceptNullValues.hashCode()
        return result
    }

    override fun getIdentifier(): String = id ?: this::class.java.simpleName

    companion object {

        /**
         * [RegexOption] set that ignores case and allows inline comments in patterns.
         */
        val IGNORE_CASE_AND_COMMENTS = setOf(RegexOption.COMMENTS, RegexOption.IGNORE_CASE)

        /**
         * Compiles [pattern] (or `".*"` when `null`) into a [Regex] using [IGNORE_CASE_AND_COMMENTS].
         */
        @JvmStatic
        fun getDefaultRegexFor(pattern: String?): Regex = Regex(pattern ?: ".*", IGNORE_CASE_AND_COMMENTS)
    }
}
