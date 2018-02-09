/*
 * Copyright (c) jGuru Europe AB.
 * All rights reserved.
 */

package se.jguru.codestyle.projects.enforcer

import org.junit.Assert
import org.junit.Test
import java.io.File
import java.io.FileNotFoundException
import java.util.Arrays
import java.util.TreeMap
import kotlin.reflect.KProperty
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.jvm.isAccessible

/**
 *
 * @author [Lennart Jörelid](mailto:lj@jguru.se), jGuru Europe AB
 */
class JavaPackageExtractorTest {

    private val JAVA_PACKAGES = "/testdata/packages/lang_java"

    @Test
    fun validatePackagePatternMatching() {

        // Assemble
        val validPackages = Arrays.asList("se", "se.jguru", "se.jguru.nazgul")
        val invalidPackages = Arrays.asList("se.", "se..jguru", ".se.jguru.nazgul")
        val javaPackageRegex = getRegexFrom(JavaPackageExtractor())

        // Act & Assert
        validPackages
            .map { "package $it;" }
            .forEach {
                Assert.assertTrue("Valid package line [$it] did not match.",
                    javaPackageRegex.matches(it))
            }

        invalidPackages
            .map { "package $it;" }
            .forEach {
                Assert.assertTrue("Invalid package line [$it] did match.",
                    !javaPackageRegex.matches(it))
            }
    }

    @Test
    fun validatePatternExtraction() {

        // Assemble
        val resource = JavaPackageExtractorTest::class.java.getResource(JAVA_PACKAGES + "/okPackage.java")
        val packageDir = File(resource.path).parentFile

        val unitUnderTest = JavaPackageExtractor()
        val packageNames = TreeMap<String, String>()

        // Act
        packageDir
            .listFiles(unitUnderTest.sourceFileFilter)
            .forEach { packageNames[it.name] = unitUnderTest.getPackage(it) }

        // Assert
        Assert.assertEquals(3, packageNames.size.toLong())
        Assert.assertEquals("se.jguru.nazgul.tools.codestyle", packageNames["okEvenLongerPackage.java"])
        Assert.assertEquals("se.jguru.nazgul", packageNames["okLongerPackage.java"])
        Assert.assertEquals("se.jguru", packageNames["okPackage.java"])
    }

    @Test
    fun validateDefaultPackageReturnedOnMalformedPackageStatement() {

        // Assemble
        val resource = JavaPackageExtractorTest::class.java.getResource(
            JAVA_PACKAGES + "/incorrect/nokNotAPackage.txt")
        val packageDir = File(resource.path).parentFile

        val unitUnderTest = JavaPackageExtractor()
        val packageNames = TreeMap<String, String>()

        // Act
        packageDir.listFiles()!!
            .filter { it.isFile }
            .forEach { packageNames[it.name] = unitUnderTest.getPackage(it) }

        // Assert
        Assert.assertEquals(1, packageNames.size.toLong())
        Assert.assertEquals("", packageNames["nokNotAPackage.txt"])
    }

    @Test(expected = FileNotFoundException::class)
    fun validateExceptionOnSubmittingDirectoriesToPackageExtractor() {

        // Assemble
        val resource = JavaPackageExtractorTest::class.java.getResource(JAVA_PACKAGES + "/incorrect")
        val unitUnderTest = JavaPackageExtractor()

        // Act
        unitUnderTest.getPackage(File(resource.path))
    }

    //
    // Private helpers
    //

    fun getRegexFrom(packageExtractor: AbstractSimplePackageExtractor): Regex {

        val propName = "packageRegEx"
        val props = packageExtractor::class.declaredMemberProperties.filter { it.name == propName }
        if (props.isNotEmpty()) {

            // Make the property accessible
            props[0].isAccessible = true

            return (props[0] as KProperty<Regex>).getter.call(packageExtractor)
        }

        throw IllegalArgumentException("Could not acquire property $propName from packageExtractor " +
            "of type ${packageExtractor.javaClass.simpleName}")
    }
}