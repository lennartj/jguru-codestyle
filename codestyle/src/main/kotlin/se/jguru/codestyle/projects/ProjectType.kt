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
 * Specification for how to classify Maven projects originating from their GAV.
 * All implementations must supply a `toString()` method to supply usable debug logs
 * from some of the enforcer rules, such as [se.jguru.codestyle.projects.enforcer.PermittedProjectTypeRule].
 *
 * @author [Lennart Jörelid](mailto:lj@jguru.se), jGuru Europe AB
 */
interface ProjectType : Serializable {

    /**
     * @return an identifier unique to this ProjectType.
     */
    fun getIdentifier(): String = this::class.java.simpleName

    /**
     * Checks if the provided artifactID complies with the standard for this ProjectType.
     *
     * @param artifactID The artifactID which should be checked for compliance.
     * @return `null` if the supplied artifactID value was compliant with this ProjectType's requirements,
     * and a reason message for non-compliance if not.
     */
    fun artifactIDNonComplianceMessage(artifactID: String?): String?

    /**
     * Checks if the provided groupID complies with the naming standard
     * for this ProjectType.
     *
     * @param groupID The groupID which should be checked for compliance.
     * @return `null` if the supplied groupID value was compliant with this ProjectType's requirements,
     * and a reason message for non-compliance if not.
     */
    fun groupIDNonComplianceMessage(groupID: String?): String?

    /**
     * Checks if the provided packaging complies with the standard/requirements of this ProjectType.
     *
     * @param packaging The packaging which should be checked for compliance.
     * @return @return `null` if the supplied packaging value was compliant with this ProjectType's
     * requirements, and a reason message for non-compliance if not.
     */
    fun packagingNonComplianceMessage(packaging: String?): String?

    /**
     * Checks if the provided MavenProject complies with the standard/requirements of this ProjectType.
     *
     * @param project The MavenProject which should be checked for compliance.
     * @param dontEvaluateGroupIds an optional list of Regexs used to exclude evaluation from artifacts whose groupIDs
     * match any of the supplied Regexes.
     * @return `null` if the supplied MavenProject's internal structure was compliant with this ProjectType's
     * requirements, and a reason message for non-compliance if not.
     */
    fun internalStructureNonComplianceMessage(project: MavenProject?,
                                              dontEvaluateGroupIds : List<Regex>? = null): String?

    /**
     * Convenience implementation used to test whether or not a [org.apache.maven.project.MavenProject] is
     * compliant with this [ProjectType]. Override to provide extra mechanics for validation.
     *
     * @param project The MavenProject to ascertain compliance for this ProjectType.
     * @param dontEvaluateGroupIds an optional list of Regexs used to exclude evaluation from artifacts
     * whose groupIDs match any of the supplied Regexes.
     */
    fun getComplianceStatus(project: MavenProject, dontEvaluateGroupIds : List<Regex>? = null): ComplianceStatusHolder {

        val toReturn = ComplianceStatusHolder()

        // Check sanity: Should we evaluate this project.
        if(dontEvaluateGroupIds != null && dontEvaluateGroupIds.any { it.matches(project.groupId) }) {
            return toReturn
        }

        toReturn.groupComplianceFailure = groupIDNonComplianceMessage(project.groupId)
        toReturn.artifactComplianceFailure = artifactIDNonComplianceMessage(project.artifactId)
        toReturn.packagingComplianceFailure = packagingNonComplianceMessage(project.packaging)
        toReturn.internalStructureComplianceFailure = internalStructureNonComplianceMessage(project, dontEvaluateGroupIds)

        // All Done
        return toReturn
    }

    companion object {
        
        private fun representation(groupId: String?,
                                   artifactId: String?,
                                   version: String?,
                                   type: String?,
                                   classifier: String?): String {

            fun safeGet(aString: String?): String = aString ?: ""

            return safeGet(groupId) + ":" +
                safeGet(artifactId) + ":" +
                safeGet(version) + ":" +
                safeGet(type) + when (classifier) {
                null -> ""
                else -> ":${safeGet(classifier)}"
            }
        }

        /**
         * Compares two Artifacts by their string representation.
         */
        @JvmStatic
        val ARTIFACT_COMPARATOR: Comparator<Artifact> = Comparator { l, r ->
            representation(l.groupId, l.artifactId, l.version, l.type, l.classifier)
                .compareTo(representation(r.groupId, r.artifactId, r.version, r.type, r.classifier))
        }

        /**
         * Compares two Dependencies by their string representation.
         */
        @JvmStatic
        val DEPENDENCY_COMPARATOR: Comparator<Dependency> = Comparator { l, r ->
            representation(l.groupId, l.artifactId, l.version, l.type, l.classifier)
                .compareTo(representation(r.groupId, r.artifactId, r.version, r.type, r.classifier))
        }
    }
}

/**
 * Default ProjectType implementation which uses a [Regex] to determine if the groupID, artifactID
 * and packaging matches required presets.
 *
 * @param groupIdRegex The [Regex] to identify matching Aether GroupIDs for this [ProjectType]
 * @param artifactIdRegex The [Regex] to identify matching Aether ArtifactIDs for this [ProjectType]
 * @param packagingRegex The [Regex] to identify matching Aether packaging for this [ProjectType]
 * @param acceptNullValues Indicates if received [null]s should be accepted or rejected.
 * @param id an optional ID to assign to this DefaultProjectType.
 */
