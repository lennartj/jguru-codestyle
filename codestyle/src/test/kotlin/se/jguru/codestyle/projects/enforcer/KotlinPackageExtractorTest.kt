/*
 * Copyright (c) jGuru Europe AB.
 * All rights reserved.
 */

package se.jguru.codestyle.projects.enforcer

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatExceptionOfType
import org.junit.jupiter.api.Test
import java.io.File
import java.util.Arrays
import java.util.TreeMap
import kotlin.reflect.KProperty
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.jvm.isAccessible

/**
 *
 * @author [Lennart J&ouml;relid](mailto:lj@jguru.se), jGuru Europe AB
 */
class KotlinPackageExtractorTest {

    @Test
    fun validatePackagePatternMatching() {

        // Assemble
        val validPackages = Arrays.asList("se", "se.jguru", "se.jguru.nazgul")
        val invalidPackages = Arrays.asList("se.", "se..jguru", ".se.jguru.nazgul")
        val kotlinPackageRegex = getRegexFrom(KotlinPackageExtractor())

        // Act & Assert
        validPackages
            .map { "package $it;" }
            .forEach {

                assertThat(kotlinPackageRegex.matches(it))
                    .withFailMessage("Valid package line [$it] did not match.")
                    .isTrue()
            }

        invalidPackages
            .map { "package $it;" }
            .forEach {

                assertThat(kotlinPackageRegex.matches(it))
                    .withFailMessage("Invalid package line [$it] did match.")
                    .isFalse()
            }
    }

    @Test
    fun validateDefaultPackageReturnedOnMalformedPackageStatement() {

        // Assemble
        val resource = KotlinPackageExtractorTest::class.java.getResource(
            "/testdata/packages/lang_kotlin/incorrect/nokNotAPackage.txt")
        val packageDir = File(resource.path).parentFile

        val unitUnderTest = KotlinPackageExtractor()
        val packageNames = TreeMap<String, String>()

        // Act
        packageDir.listFiles()!!
            .filter { it.isFile }
            .forEach { packageNames[it.name] = unitUnderTest.getPackage(it) }

        // Assert
        assertThat(packageNames.size.toLong()).isEqualTo(1)
        assertThat(packageNames["nokNotAPackage.txt"]).isEqualTo("")
    }

    @Test
    fun validateExceptionOnSubmittingDirectoriesToPackageExtractor() {

        // Assemble
        val resource = KotlinPackageExtractorTest::class.java.getResource("/testdata/kotlin/incorrect")
        val unitUnderTest = KotlinPackageExtractor()

        // Act
        assertThatExceptionOfType(IllegalStateException::class.java).isThrownBy {
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