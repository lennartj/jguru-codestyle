/*
 * Copyright (c) jGuru Europe AB.
 * All rights reserved.
 */

package se.jguru.codestyle.projects.enforcer

import org.apache.maven.enforcer.rule.api.EnforcerLogger
import org.apache.maven.enforcer.rule.api.EnforcerRuleException
import org.assertj.core.api.Assertions.assertThatExceptionOfType
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import se.jguru.codestyle.projects.MavenTestUtils
import se.jguru.codestyle.projects.enforcer.se.jguru.codestyle.projects.Slf4JDelegatingEnforcerLogger

/**
 *
 * @author [Lennart Jörelid](mailto:lj@jguru.se), jGuru Europe AB
 */
class PermittedProjectTypeRuleTest {

    private lateinit var enforcerLogger: EnforcerLogger

    @BeforeEach
    fun setupSharedState() {
        enforcerLogger = Slf4JDelegatingEnforcerLogger(log)
    }

    @Test
    fun validateCorrectPom() {

        // Assemble
        val unitUnderTest = PermittedProjectTypeRule().apply {
            log = enforcerLogger
            project = MavenTestUtils.readPom("testdata/poms/tools-parent.xml")
        }

        // Act & Assert
        unitUnderTest.execute()
    }

    @Test
    fun validateTestProjectTypePom() {

        // Assemble
        val unitUnderTest = PermittedProjectTypeRule().apply {
            log = enforcerLogger
            project = MavenTestUtils.readPom("testdata/poms/osgi-test-pom.xml")
        }

        // Act & Assert
        unitUnderTest.execute()
    }

    @Test
    fun validateAspectPom() {

        // Assemble
        val unitUnderTest = PermittedProjectTypeRule().apply {
            log = enforcerLogger
            project = MavenTestUtils.readPom("testdata/poms/aspect-project.xml")
        }

        // Act & Assert
        unitUnderTest.execute()
    }

    @Test
    fun validateExceptionOnParentPomWithModules() {

        // Assemble
        val unitUnderTest = PermittedProjectTypeRule().apply {
            log = enforcerLogger
            project = MavenTestUtils.readPom("testdata/poms/incorrect-parent-with-modules.xml")
        }

        // Act & Assert
        assertThatExceptionOfType(EnforcerRuleException::class.java).isThrownBy {
            unitUnderTest.execute()
        }
    }

    @Test
    fun validateExceptionOnBillOfMaterialsPomWithDependencies() {

        // Assemble
        val dontEvaluateGroupIdPatterns = listOf("^se\\.jguru\\..*\\.generated\\..*", "^se\\.jguru\\.codestyle\\..*")
        val unitUnderTest = PermittedProjectTypeRule(dontEvaluateGroupIdPatterns)
            .apply {
            log = enforcerLogger
            project = MavenTestUtils.readPom("testdata/poms/incorrect-bom-has-dependencies.xml")
        }


        // Act & Assert
        assertThatExceptionOfType(EnforcerRuleException::class.java).isThrownBy {
            unitUnderTest.execute()
        }
    }


    @Test
    fun validateNoExceptionOnBomHavingIgnoredDependencies() {

        // Assemble
        val dontEvaluateGroupIdPatterns = listOf("^se\\.jguru\\..*\\.generated\\..*", "^se\\.jguru\\.codestyle\\..*")
        val unitUnderTest = PermittedProjectTypeRule(dontEvaluateGroupIdPatterns)
            .apply {
                log = enforcerLogger
                project = MavenTestUtils.readPom("testdata/poms/correct-bom-with-ignored-dependencies.xml")
            }


        // Act & Assert
        unitUnderTest.execute()
    }

    companion object {

        @JvmStatic
        private val log: Logger = LoggerFactory.getLogger(PermittedProjectTypeRule::class.java)
    }
}