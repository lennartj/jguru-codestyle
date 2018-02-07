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
        Assert.assertTrue(acceptAllProjectType.isCompliantArtifactID(null))
        Assert.assertTrue(acceptAllProjectType.isCompliantGroupID(null))
        Assert.assertTrue(acceptAllProjectType.isCompliantPackaging(null))

        Assert.assertFalse(acceptNoneProjectType.isCompliantArtifactID(null))
        Assert.assertFalse(acceptNoneProjectType.isCompliantGroupID(null))
        Assert.assertFalse(acceptNoneProjectType.isCompliantPackaging(null))
    }

    @Test
    fun validateAcceptPatterns() {

        // Assemble
        val apiProjectType = DefaultProjectType(".*-api$", ".*\\.api$", "bundle|jar")

        // Act & Assert
        Assert.assertTrue(apiProjectType.isCompliantArtifactID("foo.api"))
        Assert.assertFalse(apiProjectType.isCompliantArtifactID("foo.api.bah"))
        Assert.assertFalse(apiProjectType.isCompliantArtifactID(null))

        Assert.assertTrue(apiProjectType.isCompliantGroupID("foo-api"))
        Assert.assertFalse(apiProjectType.isCompliantGroupID("foo-api-bah"))
        Assert.assertFalse(apiProjectType.isCompliantGroupID(null))

        Assert.assertTrue(apiProjectType.isCompliantPackaging("bundle"))
        Assert.assertTrue(apiProjectType.isCompliantPackaging("jar"))
        Assert.assertFalse(apiProjectType.isCompliantPackaging("war"))
        Assert.assertFalse(apiProjectType.isCompliantPackaging(null))
    }
}