/*
 * Copyright (c) jGuru Europe AB.
 * All rights reserved.
 */

package se.jguru.codestyle.projects

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test


/**
 *
 * @author [Lennart Jörelid](mailto:lj@jguru.se), jGuru Europe AB
 */
class CommonProjectTypeTest {

    @Test
    fun validateParsingProjectTypes() {

        // Assemble
        val parent = MavenTestUtils.getStub("pom", "se.jguru.foo.bar", "bar-parent")
        val reactor = MavenTestUtils.getStub("pom", "se.jguru.foo.bar", "bar-reactor")
        val assembly = MavenTestUtils.getStub("pom", "se.jguru.foo.bar", "bar-assembly")
        val model = MavenTestUtils.getStub("bundle", "se.jguru.foo.bar.model", "bar-model")
        val api = MavenTestUtils.getStub("bundle", "se.jguru.foo.bar.api", "bar-api")
        val spi = MavenTestUtils.getStub(
                "bundle", "se.jguru.foo.bar.spi.something", "bar-spi-something")
        val impl = MavenTestUtils.getStub(
                "bundle", "se.jguru.foo.bar.impl.something", "bar-impl-something")
        val test = MavenTestUtils.getStub("jar", "se.jguru.foo.bar.test.something", "bar-test")
        val poc = MavenTestUtils.getStub("bundle", "se.jguru.foo.bar.poc.something", "bar-poc")
        val war = MavenTestUtils.getStub("war", "se.jguru.foo.applications.bar", "bar-war")
        val ear = MavenTestUtils.getStub("ear", "se.jguru.foo.applications.bar", "bar-ear")
        val ejb = MavenTestUtils.getStub("ejb", "se.jguru.foo.applications.bar", "bar-ejb")
        val codestyle = MavenTestUtils.getStub("jar", "se.jguru.foo.codestyle", "bar-codestyle")
        val javaAgent = MavenTestUtils.getStub("bundle", "se.jguru.foo.bar.agent", "bar-agent")
        val standaloneApplication1 = MavenTestUtils.getStub("bundle", "se.jguru.foo.bar.application", "bar-application")
        val standaloneApplication2 = MavenTestUtils.getStub("jar", "se.jguru.foo.bar.application", "bar-application")
        val itest = MavenTestUtils.getStub("jar", "se.jguru.foo.it.bar", "bar-it")
        val mavenPlugin = MavenTestUtils.getStub("maven-plugin", "se.jguru.foo.bar.plugins.blah", "blah-maven-plugin")
        val bom = MavenTestUtils.getStub("pom", "se.jguru.foo.whatever", "foo-bom")

        // Act & Assert
        assertThat(CommonProjectType.getProjectType(parent)).isEqualTo(CommonProjectType.PARENT)
        assertThat(CommonProjectType.getProjectType(reactor)).isEqualTo(CommonProjectType.REACTOR)
        assertThat(CommonProjectType.getProjectType(assembly)).isEqualTo(CommonProjectType.ASSEMBLY)
        assertThat(CommonProjectType.getProjectType(model)).isEqualTo(CommonProjectType.MODEL)
        assertThat(CommonProjectType.getProjectType(api)).isEqualTo(CommonProjectType.API)
        assertThat(CommonProjectType.getProjectType(spi)).isEqualTo(CommonProjectType.SPI)
        assertThat(CommonProjectType.getProjectType(impl)).isEqualTo(CommonProjectType.IMPLEMENTATION)
        assertThat(CommonProjectType.getProjectType(test)).isEqualTo(CommonProjectType.TEST)
        assertThat(CommonProjectType.getProjectType(poc)).isEqualTo(CommonProjectType.PROOF_OF_CONCEPT)
        assertThat(CommonProjectType.getProjectType(war)).isEqualTo(CommonProjectType.JEE_APPLICATION)
        assertThat(CommonProjectType.getProjectType(ear)).isEqualTo(CommonProjectType.JEE_APPLICATION)
        assertThat(CommonProjectType.getProjectType(ejb)).isEqualTo(CommonProjectType.JEE_APPLICATION)
        assertThat(CommonProjectType.getProjectType(javaAgent)).isEqualTo(CommonProjectType.JAVA_AGENT)
        assertThat(CommonProjectType.getProjectType(standaloneApplication1)).isEqualTo(CommonProjectType.STANDALONE_APPLICATION)
        assertThat(CommonProjectType.getProjectType(standaloneApplication2)).isEqualTo(CommonProjectType.STANDALONE_APPLICATION)
        assertThat(CommonProjectType.getProjectType(codestyle)).isEqualTo(CommonProjectType.CODESTYLE)
        assertThat(CommonProjectType.getProjectType(itest)).isEqualTo(CommonProjectType.INTEGRATION_TEST)
        assertThat(CommonProjectType.getProjectType(mavenPlugin)).isEqualTo(CommonProjectType.PLUGIN)
        assertThat(CommonProjectType.getProjectType(bom)).isEqualTo(CommonProjectType.BILL_OF_MATERIALS)
    }

