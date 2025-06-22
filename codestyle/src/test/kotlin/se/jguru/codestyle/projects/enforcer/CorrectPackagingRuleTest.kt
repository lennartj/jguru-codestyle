/*
 * Copyright (c) jGuru Europe AB.
 * All rights reserved.
 */

package se.jguru.codestyle.projects.enforcer

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatExceptionOfType
import org.assertj.core.api.Assertions.fail
import org.junit.jupiter.api.Test
import se.jguru.codestyle.projects.MavenTestUtils
import se.jguru.codestyle.projects.enforcer.CorrectPackagingRule.Companion.DEFAULT_IGNORED_FILENAMES
import se.jguru.codestyle.projects.enforcer.CorrectPackagingRule.Companion.isIgnored
import se.jguru.codestyle.projects.enforcer.CorrectPackagingRule.Companion.synthesizeRegExpsFor
import java.io.File

/**
 *
 * @author [Lennart Jörelid](mailto:lj@jguru.se), jGuru Europe AB
 */
class CorrectPackagingRuleTest {

    @Test
    fun validateIgnoringFilePatterns() {

        // Assemble
        val fileName2Expectancy = mapOf(
            "module-info.java" to true,
            "module-inf.java" to false,
            "module-" to false,
            "module-info.something.something" to true,
            "package-info.java" to true,
            "package-inf.java" to false,
            "package-info.something.something" to true,
        )
        val ignoredFilePatterns = synthesizeRegExpsFor(DEFAULT_IGNORED_FILENAMES)

        // Act & Assert
        fileName2Expectancy.forEach { fileName, expectedResult ->

            assertThat(ignoredFilePatterns.any { it.matches(fileName) }).isEqualTo(expectedResult)
            assertThat(isIgnored(File("", fileName), ignoredFilePatterns)).isEqualTo(expectedResult)
        }
    }

    @Test
    fun validateExceptionOnIncorrectSourceCodePackaging() {

        // Assemble
        val compileSourceRoot = CorrectPackagingRuleTest::class.java.classLoader.getResource(
            "testdata/project/incorrect/src/main/java")

        assertThat(compileSourceRoot)
            .withFailMessage("compileSourceRoot not found")
            .isNotNull

        val project = MavenTestUtils.readPom("testdata/project/incorrect/pom.xml")
        project.addCompileSourceRoot(compileSourceRoot?.path)

        val unitUnderTest = CorrectPackagingRule()
        unitUnderTest.project = project

        // Act & Assert
        try {

            unitUnderTest.performValidation(project)

            fail<Void>("CorrectPackagingRule should yield an exception for projects not " +
                "complying with packaging rules.")

        } catch (e: RuleFailureException) {

            val message = e.message ?: "<none>"

            // Validate that the message contains the package-->fileName data
            assertThat(message).contains("se.jguru.nazgul.tools.validation.api=[Validatable.java]")
        }
    }

    @Test
    fun validateExceptionOnIncorrectKotlinSourceCodePackaging() {

        // Assemble
        val prefix = "testdata/kotlin/incorrect"
        val compileSourceRoot = CorrectPackagingRuleTest::class.java.classLoader.getResource("$prefix/src/main/kotlin")
            ?: throw IllegalStateException("No test resource found.")

        assertThat(compileSourceRoot)
            .withFailMessage("compileSourceRoot not found")
            .isNotNull

        val project = MavenTestUtils.readPom("$prefix/pom.xml")
        project.addCompileSourceRoot(compileSourceRoot.path)

        val unitUnderTest = CorrectPackagingRule()
        unitUnderTest.project = project

        // Act & Assert
        try {

            unitUnderTest.performValidation(project)

            fail<Void>("CorrectPackagingRule should yield an exception for projects not " +
                "complying with packaging rules.")

        } catch (e: RuleFailureException) {

            val message = e.message ?: "<none>"

            // Should contain package-->fileName data
            assertThat(message).contains("se.jguru.nazgul.tools.validation.api=[Validatable.kt]")
        }
    }


    @Test
    fun validateExceptionOnCustomPackageExtractorDoesNotImplementPackageExtractor() {

        // Assemble
        val unitUnderTest = CorrectPackagingRule()

        // Act & Assert
        assertThatExceptionOfType(IllegalArgumentException::class.java).isThrownBy {
            unitUnderTest.setPackageExtractors(MockEnforcerRuleHelper::class.java.name)
        }
    }

    @Test
    fun validateExceptionOnCustomPackageExtractorHoldsNoDefaultConstructor() {

        // Assemble
        val unitUnderTest = CorrectPackagingRule()

        // Act & Assert
        assertThatExceptionOfType(IllegalArgumentException::class.java).isThrownBy {
            unitUnderTest.setPackageExtractors(IncorrectNoDefaultConstructorPackageExtractor::class.java.name)
        }
    }

    @Test
    fun validateAddingCustomPackageExtractor() {

        // Assemble
        val unitUnderTest = CorrectPackagingRule()

        // Act
        unitUnderTest.setPackageExtractors(SillyPackageExtractor::class.java.name + "," +
            "" + JavaPackageExtractor::class.java.name)

        // Assert
        val packageExtractors = unitUnderTest.javaClass.getDeclaredField("packageExtractors")
        packageExtractors.isAccessible = true

        @Suppress("UNCHECKED_CAST")
        val extractors = packageExtractors.get(unitUnderTest) as List<PackageExtractor>

        assertThat(extractors.size.toLong()).isEqualTo(2)
        assertThat(extractors[0].javaClass.name).isEqualTo(SillyPackageExtractor::class.java.name)
        assertThat(extractors[1].javaClass.name).isEqualTo(JavaPackageExtractor::class.java.name)

        assertThat(unitUnderTest.getShortRuleDescription()).isNotNull
    }

    @Test
    fun validateIgnoredFilesDoNotTriggerTheEnforcerRule() {

        // Assemble
        val prefix = "testdata/ignoredfiles"
        val compileSourceRoot = CorrectPackagingRuleTest::class.java.classLoader.getResource(prefix)
            ?: throw IllegalStateException("No test resource found.")

        assertThat(compileSourceRoot)
            .withFailMessage("compileSourceRoot not found")
            .isNotNull

        val project = MavenTestUtils.readPom("$prefix/pom.xml")
        project.addCompileSourceRoot(compileSourceRoot.path)

        val unitUnderTest = CorrectPackagingRule()
        unitUnderTest.project = project

        // Act & Assert
        try {

            unitUnderTest.performValidation(project)

            fail<Void>("CorrectPackagingRule should yield an exception for projects not " +
                           "complying with packaging rules.")

        } catch (e: RuleFailureException) {

            val message = e.message ?: "<none>"

            // Should contain package-->fileName data
            // Incorrect packaging detected; required [se.jguru.nazgul.tools.validation.aspect] but found package to file names: {=[module-info.java]}
            assertThat(message).contains("se.jguru.nazgul.tools.validation.api=[Validatable.java]")
        }
    }
}