/*
 * Copyright (c) jGuru Europe AB.
 * All rights reserved.
 */

package se.jguru.codestyle.projects

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

/**
 *
 * @author [Lennart Jörelid](mailto:lj@jguru.se), jGuru Europe AB
 */
class ProjectTypeTest {

    @Test
    fun validateNullPatternAccept() {

        // Assemble
        val acceptAllProjectType = DefaultProjectType(acceptNullValues = true)
        val acceptNoneProjectType = DefaultProjectType(acceptNullValues = false)

        // Act & Assert
        assertThat(acceptAllProjectType.artifactIDNonComplianceMessage(null)).isNull()
        assertThat(acceptAllProjectType.groupIDNonComplianceMessage(null)).isNull()
        assertThat(acceptAllProjectType.packagingNonComplianceMessage(null)).isNull()

        assertThat(acceptNoneProjectType.artifactIDNonComplianceMessage(null)).isNotNull()
        assertThat(acceptNoneProjectType.groupIDNonComplianceMessage(null)).isNotNull()
        assertThat(acceptNoneProjectType.packagingNonComplianceMessage(null)).isNotNull()
    }

    @Test
    fun validateAcceptPatterns() {

        // Assemble
        val apiProjectType = DefaultProjectType(".*-api$", ".*\\.api$", "bundle|jar")

        // Act & Assert
        assertThat(apiProjectType.artifactIDNonComplianceMessage("foo.api")).isNull()
        assertThat(apiProjectType.artifactIDNonComplianceMessage("foo.api.bah")).isNotNull()
        assertThat(apiProjectType.artifactIDNonComplianceMessage(null)).isNotNull()

        assertThat(apiProjectType.groupIDNonComplianceMessage("foo-api")).isNull()
        assertThat(apiProjectType.groupIDNonComplianceMessage("foo-api-bah")).isNotNull()
        assertThat(apiProjectType.groupIDNonComplianceMessage(null)).isNotNull()

        assertThat(apiProjectType.packagingNonComplianceMessage("bundle")).isNull()
        assertThat(apiProjectType.packagingNonComplianceMessage("jar")).isNull()
        assertThat(apiProjectType.packagingNonComplianceMessage("war")).isNotNull()
        assertThat(apiProjectType.packagingNonComplianceMessage(null)).isNotNull()
    }
}