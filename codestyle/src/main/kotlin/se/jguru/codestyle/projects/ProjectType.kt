/*
 * Copyright (c) jGuru Europe AB.
 * All rights reserved.
 */

package se.jguru.codestyle.projects

import java.io.Serializable

/**
 * Specification for how to classify Maven projects originating from their GAV.
 *
 * @author <a href="mailto:lj@jguru.se">Lennart J&ouml;relid</a>, jGuru Europe AB
 */
interface ProjectType : Serializable {

    /**
     * Checks if the provided artifactID complies with the naming standard
     * for this ProjectType.
     *
     * @param artifactID The artifactID which should be checked for compliance.
     * @return `true` if the provided artifactID was compliant with the naming rules for this ProjectType.
     */
    fun isCompliantArtifactID(artifactID: String?): Boolean

    /**
     * Checks if the provided groupID complies with the naming standard
     * for this ProjectType.
     *
     * @param groupID The groupID which should be checked for compliance.
     * @return `true` if the provided groupID was compliant with the naming rules for this ProjectType.
     */
    fun isCompliantGroupID(groupID: String?): Boolean

    /**
     * Checks if the provided packaging complies with the standard/requirements
     * of this ProjectType.
     *
     * @param packaging The packaging which should be checked for compliance.
     * @return `true` if the provided packaging was compliant with the rules for this ProjectType.
     */
    fun isCompliantPackaging(packaging: String?): Boolean
}

/**
 * Default ProjectType implementation which uses a [Regex] to determine if the groupID, artifactID
 * and packaging matches required presets.
 */
open class DefaultProjectType(

        /**
         * The [Regex] to identify matching Aether GroupIDs for this [ProjectType]
         */
        protected val groupIdRegex: Regex,

        /**
         * The [Regex] to identify matching Aether ArtifactIDs for this [ProjectType]
         */
        protected val artifactIdRegex: Regex,

        /**
         * The [Regex] to identify matching Aether packaging for this [ProjectType]
         */
        protected val packagingRegex: Regex,

        /**
         * Indicates if received [null]s should be accepted or rejected.
         */
        protected val acceptNullValues: Boolean = false) : ProjectType {

    /**
     * Convenience constructor using the pure String [Pattern]s instead of the full [Regex] objects.
     *
     * @see #getDefaultRegexFor
     * @see #IGNORE_CASE_AND_COMMENTS
     */
    constructor(groupIdPattern: String? = null,
                artifactIdPattern: String? = null,
                packagingPattern: String? = null,
                acceptNullValues: Boolean = false) : this(
            getDefaultRegexFor(groupIdPattern),
            getDefaultRegexFor(artifactIdPattern),
            getDefaultRegexFor(packagingPattern),
            acceptNullValues)

    /**
     * Default implementation validates that the received [artifactID] matches the [artifactIdRegex]
     * or - if null - returns a value corresponding to the [acceptNullValues] constructor argument.
     *
     * @see ProjectType#isCompliantArtifactID
     */
    override fun isCompliantArtifactID(artifactID: String?): Boolean = when (artifactID) {
        null -> acceptNullValues
        else -> artifactIdRegex.matches(artifactID)
    }

    /**
     * Default implementation validates that the received [groupID] matches the [groupIdRegex]
     * or - if null - returns a value corresponding to the [acceptNullValues] constructor argument.
     *
     * @see ProjectType#isCompliantGroupID
     */
    override fun isCompliantGroupID(groupID: String?): Boolean = when (groupID) {
        null -> acceptNullValues
        else -> groupIdRegex.matches(groupID)
    }

    /**
     * Default implementation validates that the received [packaging] matches the [packagingRegex]
     * or - if null - returns a value corresponding to the [acceptNullValues] constructor argument.
     *
     * @see ProjectType#isCompliantPackaging
     */
    override fun isCompliantPackaging(packaging: String?): Boolean = when (packaging) {
        null -> acceptNullValues
        else -> packagingRegex.matches(packaging)
    }

    /**
     * Packages a representation for this ProjectType, including its regular expressions.
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

    override fun hashCode(): Int {
        var result = groupIdRegex.hashCode()
        result = 31 * result + artifactIdRegex.hashCode()
        result = 31 * result + packagingRegex.hashCode()
        result = 31 * result + acceptNullValues.hashCode()
        return result
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
        fun getDefaultRegexFor(pattern: String?): Regex = Regex(pattern ?: ".*", IGNORE_CASE_AND_COMMENTS)
    }


}