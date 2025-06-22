/*
 * Copyright (c) jGuru Europe AB.
 * All rights reserved.
 */

package se.jguru.codestyle.projects.enforcer

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatExceptionOfType
import org.junit.jupiter.api.Test
import java.io.File
import java.io.FileNotFoundException
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
        val validPackages = listOf("se", "se.jguru", "se.jguru.nazgul")
        val invalidPackages = listOf("se.", "se..jguru", ".se.jguru.nazgul")
        val javaPackageRegex = getRegexFrom(JavaPackageExtractor())

        // Act & Assert
        validPackages
            .map { "package $it;" }
            .forEach {

                assertThat(javaPackageRegex.matches(it))
                    .withFailMessage("Valid package line [$it] did not match.")
                    .isTrue()
            }

        invalidPackages
            .map { "package $it;" }
            .forEach {

                assertThat(javaPackageRegex.matches(it))
                    .withFailMessage("Invalid package line [$it] did match.")
                    .isFalse()
            }
    }

    @Test
    fun validatePatternExtraction() {

        // Assemble
        val resource = JavaPackageExtractorTest::class.java.getResource("$JAVA_PACKAGES/okPackage.java")
            ?: throw IllegalStateException("No test resource found.")
        val packageDir = File(resource.path).parentFile

        val unitUnderTest = JavaPackageExtractor()
        val packageNames = TreeMap<String, String>()

        // Act
        packageDir
            ?.listFiles(unitUnderTest.sourceFileFilter)
            ?.forEach { packageNames[it.name] = unitUnderTest.getPackage(it) }

        // Assert
        assertThat(packageNames.size.toLong()).isEqualTo(3)
        assertThat(packageNames["okEvenLongerPackage.java"]).isEqualTo("se.jguru.nazgul.tools.codestyle")
        assertThat(packageNames["okLongerPackage.java"]).isEqualTo("se.jguru.nazgul")
        assertThat(packageNames["okPackage.java"]).isEqualTo("se.jguru")
    }

    @Test
    fun validateDefaultPackageReturnedOnMalformedPackageStatement() {

        // Assemble
        val resource = JavaPackageExtractorTest::class.java.getResource(
            "$JAVA_PACKAGES/incorrect/nokNotAPackage.txt")
            ?: throw IllegalStateException("No test resource found.")
        val packageDir = File(resource.path).parentFile

        val unitUnderTest = JavaPackageExtractor()
        val packageNames = TreeMap<String, String>()

        // Act
        packageDir.listFiles()!!
            .filter { it.isFile }
            .forEach { packageNames[it.name] = unitUnderTest.getPackage(it) }

        // Assert
        assertThat(packageNames.size).isEqualTo(1)
        assertThat(packageNames["nokNotAPackage.txt"]).isEmpty()
    }

    @Test
    fun validateExceptionOnSubmittingDirectoriesToPackageExtractor() {

        // Assemble
        val resource = JavaPackageExtractorTest::class.java.getResource("$JAVA_PACKAGES/incorrect")
            ?: throw IllegalStateException("No test resource found.")
        val unitUnderTest = JavaPackageExtractor()

        // Act
        assertThatExceptionOfType(FileNotFoundException::class.java).isThrownBy {
            unitUnderTest.getPackage(File(resource.path))
        }
    }

    //
    // Private helpers
    //

    private fun getRegexFrom(packageExtractor: AbstractSimplePackageExtractor): Regex {

        val propName = "packageRegEx"
        val props = packageExtractor::class.declaredMemberProperties.filter { it.name == propName }
        if (props.isNotEmpty()) {

            // Make the property accessible
            props[0].isAccessible = true

            @Suppress("UNCHECKED_CAST")
            return (props[0] as KProperty<Regex>).getter.call(packageExtractor)
        }

        throw IllegalArgumentException("Could not acquire property $propName from packageExtractor " +
            "of type ${packageExtractor.javaClass.simpleName}")
    }
}