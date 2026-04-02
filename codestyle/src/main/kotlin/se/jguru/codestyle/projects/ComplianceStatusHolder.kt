/*
 * Copyright (c) jGuru Europe AB.
 * All rights reserved.
 */

package se.jguru.codestyle.projects

import java.io.Serializable

/**
 * Holds the compliance status of a Maven project against a [ProjectType], tracking failures
 * across four independent dimensions: groupId, artifactId, packaging, and internal structure.
 *
 * Each non-null failure field contributes to [complianceDistance], which ranks how closely a
 * project matches a given type even when it is not fully compliant. GAV failures (group, artifact,
 * packaging) each add 2 to the distance; an internal-structure failure adds 1.
 *
 * @param groupComplianceFailure Non-null if the project's groupId is non-compliant, holding the reason.
 * @param artifactComplianceFailure Non-null if the project's artifactId is non-compliant, holding the reason.
 * @param packagingComplianceFailure Non-null if the project's packaging is non-compliant, holding the reason.
 * @param internalStructureComplianceFailure Non-null if the project's internal structure is non-compliant,
 * holding the reason.
 *
 * @author [Lennart Jörelid](mailto:lj@jguru.se), jGuru Europe AB
 */
open class ComplianceStatusHolder @JvmOverloads constructor(
    var groupComplianceFailure: String? = null,
    var artifactComplianceFailure: String? = null,
    var packagingComplianceFailure: String? = null,
    var internalStructureComplianceFailure: String? = null
) : Serializable {

    /**
     * `true` if all compliance dimensions pass (all failure fields are `null`).
     */
    val isCompliant: Boolean
        get() = groupComplianceFailure == null &&
            artifactComplianceFailure == null &&
            packagingComplianceFailure == null &&
            internalStructureComplianceFailure == null

    /**
     * Numeric distance from full compliance. A value of `0` means fully compliant.
     * GAV failures (group, artifact, packaging) each contribute 2; an internal-structure
     * failure contributes 1, reflecting that structural issues are secondary to naming mismatches.
     */
    val complianceDistance: Int
        get() = listOf(groupComplianceFailure, artifactComplianceFailure, packagingComplianceFailure)
            .count { it != null } * 2 +
            if (internalStructureComplianceFailure != null) 1 else 0

    override fun toString(): String = when {
        isCompliant -> "Fully Compliant"
        else -> "[$complianceDistance] differences: " + mapOf(
            "GroupId" to groupComplianceFailure,
            "ArtifactId" to artifactComplianceFailure,
            "Packaging" to packagingComplianceFailure,
            "Internal structure" to internalStructureComplianceFailure
        ).filterValues { it != null }
            .entries.joinToString(", ") { "${it.key} ${it.value}" }
    }

    companion object {

        /**
         * Singleton representing a fully compliant status with no failures.
         */
        @JvmStatic
        val OK = ComplianceStatusHolder()
    }
}
