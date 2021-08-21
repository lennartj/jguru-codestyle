/*
 * Copyright (c) jGuru Europe AB.
 * All rights reserved.
 */

package se.jguru.codestyle.projects.enforcer

import org.apache.maven.enforcer.rule.api.EnforcerRuleException
import org.assertj.core.api.Assertions.assertThatExceptionOfType
import org.junit.jupiter.api.Test
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
        val mockHelper = MockEnforcerRuleHelper(project)
        val unitUnderTest = PermittedProjectTypeRule()

        // Act & Assert
        unitUnderTest.execute(mockHelper)
    }

    @Test
    fun validateTestProjectTypePom() {

        // Assemble
        val project = MavenTestUtils.readPom("testdata/poms/osgi-test-pom.xml")
        val mockHelper = MockEnforcerRuleHelper(project)
        val unitUnderTest = PermittedProjectTypeRule()

        // Act & Assert
        unitUnderTest.execute(mockHelper)
    }

    @Test
    fun validateAspectPom() {

        // Assemble
        val project = MavenTestUtils.readPom("testdata/poms/aspect-project.xml")
        val mockHelper = MockEnforcerRuleHelper(project)
        val unitUnderTest = PermittedProjectTypeRule()

        // Act & Assert
        unitUnderTest.execute(mockHelper)
    }

    @Test
    fun validateExceptionOnParentPomWithModules() {

        // Assemble
        val project = MavenTestUtils.readPom("testdata/poms/incorrect-parent-with-modules.xml")
        val mockHelper = MockEnforcerRuleHelper(project)
        val unitUnderTest = PermittedProjectTypeRule()

        // Act & Assert
        assertThatExceptionOfType(EnforcerRuleException::class.java).isThrownBy {
            unitUnderTest.execute(mockHelper)
        }
    }

    @Test
    fun validateExceptionOnBillOfMaterialsPomWithDependencies() {

        // Assemble
        val project = MavenTestUtils.readPom("testdata/poms/incorrect-bom-has-dependencies.xml")
        val mockHelper = MockEnforcerRuleHelper(project)
        val unitUnderTest = PermittedProjectTypeRule(
            listOf("^se\\.jguru\\..*\\.generated\\..*", "^se\\.jguru\\.codestyle\\..*")
        )

        // Act & Assert
        assertThatExceptionOfType(EnforcerRuleException::class.java).isThrownBy {
            unitUnderTest.execute(mockHelper)
        }
    }


    @Test
    fun validateNoExceptionOnBomHavingIgnoredDependencies() {

        // Assemble
        val project = MavenTestUtils.readPom("testdata/poms/correct-bom-with-ignored-dependencies.xml")
        val mockHelper = MockEnforcerRuleHelper(project)
        val unitUnderTest = PermittedProjectTypeRule(
            listOf("^se\\.jguru\\..*\\.generated\\..*", "^se\\.jguru\\.codestyle\\..*")
        )

        // Act & Assert
        unitUnderTest.execute(mockHelper)
    }
}