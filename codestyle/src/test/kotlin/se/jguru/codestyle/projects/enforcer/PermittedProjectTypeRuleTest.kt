/*
 * Copyright (c) jGuru Europe AB.
 * All rights reserved.
 */

package se.jguru.codestyle.projects.enforcer

import org.apache.maven.enforcer.rule.api.EnforcerRuleException
import org.assertj.core.api.Assertions.assertThatExceptionOfType
import org.junit.jupiter.api.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import se.jguru.codestyle.projects.MavenTestUtils

/**
 *
 * @author [Lennart Jörelid](mailto:lj@jguru.se), jGuru Europe AB
 */
class PermittedProjectTypeRuleTest {

    @Test
    fun validateCorrectPom() {

        // Assemble
        val project = MavenTestUtils.readPom("testdata/poms/tools-parent.xml")
        val unitUnderTest = PermittedProjectTypeRule()
        unitUnderTest.project = project

        // Act & Assert
        unitUnderTest.execute()
    }

    @Test
    fun validateTestProjectTypePom() {

        // Assemble
        val project = MavenTestUtils.readPom("testdata/poms/osgi-test-pom.xml")
        val unitUnderTest = PermittedProjectTypeRule()
        unitUnderTest.project = project

        // Act & Assert
        unitUnderTest.execute()
    }

    @Test
    fun validateAspectPom() {

        // Assemble
        val project = MavenTestUtils.readPom("testdata/poms/aspect-project.xml")
        val unitUnderTest = PermittedProjectTypeRule()
        unitUnderTest.project = project

        // Act & Assert
        unitUnderTest.execute()
    }

    @Test
    fun validateExceptionOnParentPomWithModules() {

        // Assemble
        val project = MavenTestUtils.readPom("testdata/poms/incorrect-parent-with-modules.xml")
        val unitUnderTest = PermittedProjectTypeRule()
        unitUnderTest.project = project

        // Act & Assert
        assertThatExceptionOfType(EnforcerRuleException::class.java).isThrownBy {
            unitUnderTest.execute()
        }
    }

    @Test
    fun validateExceptionOnBillOfMaterialsPomWithDependencies() {

        // Assemble
        val project = MavenTestUtils.readPom("testdata/poms/incorrect-bom-has-dependencies.xml")
        val unitUnderTest = PermittedProjectTypeRule(
            listOf("^se\\.jguru\\..*\\.generated\\..*", "^se\\.jguru\\.codestyle\\..*")
        )
        unitUnderTest.project = project

        // Act & Assert
        assertThatExceptionOfType(EnforcerRuleException::class.java).isThrownBy {
            unitUnderTest.execute()
        }
    }


    @Test
    fun validateNoExceptionOnBomHavingIgnoredDependencies() {

        // Assemble
        val project = MavenTestUtils.readPom("testdata/poms/correct-bom-with-ignored-dependencies.xml")
        val unitUnderTest = PermittedProjectTypeRule(
            listOf("^se\\.jguru\\..*\\.generated\\..*", "^se\\.jguru\\.codestyle\\..*")
        )
        unitUnderTest.project = project

        // Act & Assert
        unitUnderTest.execute()
    }

    companion object {

        @JvmStatic
        private val log: Logger = LoggerFactory.getLogger(PermittedProjectTypeRule::class.java)
    }
}