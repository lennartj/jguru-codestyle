/*
 * Copyright (c) jGuru Europe AB.
 * All rights reserved.
 */

package se.jguru.codestyle.projects.enforcer

import org.apache.maven.enforcer.rule.api.EnforcerRuleException
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatExceptionOfType
import org.assertj.core.api.Assertions.fail
import org.junit.jupiter.api.Test
import se.jguru.codestyle.projects.MavenTestUtils

class CorrectDependenciesRuleTest {

    @Test
    fun validateIncorrectBomImportedAsDependencies() {

        // Assemble
        val project = MavenTestUtils.readPom("testdata/poms/incorrect-bom-as-dependency.xml")
        val unitUnderTest = CorrectDependenciesRule()
        unitUnderTest.project = project

        // Act & Assert
        assertThatExceptionOfType(EnforcerRuleException::class.java).isThrownBy {
            unitUnderTest.execute()
        }
    }

    @Test
    fun validateExceptionMessageForBomImportedInDependencyScope() {

        // Assemble
        val project = MavenTestUtils.readPom("testdata/poms/incorrect-bom-as-dependency.xml")
        val unitUnderTest = CorrectDependenciesRule()
        unitUnderTest.project = project

        // Act & Assert
        try {

            unitUnderTest.performValidation(project)

            fail<Void>("CorrectPackagingRule should yield an exception for projects " +
                                           "not complying with packaging rules.")

        } catch (e: RuleFailureException) {

            val message = e.message ?: "<none>"

            // Validate that the message contains the correct failure reason.
            assertThat(message).contains("Don't use CommonProjectType.BILL_OF_MATERIALS dependencies in Dependency " +
                    "block. (Use only as DependencyManagement import-scoped dependencies).")
        }
    }
}