    @Test
    fun validateModelProjectPatterns() {

        // Act & Assert
        assertThat(CommonProjectType.MODEL.artifactIDNonComplianceMessage("test-foo-model")).isNull()
        assertThat(CommonProjectType.MODEL.artifactIDNonComplianceMessage("model-test")).isNotNull()
        assertThat(CommonProjectType.MODEL.artifactIDNonComplianceMessage("foo-model-test")).isNotNull()

        assertThat(CommonProjectType.MODEL.groupIDNonComplianceMessage("test.foo.model")).isNull()
        assertThat(CommonProjectType.MODEL.groupIDNonComplianceMessage("model.test")).isNotNull()
        assertThat(CommonProjectType.MODEL.groupIDNonComplianceMessage("foo.model.test")).isNotNull()

        assertThat(CommonProjectType.MODEL.packagingNonComplianceMessage("pom")).isNotNull()
        assertThat(CommonProjectType.MODEL.packagingNonComplianceMessage("jar")).isNull()
        assertThat(CommonProjectType.MODEL.packagingNonComplianceMessage("bundle")).isNull()
        assertThat(CommonProjectType.MODEL.packagingNonComplianceMessage("war")).isNotNull()
    }

    @Test
    fun validateReactorProjectPatterns() {

        // Act & Assert
        assertThat(CommonProjectType.REACTOR.artifactIDNonComplianceMessage("test-foo-reactor")).isNull()
        assertThat(CommonProjectType.REACTOR.artifactIDNonComplianceMessage("reactor-test")).isNotNull()
        assertThat(CommonProjectType.REACTOR.artifactIDNonComplianceMessage("foo-reactor-test")).isNotNull()

        assertThat(CommonProjectType.REACTOR.groupIDNonComplianceMessage("test.foo.reactor")).isNull()
        assertThat(CommonProjectType.REACTOR.groupIDNonComplianceMessage("reactor.test")).isNull()
        assertThat(CommonProjectType.REACTOR.groupIDNonComplianceMessage("foo.model.test")).isNull()

        assertThat(CommonProjectType.REACTOR.packagingNonComplianceMessage("pom")).isNull()
        assertThat(CommonProjectType.REACTOR.packagingNonComplianceMessage("jar")).isNotNull()
        assertThat(CommonProjectType.REACTOR.packagingNonComplianceMessage("bundle")).isNotNull()
    }

    @Test
    fun validateAssemblyProjectPatterns() {

        // Act & Assert
        assertThat(CommonProjectType.ASSEMBLY.artifactIDNonComplianceMessage("test-foo-assembly")).isNull()
        assertThat(CommonProjectType.ASSEMBLY.artifactIDNonComplianceMessage("assembly-test")).isNotNull()
        assertThat(CommonProjectType.ASSEMBLY.artifactIDNonComplianceMessage("foo-assembly-test")).isNotNull()

        assertThat(CommonProjectType.ASSEMBLY.groupIDNonComplianceMessage("test.foo.assembly")).isNull()
        assertThat(CommonProjectType.ASSEMBLY.groupIDNonComplianceMessage("assembly.test")).isNull()
        assertThat(CommonProjectType.ASSEMBLY.groupIDNonComplianceMessage("foo.assembly.test")).isNull()

        assertThat(CommonProjectType.ASSEMBLY.packagingNonComplianceMessage("pom")).isNull()
        assertThat(CommonProjectType.ASSEMBLY.packagingNonComplianceMessage("jar")).isNotNull()
        assertThat(CommonProjectType.ASSEMBLY.packagingNonComplianceMessage("bundle")).isNotNull()
    }

    @Test
    fun validatePluginProjectPatterns() {

        // Act & Assert
        assertThat(CommonProjectType.PLUGIN.artifactIDNonComplianceMessage("test-foo-maven-plugin")).isNull()
        assertThat(CommonProjectType.PLUGIN.artifactIDNonComplianceMessage("foo-maven-plugin-test")).isNotNull()
        assertThat(CommonProjectType.PLUGIN.artifactIDNonComplianceMessage("foo-plugin-test")).isNotNull()

        assertThat(CommonProjectType.PLUGIN.groupIDNonComplianceMessage("test.foo.plugin")).isNull()
        assertThat(CommonProjectType.PLUGIN.groupIDNonComplianceMessage("plugin.test")).isNull()
        assertThat(CommonProjectType.PLUGIN.groupIDNonComplianceMessage("foo.plugin.test")).isNull()

        assertThat(CommonProjectType.PLUGIN.packagingNonComplianceMessage("pom")).isNotNull()
        assertThat(CommonProjectType.PLUGIN.packagingNonComplianceMessage("maven-plugin")).isNull()
        assertThat(CommonProjectType.PLUGIN.packagingNonComplianceMessage("bundle")).isNotNull()
    }

