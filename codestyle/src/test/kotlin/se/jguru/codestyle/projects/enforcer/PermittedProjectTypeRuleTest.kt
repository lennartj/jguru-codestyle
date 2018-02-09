/*
 * Copyright (c) jGuru Europe AB.
 * All rights reserved.
 */

package se.jguru.codestyle.projects.enforcer

import org.apache.maven.enforcer.rule.api.EnforcerRuleException
import org.junit.Test
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

    @Test(expected = EnforcerRuleException::class)
    fun validateExceptionOnParentPomWithModules() {

        // Assemble
        val project = MavenTestUtils.readPom("testdata/poms/incorrect-parent-with-modules.xml")
        val mockHelper = MockEnforcerRuleHelper(project)
        val unitUnderTest = PermittedProjectTypeRule()

        // Act & Assert
        unitUnderTest.execute(mockHelper)
    }
}