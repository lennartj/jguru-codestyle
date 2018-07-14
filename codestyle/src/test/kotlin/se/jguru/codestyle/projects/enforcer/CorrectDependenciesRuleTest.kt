/*
 * Copyright (c) jGuru Europe AB.
 * All rights reserved.
 */

package se.jguru.codestyle.projects.enforcer

import org.apache.maven.enforcer.rule.api.EnforcerRuleException
import org.junit.Assert
import org.junit.Test
import se.jguru.codestyle.projects.MavenTestUtils

class CorrectDependenciesRuleTest {

    @Test(expected = EnforcerRuleException::class)
    fun validateIncorrectBomImportedAsDependencies() {

        // Assemble
        val project = MavenTestUtils.readPom("testdata/poms/incorrect-bom-as-dependency.xml")
        val mockHelper = MockEnforcerRuleHelper(project)
        val unitUnderTest = CorrectDependenciesRule()

        // Act & Assert
        unitUnderTest.execute(mockHelper)
    }

    @Test
    fun validateExceptionOnBomImportedInDependencyScope() {

        // Assemble
        val project = MavenTestUtils.readPom("testdata/poms/incorrect-bom-as-dependency.xml")
        val mockHelper = MockEnforcerRuleHelper(project)
        val unitUnderTest = CorrectDependenciesRule()

        // Act & Assert
        try {

            unitUnderTest.performValidation(project, mockHelper)

            Assert.fail("CorrectPackagingRule should yield an exception for projects not " +
                    "complying with packaging rules.")

        } catch (e: RuleFailureException) {

            val message = e.message ?: "<none>"

            // Validate that the message contains the correct failure reason.
            Assert.assertTrue(
                    message.contains("Don't use CommonProjectType.BILL_OF_MATERIALS dependencies in Dependency " +
                            "block. (Use only as DependencyManagement import-scoped dependencies)."))
        }
    }
}