open class DefaultProjectType @JvmOverloads constructor(

    protected val groupIdRegex: Regex,

    protected val artifactIdRegex: Regex,

    protected val packagingRegex: Regex,

    protected val acceptNullValues: Boolean = false,

    private val id: String? = null,

    protected val structureChecker: (MavenProject, List<Regex>?) -> String?) : ProjectType {

    /**
     * Convenience constructor using the pure String [Pattern]s instead of the full [Regex] objects.
     *
     * @see #getDefaultRegexFor
     * @see #IGNORE_CASE_AND_COMMENTS
     */
    constructor(groupIdPattern: String? = null,
                artifactIdPattern: String? = null,
                packagingPattern: String? = null,
                acceptNullValues: Boolean = false,
                id: String? = null,
                structureChecker: (MavenProject, List<Regex>?) -> String? = { _, _ -> null }) : this(
        getDefaultRegexFor(groupIdPattern),
        getDefaultRegexFor(artifactIdPattern),
        getDefaultRegexFor(packagingPattern),
        acceptNullValues,
        id,
        structureChecker)

    /**
     * Default implementation validates that the received [artifactID] matches the [artifactIdRegex]
     * or - if a null artifactID is given - returns a value corresponding to the [acceptNullValues]
     * constructor argument.
     *
     * @see ProjectType#isCompliantArtifactID
     */
    override fun artifactIDNonComplianceMessage(artifactID: String?): String? = when (artifactID) {

        null -> when (acceptNullValues) {
            true -> null
            else -> "Got null artifactID. Expected: non-null."
        }

        else -> when (artifactIdRegex.matches(artifactID)) {
            true -> null
            else -> "Incorrect artifactId [$artifactID]. Expected: matching pattern [$artifactIdRegex]."
        }
    }

    /**
     * Default implementation validates that the received `groupID` matches the groupIdRegex
     * or - if null - returns a value corresponding to the `acceptNullValues` constructor argument.
     *
     * @see [ProjectType.groupIDNonComplianceMessage]
     */
    override fun groupIDNonComplianceMessage(groupID: String?): String? = when (groupID) {

        null -> when (acceptNullValues) {
            true -> null
            else -> "Got null groupID. Expected: non-null."
        }

        else -> when (groupIdRegex.matches(groupID)) {
            true -> null
            else -> "Incorrect GroupId [$groupID]. Expected: matching pattern [$groupIdRegex]."
        }
    }

    /**
     * Default implementation validates that the received `packaging` matches the `packagingRegex`
     * or - if null - returns a value corresponding to the `acceptNullValues` constructor argument.
     *
     * @see [ProjectType.packagingNonComplianceMessage]
     */
    override fun packagingNonComplianceMessage(packaging: String?): String? = when (packaging) {

        null -> when (acceptNullValues) {
            true -> null
            else -> "Got null packaging. Expected: non-null."
        }

        else -> when (packagingRegex.matches(packaging)) {
            true -> null
            else -> "Incorrect packaging [$packaging]. Expected: matching pattern [$packagingRegex]."
        }
    }

    /**
     * Default implementation validates that the received `project` matches the criteria of the structureChecker
     * lambda or - if null - returns a value corresponding to the `acceptNullValues` constructor argument.
     *
     * @see [ProjectType.packagingNonComplianceMessage]
     */
    override fun internalStructureNonComplianceMessage(project: MavenProject?,
                                                       dontEvaluateGroupIds: List<Regex>?): String? = when (project) {

        null -> when (acceptNullValues) {
            true -> null
            else -> "Got null MavenProject. Expected: non-null."
        }

        else -> structureChecker.invoke(project, dontEvaluateGroupIds)
    }

    /**
     * Packages a representation for this ProjectType, including its regular expressions.
     *
     * @return A human-readable debug string for this ProjectType.
     */
    override fun toString(): String {
        return "[ProjectType: ${javaClass.name}] - GroupIdRegex: ${groupIdRegex.pattern}, " +
            "ArtifactIdRegex: ${artifactIdRegex.pattern}, " +
            "PackagingRegex: ${packagingRegex.pattern}"
    }

    /**
     * Validates equality by comparing the [Regex] members of both [DefaultProjectType] objects.
     */
    override fun equals(other: Any?): Boolean {

        // Fail fast
        if (this === other) return true
        if (other !is DefaultProjectType) return false

        // Delegate to internal state
        if (groupIdRegex != other.groupIdRegex) return false
        if (artifactIdRegex != other.artifactIdRegex) return false
        if (packagingRegex != other.packagingRegex) return false
        if (acceptNullValues != other.acceptNullValues) return false

        return true
    }

    /**
     * Simply delegates the hashCode to the internal [Regex] objects and the [acceptNullValues].
     */
    override fun hashCode(): Int {
        var result = groupIdRegex.hashCode()
        result = 31 * result + artifactIdRegex.hashCode()
        result = 31 * result + packagingRegex.hashCode()
        result = 31 * result + acceptNullValues.hashCode()
        return result
    }

    override fun getIdentifier(): String = when (id != null) {
        true -> id
        else -> this::class.java.simpleName
    }

    companion object {

        /**
         * The set of [RegexOption]s permitting comments and ignoring case.
         */
        val IGNORE_CASE_AND_COMMENTS = setOf(RegexOption.COMMENTS, RegexOption.IGNORE_CASE)

        /**
         * Convenience function to create a [Regex] from the supplied pattern and using
         * [IGNORE_CASE_AND_COMMENTS] for options.
         */
        @JvmStatic
        fun getDefaultRegexFor(pattern: String?): Regex = Regex(pattern ?: ".*", IGNORE_CASE_AND_COMMENTS)
    }
}