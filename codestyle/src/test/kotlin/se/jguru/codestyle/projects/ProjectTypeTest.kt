/*
 * Copyright (c) jGuru Europe AB.
 * All rights reserved.
 */

package se.jguru.codestyle.projects

import org.junit.Assert
import org.junit.Test

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
        Assert.assertNull(acceptAllProjectType.artifactIDNonComplianceMessage(null))
        Assert.assertNull(acceptAllProjectType.groupIDNonComplianceMessage(null))
        Assert.assertNull(acceptAllProjectType.packagingNonComplianceMessage(null))

        Assert.assertNotNull(acceptNoneProjectType.artifactIDNonComplianceMessage(null))
        Assert.assertNotNull(acceptNoneProjectType.groupIDNonComplianceMessage(null))
        Assert.assertNotNull(acceptNoneProjectType.packagingNonComplianceMessage(null))
    }

    @Test
    fun validateAcceptPatterns() {

        // Assemble
        val apiProjectType = DefaultProjectType(".*-api$", ".*\\.api$", "bundle|jar")

        // Act & Assert
        Assert.assertNull(apiProjectType.artifactIDNonComplianceMessage("foo.api"))
        Assert.assertNotNull(apiProjectType.artifactIDNonComplianceMessage("foo.api.bah"))
        Assert.assertNotNull(apiProjectType.artifactIDNonComplianceMessage(null))

        Assert.assertNull(apiProjectType.groupIDNonComplianceMessage("foo-api"))
        Assert.assertNotNull(apiProjectType.groupIDNonComplianceMessage("foo-api-bah"))
        Assert.assertNotNull(apiProjectType.groupIDNonComplianceMessage(null))

        Assert.assertNull(apiProjectType.packagingNonComplianceMessage("bundle"))
        Assert.assertNull(apiProjectType.packagingNonComplianceMessage("jar"))
        Assert.assertNotNull(apiProjectType.packagingNonComplianceMessage("war"))
        Assert.assertNotNull(apiProjectType.packagingNonComplianceMessage(null))
    }
}