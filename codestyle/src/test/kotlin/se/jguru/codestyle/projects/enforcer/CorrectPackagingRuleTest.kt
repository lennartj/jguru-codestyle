/*
 * Copyright (c) jGuru Europe AB.
 * All rights reserved.
 */

package se.jguru.codestyle.projects.enforcer

import org.junit.Assert
import org.junit.Test
import se.jguru.codestyle.projects.MavenTestUtils

/**
 *
 * @author [Lennart Jörelid](mailto:lj@jguru.se), jGuru Europe AB
 */
class CorrectPackagingRuleTest {

    @Test
    fun validateExceptionOnIncorrectSourceCodePackaging() {

        // Assemble
        val compileSourceRoot = CorrectPackagingRuleTest::class.java.classLoader.getResource(
            "testdata/project/incorrect/src/main/java")
        Assert.assertNotNull("compileSourceRoot not found", compileSourceRoot)

        val project = MavenTestUtils.readPom("testdata/project/incorrect/pom.xml")
        project.addCompileSourceRoot(compileSourceRoot.path)

        val mockHelper = MockEnforcerRuleHelper(project)
        val unitUnderTest = CorrectPackagingRule()

        // Act & Assert
        try {

            unitUnderTest.performValidation(project, mockHelper)

            Assert.fail("CorrectPackagingRule should yield an exception for projects not " +
                "complying with packaging rules.")

        } catch (e: RuleFailureException) {

            val message = e.message ?: "<none>"

            // Validate that the message contains the package-->fileName data
            Assert.assertTrue(
                message.contains("se.jguru.nazgul.tools.validation.api=[Validatable.java, package-info.java]"))
        }

    }

    @Test(expected = IllegalArgumentException::class)
    fun validateExceptionOnCustomPackageExtractorDoesNotImplementPackageExtractor() {

        // Assemble
        val unitUnderTest = CorrectPackagingRule()

        // Act & Assert
        unitUnderTest.setPackageExtractors(MockEnforcerRuleHelper::class.java.name)
    }

    @Test(expected = IllegalArgumentException::class)
    fun validateExceptionOnCustomPackageExtractorHoldsNoDefaultConstructor() {

        // Assemble
        val unitUnderTest = CorrectPackagingRule()

        // Act & Assert
        unitUnderTest.setPackageExtractors(IncorrectNoDefaultConstructorPackageExtractor::class.java!!.getName())
    }

    @Test
    @Throws(Exception::class)
    fun validateAddingCustomPackageExtractor() {

        // Assemble
        val unitUnderTest = CorrectPackagingRule()

        // Act
        unitUnderTest.setPackageExtractors(SillyPackageExtractor::class.java.name + "," +
            "" + JavaPackageExtractor::class.java.name)

        // Assert
        val packageExtractors = unitUnderTest.javaClass.getDeclaredField("packageExtractors")
        packageExtractors.isAccessible = true

        val extractors = packageExtractors.get(unitUnderTest) as List<PackageExtractor>
        Assert.assertEquals(2, extractors.size.toLong())
        Assert.assertEquals(SillyPackageExtractor::class.java.name, extractors[0].javaClass.name)
        Assert.assertEquals(JavaPackageExtractor::class.java.name, extractors[1].javaClass.name)

        Assert.assertNotNull(unitUnderTest.getShortRuleDescription())
    }
}