    @Test
    fun validateApiProjectPatterns() {

        val apiArtifactRegex = Regex(".*-api$", DefaultProjectType.IGNORE_CASE_AND_COMMENTS)
        val result = apiArtifactRegex.matches("api-test")
        assertThat(result).withFailMessage("nopes: $result").isNotNull()

        // Act & Assert
        assertThat(CommonProjectType.API.artifactIDNonComplianceMessage("test-foo-api")).isNull()
        assertThat(CommonProjectType.API.artifactIDNonComplianceMessage("test-api-foo")).isNotNull()
        assertThat(CommonProjectType.API.artifactIDNonComplianceMessage("api-test")).isNotNull()
        assertThat(CommonProjectType.API.artifactIDNonComplianceMessage(null)).isNotNull()

        assertThat(CommonProjectType.API.groupIDNonComplianceMessage("test.api.foo"))
        assertThat(CommonProjectType.API.groupIDNonComplianceMessage("api.test")).isNotNull()
        assertThat(CommonProjectType.API.groupIDNonComplianceMessage("test.api")).isNull()
        assertThat(CommonProjectType.API.groupIDNonComplianceMessage(null)).isNotNull()

        assertThat(CommonProjectType.API.packagingNonComplianceMessage("pom")).isNotNull()
        assertThat(CommonProjectType.API.packagingNonComplianceMessage("jar")).isNull()
        assertThat(CommonProjectType.API.packagingNonComplianceMessage("bundle")).isNull()
        assertThat(CommonProjectType.API.packagingNonComplianceMessage(null)).isNotNull()
        assertThat(CommonProjectType.API.packagingNonComplianceMessage("war")).isNotNull()
    }

    @Test
    fun validateSpiProjectPatterns() {

        // Act & Assert
        assertThat(CommonProjectType.SPI.artifactIDNonComplianceMessage("test-foo-spi")).isNotNull()
        assertThat(CommonProjectType.SPI.artifactIDNonComplianceMessage("test-spi-foo")).isNull()
        assertThat(CommonProjectType.SPI.artifactIDNonComplianceMessage("spi-test")).isNotNull()

        assertThat(CommonProjectType.SPI.groupIDNonComplianceMessage("test.spi.foo")).isNull()
        assertThat(CommonProjectType.SPI.groupIDNonComplianceMessage("spi.test")).isNotNull()
        assertThat(CommonProjectType.SPI.groupIDNonComplianceMessage("test.spi")).isNotNull()

        assertThat(CommonProjectType.SPI.packagingNonComplianceMessage("pom")).isNotNull()
        assertThat(CommonProjectType.SPI.packagingNonComplianceMessage("jar")).isNull()
        assertThat(CommonProjectType.SPI.packagingNonComplianceMessage("bundle")).isNull()
        assertThat(CommonProjectType.SPI.packagingNonComplianceMessage("war")).isNotNull()
    }

    @Test
    fun validateImplementationProjectPatterns() {

        // Act & Assert
        assertThat(CommonProjectType.IMPLEMENTATION.artifactIDNonComplianceMessage("test-foo-impl")).isNotNull()
        assertThat(CommonProjectType.IMPLEMENTATION.artifactIDNonComplianceMessage("test-impl-foo")).isNull()
        assertThat(CommonProjectType.IMPLEMENTATION.artifactIDNonComplianceMessage("impl-test")).isNotNull()

        assertThat(CommonProjectType.IMPLEMENTATION.groupIDNonComplianceMessage("test.impl.foo")).isNull()
        assertThat(CommonProjectType.IMPLEMENTATION.groupIDNonComplianceMessage("impl.test")).isNotNull()
        assertThat(CommonProjectType.IMPLEMENTATION.groupIDNonComplianceMessage("test.impl")).isNotNull()

        assertThat(CommonProjectType.IMPLEMENTATION.packagingNonComplianceMessage("pom")).isNotNull()
        assertThat(CommonProjectType.IMPLEMENTATION.packagingNonComplianceMessage("jar")).isNull()
        assertThat(CommonProjectType.IMPLEMENTATION.packagingNonComplianceMessage("bundle")).isNull()
        assertThat(CommonProjectType.IMPLEMENTATION.packagingNonComplianceMessage("war")).isNotNull()
    }

