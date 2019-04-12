/*
 * Copyright (c) jGuru Europe AB.
 * All rights reserved.
 */

package se.jguru.codestyle.projects

import java.io.Serializable

/**
 * Compliance status structure and message holder.
 *
 * @author [Lennart J&ouml;relid](mailto:lj@jguru.se), jGuru Europe AB
 */
open class ComplianceStatusHolder @JvmOverloads constructor(
    var groupComplianceFailure: String? = null,
    var artifactComplianceFailure: String? = null,
    var packagingComplianceFailure: String? = null,
    var internalStructureComplianceFailure: String? = null) : Serializable {

    /**
     * Synthetic property indicating if this ComplianceStatus implies
     * adherence to/compliance with all rules found.
     */
    val isCompliant: Boolean
        get() = groupComplianceFailure == null &&
            artifactComplianceFailure == null &&
            packagingComplianceFailure == null &&
            internalStructureComplianceFailure == null

    /**
     * Synthetic property indicating the distance to adherence to/compliance with all rules.
     */
    val complianceDistance: Int
        get() {
            var toReturn = 0

            listOf(groupComplianceFailure,
                artifactComplianceFailure,
                packagingComplianceFailure).forEach { if (it != null) toReturn += 2 }

            if(internalStructureComplianceFailure != null) {
                toReturn += 1
            }

            // All Done.
            return toReturn
        }

    override fun toString(): String = when (isCompliant) {
        true -> "Fully Compliant"
        else -> "[$complianceDistance] differences: " +
            mapOf(Pair("GroupId", groupComplianceFailure),
                Pair("ArtifactId", artifactComplianceFailure),
                Pair("Packaging", packagingComplianceFailure),
                Pair("Internal structure", internalStructureComplianceFailure))
                .filter { it.value != null }
                .map { "${it.key} ${it.value}" }
                .joinToString(", ")
    }

    companion object {

        /**
         * A ComplianceStatus indicating that all is OK.
         */
        @JvmStatic
        val OK = ComplianceStatusHolder()
    }
}