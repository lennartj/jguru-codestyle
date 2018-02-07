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
class CommonProjectTypesTest {

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

        // Act & Assert
        Assert.assertEquals(CommonProjectTypes.PARENT, CommonProjectTypes.getProjectType(parent))
        Assert.assertEquals(CommonProjectTypes.REACTOR, CommonProjectTypes.getProjectType(reactor))
        Assert.assertEquals(CommonProjectTypes.ASSEMBLY, CommonProjectTypes.getProjectType(assembly))
        Assert.assertEquals(CommonProjectTypes.MODEL, CommonProjectTypes.getProjectType(model))
        Assert.assertEquals(CommonProjectTypes.API, CommonProjectTypes.getProjectType(api))
        Assert.assertEquals(CommonProjectTypes.SPI, CommonProjectTypes.getProjectType(spi))
        Assert.assertEquals(CommonProjectTypes.IMPLEMENTATION, CommonProjectTypes.getProjectType(impl))
        Assert.assertEquals(CommonProjectTypes.TEST, CommonProjectTypes.getProjectType(test))
        Assert.assertEquals(CommonProjectTypes.PROOF_OF_CONCEPT, CommonProjectTypes.getProjectType(poc))
        Assert.assertEquals(CommonProjectTypes.JEE_APPLICATION, CommonProjectTypes.getProjectType(war))
        Assert.assertEquals(CommonProjectTypes.JEE_APPLICATION, CommonProjectTypes.getProjectType(ear))
        Assert.assertEquals(CommonProjectTypes.JEE_APPLICATION, CommonProjectTypes.getProjectType(ejb))
        Assert.assertEquals(CommonProjectTypes.JAVA_AGENT, CommonProjectTypes.getProjectType(javaAgent))
        Assert.assertEquals(CommonProjectTypes.STANDALONE_APPLICATION,
                CommonProjectTypes.getProjectType(standaloneApplication1))
        Assert.assertEquals(CommonProjectTypes.STANDALONE_APPLICATION,
                CommonProjectTypes.getProjectType(standaloneApplication2))
        Assert.assertEquals(CommonProjectTypes.CODESTYLE, CommonProjectTypes.getProjectType(codestyle))
        Assert.assertEquals(CommonProjectTypes.INTEGRATION_TEST, CommonProjectTypes.getProjectType(itest))
        Assert.assertEquals(CommonProjectTypes.PLUGIN, CommonProjectTypes.getProjectType(mavenPlugin))
    }

    @Test
    fun validateModelProjectPatterns() {

        // Act & Assert
        Assert.assertTrue(CommonProjectTypes.MODEL.isCompliantArtifactID("test-foo-model"))
        Assert.assertFalse(CommonProjectTypes.MODEL.isCompliantArtifactID("model-test"))
        Assert.assertFalse(CommonProjectTypes.MODEL.isCompliantArtifactID("foo-model-test"))

        Assert.assertTrue(CommonProjectTypes.MODEL.isCompliantGroupID("test.foo.model"))
        Assert.assertFalse(CommonProjectTypes.MODEL.isCompliantGroupID("model.test"))
        Assert.assertFalse(CommonProjectTypes.MODEL.isCompliantGroupID("foo.model.test"))

        Assert.assertFalse(CommonProjectTypes.MODEL.isCompliantPackaging("pom"))
        Assert.assertTrue(CommonProjectTypes.MODEL.isCompliantPackaging("jar"))
        Assert.assertTrue(CommonProjectTypes.MODEL.isCompliantPackaging("bundle"))
        Assert.assertFalse(CommonProjectTypes.MODEL.isCompliantPackaging("war"))
    }

    @Test
    fun validateReactorProjectPatterns() {

        // Act & Assert
        Assert.assertTrue(CommonProjectTypes.REACTOR.isCompliantArtifactID("test-foo-reactor"))
        Assert.assertFalse(CommonProjectTypes.REACTOR.isCompliantArtifactID("reactor-test"))
        Assert.assertFalse(CommonProjectTypes.REACTOR.isCompliantArtifactID("foo-reactor-test"))

        Assert.assertTrue(CommonProjectTypes.REACTOR.isCompliantGroupID("test.foo.reactor"))
        Assert.assertTrue(CommonProjectTypes.REACTOR.isCompliantGroupID("reactor.test"))
        Assert.assertTrue(CommonProjectTypes.REACTOR.isCompliantGroupID("foo.model.test"))

        Assert.assertTrue(CommonProjectTypes.REACTOR.isCompliantPackaging("pom"))
        Assert.assertFalse(CommonProjectTypes.REACTOR.isCompliantPackaging("jar"))
        Assert.assertFalse(CommonProjectTypes.REACTOR.isCompliantPackaging("bundle"))
    }

    @Test
    fun validateAssemblyProjectPatterns() {

        // Act & Assert
        Assert.assertTrue(CommonProjectTypes.ASSEMBLY.isCompliantArtifactID("test-foo-assembly"))
        Assert.assertFalse(CommonProjectTypes.ASSEMBLY.isCompliantArtifactID("assembly-test"))
        Assert.assertFalse(CommonProjectTypes.ASSEMBLY.isCompliantArtifactID("foo-assembly-test"))

        Assert.assertTrue(CommonProjectTypes.ASSEMBLY.isCompliantGroupID("test.foo.assembly"))
        Assert.assertTrue(CommonProjectTypes.ASSEMBLY.isCompliantGroupID("assembly.test"))
        Assert.assertTrue(CommonProjectTypes.ASSEMBLY.isCompliantGroupID("foo.assembly.test"))

        Assert.assertTrue(CommonProjectTypes.ASSEMBLY.isCompliantPackaging("pom"))
        Assert.assertFalse(CommonProjectTypes.ASSEMBLY.isCompliantPackaging("jar"))
        Assert.assertFalse(CommonProjectTypes.ASSEMBLY.isCompliantPackaging("bundle"))
    }

    @Test
    fun validatePluginProjectPatterns() {

        // Act & Assert
        Assert.assertTrue(CommonProjectTypes.PLUGIN.isCompliantArtifactID("test-foo-maven-plugin"))
        Assert.assertFalse(CommonProjectTypes.PLUGIN.isCompliantArtifactID("foo-maven-plugin-test"))
        Assert.assertFalse(CommonProjectTypes.PLUGIN.isCompliantArtifactID("foo-plugin-test"))

        Assert.assertTrue(CommonProjectTypes.PLUGIN.isCompliantGroupID("test.foo.plugin"))
        Assert.assertTrue(CommonProjectTypes.PLUGIN.isCompliantGroupID("plugin.test"))
        Assert.assertTrue(CommonProjectTypes.PLUGIN.isCompliantGroupID("foo.plugin.test"))

        Assert.assertFalse(CommonProjectTypes.PLUGIN.isCompliantPackaging("pom"))
        Assert.assertTrue(CommonProjectTypes.PLUGIN.isCompliantPackaging("maven-plugin"))
        Assert.assertFalse(CommonProjectTypes.PLUGIN.isCompliantPackaging("bundle"))
    }

    @Test
    fun validateApiProjectPatterns() {

        val apiArtifactRegex = Regex(".*-api$", DefaultProjectType.IGNORE_CASE_AND_COMMENTS)
        val result = apiArtifactRegex.matches("api-test")
        Assert.assertFalse("nopes: $result", result)

        // Act & Assert
        Assert.assertTrue(CommonProjectTypes.API.isCompliantArtifactID("test-foo-api"))
        Assert.assertFalse(CommonProjectTypes.API.isCompliantArtifactID("test-api-foo"))
        Assert.assertFalse(CommonProjectTypes.API.isCompliantArtifactID("api-test"))
        Assert.assertFalse(CommonProjectTypes.API.isCompliantArtifactID(null))

        Assert.assertFalse(CommonProjectTypes.API.isCompliantGroupID("test.api.foo"))
        Assert.assertFalse(CommonProjectTypes.API.isCompliantGroupID("api.test"))
        Assert.assertTrue(CommonProjectTypes.API.isCompliantGroupID("test.api"))
        Assert.assertFalse(CommonProjectTypes.API.isCompliantGroupID(null))

        Assert.assertFalse(CommonProjectTypes.API.isCompliantPackaging("pom"))
        Assert.assertTrue(CommonProjectTypes.API.isCompliantPackaging("jar"))
        Assert.assertTrue(CommonProjectTypes.API.isCompliantPackaging("bundle"))
        Assert.assertFalse(CommonProjectTypes.API.isCompliantPackaging(null))
        Assert.assertFalse(CommonProjectTypes.API.isCompliantPackaging("war"))
    }

    @Test
    fun validateSpiProjectPatterns() {

        // Act & Assert
        Assert.assertFalse(CommonProjectTypes.SPI.isCompliantArtifactID("test-foo-spi"))
        Assert.assertTrue(CommonProjectTypes.SPI.isCompliantArtifactID("test-spi-foo"))
        Assert.assertFalse(CommonProjectTypes.SPI.isCompliantArtifactID("spi-test"))

        Assert.assertTrue(CommonProjectTypes.SPI.isCompliantGroupID("test.spi.foo"))
        Assert.assertFalse(CommonProjectTypes.SPI.isCompliantGroupID("spi.test"))
        Assert.assertFalse(CommonProjectTypes.SPI.isCompliantGroupID("test.spi"))

        Assert.assertFalse(CommonProjectTypes.SPI.isCompliantPackaging("pom"))
        Assert.assertTrue(CommonProjectTypes.SPI.isCompliantPackaging("jar"))
        Assert.assertTrue(CommonProjectTypes.SPI.isCompliantPackaging("bundle"))
        Assert.assertFalse(CommonProjectTypes.SPI.isCompliantPackaging("war"))
    }

    @Test
    fun validateImplementationProjectPatterns() {

        // Act & Assert
        Assert.assertFalse(CommonProjectTypes.IMPLEMENTATION.isCompliantArtifactID("test-foo-impl"))
        Assert.assertTrue(CommonProjectTypes.IMPLEMENTATION.isCompliantArtifactID("test-impl-foo"))
        Assert.assertFalse(CommonProjectTypes.IMPLEMENTATION.isCompliantArtifactID("impl-test"))

        Assert.assertTrue(CommonProjectTypes.IMPLEMENTATION.isCompliantGroupID("test.impl.foo"))
        Assert.assertFalse(CommonProjectTypes.IMPLEMENTATION.isCompliantGroupID("impl.test"))
        Assert.assertFalse(CommonProjectTypes.IMPLEMENTATION.isCompliantGroupID("test.impl"))

        Assert.assertFalse(CommonProjectTypes.IMPLEMENTATION.isCompliantPackaging("pom"))
        Assert.assertTrue(CommonProjectTypes.IMPLEMENTATION.isCompliantPackaging("jar"))
        Assert.assertTrue(CommonProjectTypes.IMPLEMENTATION.isCompliantPackaging("bundle"))
        Assert.assertFalse(CommonProjectTypes.IMPLEMENTATION.isCompliantPackaging("war"))
    }

    @Test
    fun validateTestProjectPatterns() {

        // Act & Assert
        Assert.assertFalse(CommonProjectTypes.TEST.isCompliantArtifactID("test-foo-impl"))
        Assert.assertFalse(CommonProjectTypes.TEST.isCompliantArtifactID("test-impl-foo"))
        Assert.assertTrue(CommonProjectTypes.TEST.isCompliantArtifactID("foo-test"))
        Assert.assertTrue(CommonProjectTypes.TEST.isCompliantArtifactID("nazgul-core-osgi-test"))

        Assert.assertFalse(CommonProjectTypes.TEST.isCompliantGroupID("test.foo"))
        Assert.assertFalse(CommonProjectTypes.TEST.isCompliantGroupID("impl.test"))
        Assert.assertTrue(CommonProjectTypes.TEST.isCompliantGroupID("some.test.foo"))
        Assert.assertTrue(CommonProjectTypes.TEST.isCompliantGroupID("se.jguru.nazgul.test.osgi"))

        Assert.assertTrue(CommonProjectTypes.TEST.isCompliantPackaging("bundle"))
        Assert.assertTrue(CommonProjectTypes.TEST.isCompliantPackaging("jar"))
        Assert.assertTrue(CommonProjectTypes.TEST.isCompliantPackaging("war"))
    }

    @Test
    fun validateIntegrationTestProjectPatterns() {

        // Act & Assert
        Assert.assertFalse(CommonProjectTypes.INTEGRATION_TEST.isCompliantArtifactID("it-foo-impl"))
        Assert.assertFalse(CommonProjectTypes.INTEGRATION_TEST.isCompliantArtifactID("test-it-foo"))
        Assert.assertTrue(CommonProjectTypes.INTEGRATION_TEST.isCompliantArtifactID("foo-it"))

        Assert.assertFalse(CommonProjectTypes.INTEGRATION_TEST.isCompliantGroupID("it.foo"))
        Assert.assertFalse(CommonProjectTypes.INTEGRATION_TEST.isCompliantGroupID("impl.it"))
        Assert.assertTrue(CommonProjectTypes.INTEGRATION_TEST.isCompliantGroupID("some.it.foo"))
    }

    @Test
    fun validatePocProjectPatterns() {

        // Act & Assert
        Assert.assertFalse(CommonProjectTypes.PROOF_OF_CONCEPT.isCompliantArtifactID("poc-foo-impl"))
        Assert.assertFalse(CommonProjectTypes.PROOF_OF_CONCEPT.isCompliantArtifactID("test-poc-foo"))
        Assert.assertTrue(CommonProjectTypes.PROOF_OF_CONCEPT.isCompliantArtifactID("foo-poc"))

        Assert.assertFalse(CommonProjectTypes.PROOF_OF_CONCEPT.isCompliantGroupID("poc.foo"))
        Assert.assertFalse(CommonProjectTypes.PROOF_OF_CONCEPT.isCompliantGroupID("impl.poc"))
        Assert.assertTrue(CommonProjectTypes.PROOF_OF_CONCEPT.isCompliantGroupID("some.poc.foo"))
    }

    @Test
    fun validateCodestyleProjectPatterns() {

        // Act & Assert
        Assert.assertFalse(CommonProjectTypes.CODESTYLE.isCompliantArtifactID("codestyle-foo"))
        Assert.assertTrue(CommonProjectTypes.CODESTYLE.isCompliantArtifactID("foo-codestyle"))

        Assert.assertFalse(CommonProjectTypes.CODESTYLE.isCompliantGroupID("codestyle.foo"))
        Assert.assertTrue(CommonProjectTypes.CODESTYLE.isCompliantGroupID("foo.codestyle"))
    }

    @Test
    fun validateExampleProjectPatterns() {

        // Act & Assert
        Assert.assertTrue(CommonProjectTypes.EXAMPLE.isCompliantArtifactID("codestyle-foo-example"))
        Assert.assertFalse(CommonProjectTypes.EXAMPLE.isCompliantArtifactID("example-codestyle"))

        Assert.assertTrue(CommonProjectTypes.EXAMPLE.isCompliantGroupID("codestyle.foo.example"))
        Assert.assertFalse(CommonProjectTypes.EXAMPLE.isCompliantGroupID("foo.example.codestyle"))
    }
}