    @Test
    fun validateTestProjectPatterns() {

        // Act & Assert
        assertThat(CommonProjectType.TEST.artifactIDNonComplianceMessage("test-foo-impl")).isNotNull()
        assertThat(CommonProjectType.TEST.artifactIDNonComplianceMessage("test-impl-foo")).isNotNull()
        assertThat(CommonProjectType.TEST.artifactIDNonComplianceMessage("foo-test")).isNull()
        assertThat(CommonProjectType.TEST.artifactIDNonComplianceMessage("nazgul-core-osgi-test")).isNull()

        assertThat(CommonProjectType.TEST.groupIDNonComplianceMessage("test.foo")).isNotNull()
        assertThat(CommonProjectType.TEST.groupIDNonComplianceMessage("impl.test")).isNotNull()
        assertThat(CommonProjectType.TEST.groupIDNonComplianceMessage("some.test.foo")).isNull()
        assertThat(CommonProjectType.TEST.groupIDNonComplianceMessage("se.jguru.nazgul.test.osgi")).isNull()

        assertThat(CommonProjectType.TEST.packagingNonComplianceMessage("bundle")).isNull()
        assertThat(CommonProjectType.TEST.packagingNonComplianceMessage("jar")).isNull()
        assertThat(CommonProjectType.TEST.packagingNonComplianceMessage("war")).isNull()
    }

    @Test
    fun validateIntegrationTestProjectPatterns() {

        // Act & Assert
        assertThat(CommonProjectType.INTEGRATION_TEST.artifactIDNonComplianceMessage("it-foo-impl")).isNotNull()
        assertThat(CommonProjectType.INTEGRATION_TEST.artifactIDNonComplianceMessage("test-it-foo")).isNotNull()
        assertThat(CommonProjectType.INTEGRATION_TEST.artifactIDNonComplianceMessage("foo-it")).isNull()

        assertThat(CommonProjectType.INTEGRATION_TEST.groupIDNonComplianceMessage("it.foo")).isNotNull()
        assertThat(CommonProjectType.INTEGRATION_TEST.groupIDNonComplianceMessage("impl.it")).isNotNull()
        assertThat(CommonProjectType.INTEGRATION_TEST.groupIDNonComplianceMessage("some.it.foo")).isNull()
    }

    @Test
    fun validatePocProjectPatterns() {

        // Act & Assert
        assertThat(CommonProjectType.PROOF_OF_CONCEPT.artifactIDNonComplianceMessage("poc-foo-impl")).isNotNull()
        assertThat(CommonProjectType.PROOF_OF_CONCEPT.artifactIDNonComplianceMessage("test-poc-foo")).isNotNull()
        assertThat(CommonProjectType.PROOF_OF_CONCEPT.artifactIDNonComplianceMessage("foo-poc")).isNull()

        assertThat(CommonProjectType.PROOF_OF_CONCEPT.groupIDNonComplianceMessage("poc.foo")).isNotNull()
        assertThat(CommonProjectType.PROOF_OF_CONCEPT.groupIDNonComplianceMessage("impl.poc")).isNotNull()
        assertThat(CommonProjectType.PROOF_OF_CONCEPT.groupIDNonComplianceMessage("some.poc.foo")).isNull()
    }

    @Test
    fun validateCodestyleProjectPatterns() {

        // Act & Assert
        assertThat(CommonProjectType.CODESTYLE.artifactIDNonComplianceMessage("codestyle-foo")).isNotNull()
        assertThat(CommonProjectType.CODESTYLE.artifactIDNonComplianceMessage("foo-codestyle")).isNull()

        assertThat(CommonProjectType.CODESTYLE.groupIDNonComplianceMessage("codestyle.foo")).isNotNull()
        assertThat(CommonProjectType.CODESTYLE.groupIDNonComplianceMessage("foo.codestyle")).isNull()
    }

    @Test
    fun validateExampleProjectPatterns() {

        // Act & Assert
        assertThat(CommonProjectType.EXAMPLE.artifactIDNonComplianceMessage("codestyle-foo-example")).isNull()
        assertThat(CommonProjectType.EXAMPLE.artifactIDNonComplianceMessage("example-codestyle")).isNotNull()

        assertThat(CommonProjectType.EXAMPLE.groupIDNonComplianceMessage("codestyle.foo.example")).isNull()
        assertThat(CommonProjectType.EXAMPLE.groupIDNonComplianceMessage("foo.example.codestyle")).isNotNull()
    }
}