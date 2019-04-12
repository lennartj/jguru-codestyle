/*
 * Copyright (c) jGuru Europe AB.
 * All rights reserved.
 */

package se.jguru.codestyle.projects

import org.junit.Assert
import org.junit.Test

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
        Assert.assertEquals(CommonProjectType.PARENT, CommonProjectType.getProjectType(parent))
        Assert.assertEquals(CommonProjectType.REACTOR, CommonProjectType.getProjectType(reactor))
        Assert.assertEquals(CommonProjectType.ASSEMBLY, CommonProjectType.getProjectType(assembly))
        Assert.assertEquals(CommonProjectType.MODEL, CommonProjectType.getProjectType(model))
        Assert.assertEquals(CommonProjectType.API, CommonProjectType.getProjectType(api))
        Assert.assertEquals(CommonProjectType.SPI, CommonProjectType.getProjectType(spi))
        Assert.assertEquals(CommonProjectType.IMPLEMENTATION, CommonProjectType.getProjectType(impl))
        Assert.assertEquals(CommonProjectType.TEST, CommonProjectType.getProjectType(test))
        Assert.assertEquals(CommonProjectType.PROOF_OF_CONCEPT, CommonProjectType.getProjectType(poc))
        Assert.assertEquals(CommonProjectType.JEE_APPLICATION, CommonProjectType.getProjectType(war))
        Assert.assertEquals(CommonProjectType.JEE_APPLICATION, CommonProjectType.getProjectType(ear))
        Assert.assertEquals(CommonProjectType.JEE_APPLICATION, CommonProjectType.getProjectType(ejb))
        Assert.assertEquals(CommonProjectType.JAVA_AGENT, CommonProjectType.getProjectType(javaAgent))
        Assert.assertEquals(CommonProjectType.STANDALONE_APPLICATION,
                CommonProjectType.getProjectType(standaloneApplication1))
        Assert.assertEquals(CommonProjectType.STANDALONE_APPLICATION,
                CommonProjectType.getProjectType(standaloneApplication2))
        Assert.assertEquals(CommonProjectType.CODESTYLE, CommonProjectType.getProjectType(codestyle))
        Assert.assertEquals(CommonProjectType.INTEGRATION_TEST, CommonProjectType.getProjectType(itest))
        Assert.assertEquals(CommonProjectType.PLUGIN, CommonProjectType.getProjectType(mavenPlugin))
        Assert.assertEquals(CommonProjectType.BILL_OF_MATERIALS, CommonProjectType.getProjectType(bom))
    }

    @Test
    fun validateModelProjectPatterns() {

        // Act & Assert
        Assert.assertNull(CommonProjectType.MODEL.artifactIDNonComplianceMessage("test-foo-model"))
        Assert.assertNotNull(CommonProjectType.MODEL.artifactIDNonComplianceMessage("model-test"))
        Assert.assertNotNull(CommonProjectType.MODEL.artifactIDNonComplianceMessage("foo-model-test"))

        Assert.assertNull(CommonProjectType.MODEL.groupIDNonComplianceMessage("test.foo.model"))
        Assert.assertNotNull(CommonProjectType.MODEL.groupIDNonComplianceMessage("model.test"))
        Assert.assertNotNull(CommonProjectType.MODEL.groupIDNonComplianceMessage("foo.model.test"))

        Assert.assertNotNull(CommonProjectType.MODEL.packagingNonComplianceMessage("pom"))
        Assert.assertNull(CommonProjectType.MODEL.packagingNonComplianceMessage("jar"))
        Assert.assertNull(CommonProjectType.MODEL.packagingNonComplianceMessage("bundle"))
        Assert.assertNotNull(CommonProjectType.MODEL.packagingNonComplianceMessage("war"))
    }

    @Test
    fun validateReactorProjectPatterns() {

        // Act & Assert
        Assert.assertNull(CommonProjectType.REACTOR.artifactIDNonComplianceMessage("test-foo-reactor"))
        Assert.assertNotNull(CommonProjectType.REACTOR.artifactIDNonComplianceMessage("reactor-test"))
        Assert.assertNotNull(CommonProjectType.REACTOR.artifactIDNonComplianceMessage("foo-reactor-test"))

        Assert.assertNull(CommonProjectType.REACTOR.groupIDNonComplianceMessage("test.foo.reactor"))
        Assert.assertNull(CommonProjectType.REACTOR.groupIDNonComplianceMessage("reactor.test"))
        Assert.assertNull(CommonProjectType.REACTOR.groupIDNonComplianceMessage("foo.model.test"))

        Assert.assertNull(CommonProjectType.REACTOR.packagingNonComplianceMessage("pom"))
        Assert.assertNotNull(CommonProjectType.REACTOR.packagingNonComplianceMessage("jar"))
        Assert.assertNotNull(CommonProjectType.REACTOR.packagingNonComplianceMessage("bundle"))
    }

    @Test
    fun validateAssemblyProjectPatterns() {

        // Act & Assert
        Assert.assertNull(CommonProjectType.ASSEMBLY.artifactIDNonComplianceMessage("test-foo-assembly"))
        Assert.assertNotNull(CommonProjectType.ASSEMBLY.artifactIDNonComplianceMessage("assembly-test"))
        Assert.assertNotNull(CommonProjectType.ASSEMBLY.artifactIDNonComplianceMessage("foo-assembly-test"))

        Assert.assertNull(CommonProjectType.ASSEMBLY.groupIDNonComplianceMessage("test.foo.assembly"))
        Assert.assertNull(CommonProjectType.ASSEMBLY.groupIDNonComplianceMessage("assembly.test"))
        Assert.assertNull(CommonProjectType.ASSEMBLY.groupIDNonComplianceMessage("foo.assembly.test"))

        Assert.assertNull(CommonProjectType.ASSEMBLY.packagingNonComplianceMessage("pom"))
        Assert.assertNotNull(CommonProjectType.ASSEMBLY.packagingNonComplianceMessage("jar"))
        Assert.assertNotNull(CommonProjectType.ASSEMBLY.packagingNonComplianceMessage("bundle"))
    }

    @Test
    fun validatePluginProjectPatterns() {

        // Act & Assert
        Assert.assertNull(CommonProjectType.PLUGIN.artifactIDNonComplianceMessage("test-foo-maven-plugin"))
        Assert.assertNotNull(CommonProjectType.PLUGIN.artifactIDNonComplianceMessage("foo-maven-plugin-test"))
        Assert.assertNotNull(CommonProjectType.PLUGIN.artifactIDNonComplianceMessage("foo-plugin-test"))

        Assert.assertNull(CommonProjectType.PLUGIN.groupIDNonComplianceMessage("test.foo.plugin"))
        Assert.assertNull(CommonProjectType.PLUGIN.groupIDNonComplianceMessage("plugin.test"))
        Assert.assertNull(CommonProjectType.PLUGIN.groupIDNonComplianceMessage("foo.plugin.test"))

        Assert.assertNotNull(CommonProjectType.PLUGIN.packagingNonComplianceMessage("pom"))
        Assert.assertNull(CommonProjectType.PLUGIN.packagingNonComplianceMessage("maven-plugin"))
        Assert.assertNotNull(CommonProjectType.PLUGIN.packagingNonComplianceMessage("bundle"))
    }

    @Test
    fun validateApiProjectPatterns() {

        val apiArtifactRegex = Regex(".*-api$", DefaultProjectType.IGNORE_CASE_AND_COMMENTS)
        val result = apiArtifactRegex.matches("api-test")
        Assert.assertNotNull("nopes: $result", result)

        // Act & Assert
        Assert.assertNull(CommonProjectType.API.artifactIDNonComplianceMessage("test-foo-api"))
        Assert.assertNotNull(CommonProjectType.API.artifactIDNonComplianceMessage("test-api-foo"))
        Assert.assertNotNull(CommonProjectType.API.artifactIDNonComplianceMessage("api-test"))
        Assert.assertNotNull(CommonProjectType.API.artifactIDNonComplianceMessage(null))

        Assert.assertNotNull(CommonProjectType.API.groupIDNonComplianceMessage("test.api.foo"))
        Assert.assertNotNull(CommonProjectType.API.groupIDNonComplianceMessage("api.test"))
        Assert.assertNull(CommonProjectType.API.groupIDNonComplianceMessage("test.api"))
        Assert.assertNotNull(CommonProjectType.API.groupIDNonComplianceMessage(null))

        Assert.assertNotNull(CommonProjectType.API.packagingNonComplianceMessage("pom"))
        Assert.assertNull(CommonProjectType.API.packagingNonComplianceMessage("jar"))
        Assert.assertNull(CommonProjectType.API.packagingNonComplianceMessage("bundle"))
        Assert.assertNotNull(CommonProjectType.API.packagingNonComplianceMessage(null))
        Assert.assertNotNull(CommonProjectType.API.packagingNonComplianceMessage("war"))
    }

    @Test
    fun validateSpiProjectPatterns() {

        // Act & Assert
        Assert.assertNotNull(CommonProjectType.SPI.artifactIDNonComplianceMessage("test-foo-spi"))
        Assert.assertNull(CommonProjectType.SPI.artifactIDNonComplianceMessage("test-spi-foo"))
        Assert.assertNotNull(CommonProjectType.SPI.artifactIDNonComplianceMessage("spi-test"))

        Assert.assertNull(CommonProjectType.SPI.groupIDNonComplianceMessage("test.spi.foo"))
        Assert.assertNotNull(CommonProjectType.SPI.groupIDNonComplianceMessage("spi.test"))
        Assert.assertNotNull(CommonProjectType.SPI.groupIDNonComplianceMessage("test.spi"))

        Assert.assertNotNull(CommonProjectType.SPI.packagingNonComplianceMessage("pom"))
        Assert.assertNull(CommonProjectType.SPI.packagingNonComplianceMessage("jar"))
        Assert.assertNull(CommonProjectType.SPI.packagingNonComplianceMessage("bundle"))
        Assert.assertNotNull(CommonProjectType.SPI.packagingNonComplianceMessage("war"))
    }

    @Test
    fun validateImplementationProjectPatterns() {

        // Act & Assert
        Assert.assertNotNull(CommonProjectType.IMPLEMENTATION.artifactIDNonComplianceMessage("test-foo-impl"))
        Assert.assertNull(CommonProjectType.IMPLEMENTATION.artifactIDNonComplianceMessage("test-impl-foo"))
        Assert.assertNotNull(CommonProjectType.IMPLEMENTATION.artifactIDNonComplianceMessage("impl-test"))

        Assert.assertNull(CommonProjectType.IMPLEMENTATION.groupIDNonComplianceMessage("test.impl.foo"))
        Assert.assertNotNull(CommonProjectType.IMPLEMENTATION.groupIDNonComplianceMessage("impl.test"))
        Assert.assertNotNull(CommonProjectType.IMPLEMENTATION.groupIDNonComplianceMessage("test.impl"))

        Assert.assertNotNull(CommonProjectType.IMPLEMENTATION.packagingNonComplianceMessage("pom"))
        Assert.assertNull(CommonProjectType.IMPLEMENTATION.packagingNonComplianceMessage("jar"))
        Assert.assertNull(CommonProjectType.IMPLEMENTATION.packagingNonComplianceMessage("bundle"))
        Assert.assertNotNull(CommonProjectType.IMPLEMENTATION.packagingNonComplianceMessage("war"))
    }

    @Test
    fun validateTestProjectPatterns() {

        // Act & Assert
        Assert.assertNotNull(CommonProjectType.TEST.artifactIDNonComplianceMessage("test-foo-impl"))
        Assert.assertNotNull(CommonProjectType.TEST.artifactIDNonComplianceMessage("test-impl-foo"))
        Assert.assertNull(CommonProjectType.TEST.artifactIDNonComplianceMessage("foo-test"))
        Assert.assertNull(CommonProjectType.TEST.artifactIDNonComplianceMessage("nazgul-core-osgi-test"))

        Assert.assertNotNull(CommonProjectType.TEST.groupIDNonComplianceMessage("test.foo"))
        Assert.assertNotNull(CommonProjectType.TEST.groupIDNonComplianceMessage("impl.test"))
        Assert.assertNull(CommonProjectType.TEST.groupIDNonComplianceMessage("some.test.foo"))
        Assert.assertNull(CommonProjectType.TEST.groupIDNonComplianceMessage("se.jguru.nazgul.test.osgi"))

        Assert.assertNull(CommonProjectType.TEST.packagingNonComplianceMessage("bundle"))
        Assert.assertNull(CommonProjectType.TEST.packagingNonComplianceMessage("jar"))
        Assert.assertNull(CommonProjectType.TEST.packagingNonComplianceMessage("war"))
    }

    @Test
    fun validateIntegrationTestProjectPatterns() {

        // Act & Assert
        Assert.assertNotNull(CommonProjectType.INTEGRATION_TEST.artifactIDNonComplianceMessage("it-foo-impl"))
        Assert.assertNotNull(CommonProjectType.INTEGRATION_TEST.artifactIDNonComplianceMessage("test-it-foo"))
        Assert.assertNull(CommonProjectType.INTEGRATION_TEST.artifactIDNonComplianceMessage("foo-it"))

        Assert.assertNotNull(CommonProjectType.INTEGRATION_TEST.groupIDNonComplianceMessage("it.foo"))
        Assert.assertNotNull(CommonProjectType.INTEGRATION_TEST.groupIDNonComplianceMessage("impl.it"))
        Assert.assertNull(CommonProjectType.INTEGRATION_TEST.groupIDNonComplianceMessage("some.it.foo"))
    }

    @Test
    fun validatePocProjectPatterns() {

        // Act & Assert
        Assert.assertNotNull(CommonProjectType.PROOF_OF_CONCEPT.artifactIDNonComplianceMessage("poc-foo-impl"))
        Assert.assertNotNull(CommonProjectType.PROOF_OF_CONCEPT.artifactIDNonComplianceMessage("test-poc-foo"))
        Assert.assertNull(CommonProjectType.PROOF_OF_CONCEPT.artifactIDNonComplianceMessage("foo-poc"))

        Assert.assertNotNull(CommonProjectType.PROOF_OF_CONCEPT.groupIDNonComplianceMessage("poc.foo"))
        Assert.assertNotNull(CommonProjectType.PROOF_OF_CONCEPT.groupIDNonComplianceMessage("impl.poc"))
        Assert.assertNull(CommonProjectType.PROOF_OF_CONCEPT.groupIDNonComplianceMessage("some.poc.foo"))
    }

    @Test
    fun validateCodestyleProjectPatterns() {

        // Act & Assert
        Assert.assertNotNull(CommonProjectType.CODESTYLE.artifactIDNonComplianceMessage("codestyle-foo"))
        Assert.assertNull(CommonProjectType.CODESTYLE.artifactIDNonComplianceMessage("foo-codestyle"))

        Assert.assertNotNull(CommonProjectType.CODESTYLE.groupIDNonComplianceMessage("codestyle.foo"))
        Assert.assertNull(CommonProjectType.CODESTYLE.groupIDNonComplianceMessage("foo.codestyle"))
    }

    @Test
    fun validateExampleProjectPatterns() {

        // Act & Assert
        Assert.assertNull(CommonProjectType.EXAMPLE.artifactIDNonComplianceMessage("codestyle-foo-example"))
        Assert.assertNotNull(CommonProjectType.EXAMPLE.artifactIDNonComplianceMessage("example-codestyle"))

        Assert.assertNull(CommonProjectType.EXAMPLE.groupIDNonComplianceMessage("codestyle.foo.example"))
        Assert.assertNotNull(CommonProjectType.EXAMPLE.groupIDNonComplianceMessage("foo.example.codestyle"))
    }
}