/*
 * Copyright (c) jGuru Europe AB.
 * All rights reserved.
 */

package se.jguru.codestyle.projects

import org.apache.maven.artifact.Artifact

/**
 * Commonly known and used ProjectTypes, collected within an enum.
 *
 * @author <a href="mailto:lj@jguru.se">Lennart J&ouml;relid</a>, jGuru Europe AB
 */
enum class CommonProjectTypes(groupIdPattern: String?,
                              artifactIdPattern: String?,
                              packagingPattern: String?,
                              acceptNullValues: Boolean = true) : ProjectType {

    /**
     * Reactor project, of type pom. May not contain anything except module definitions.
     */
    REACTOR(".*-reactor$", null, "pom"),

    /**
     * Parent pom project, of type pom, defining dependencies and/or build
     * life cycles. May not contain module definitions.
     */
    PARENT(".*-parent$", null, "pom"),

    /**
     * Pom project, defining assemblies and/or aggregation projects. May not contain module definitions.
     */
    ASSEMBLY(".*-assembly$", null, "pom"),

    /**
     * Aspect definition project, holding publicly available aspects.
     */
    ASPECT(".*-aspect$", ".*\\.aspect$", "bundle|jar"),

    /**s
     * Model project defining entities. May have test-scope dependencies on test and proof-of-concept projects.
     */
    MODEL(".*-model$", ".*\\.model$", "bundle|jar"),

    /**
     * Application project defining JEE-deployable artifacts.
     * Injections of implementation projects are permitted here.
     */
    JEE_APPLICATION(null, null, "war|ear|ejb"),

    /**
     * Standalone application project defining runnable Java applications.
     * Injections of implementation projects are permitted here.
     */
    STANDALONE_APPLICATION(".*-application$", ".*\\.application$", "bundle|jar"),

    /**
     * Example project providing runnable example code for showing the
     * typical scenarios of the component. Should contain relevant documentation
     * as well as cut-and-paste code. No dependency rules.
     */
    EXAMPLE(".*-example$", ".*\\.example$", null),

    /**
     * "javaagent" definition project, holding implementation of a
     * JVM agent to be launched in-process on the form
     * "-javaagent:[yourpath/][agentjar].jar=[option1]=[value1],[option2]=[value2]"
     *
     * This project type can import/inject implementation dependencies, as
     * it is considered an application entrypoint.
     */
    JAVA_AGENT(".*-agent$", ".*\\.agent$", "bundle|jar"),

    /**
     * API project, defining service interaction, abstract implementations and exceptions. May have compile-scope
     * dependencies on model projects within the same component, and test-scope dependencies on test and
     * proof-of-concept projects.
     */
    API(".*-api$", ".*\\.api$", "bundle|jar"),

    /**
     * SPI project, defining service interaction, abstract implementations and exceptions. Must have compile-scope
     * dependencies to API projects within the same component. May have test-scope dependencies on test and
     * proof-of-concept projects.
     */
    SPI(".*-spi-\\w*$", ".*\\.spi\\.\\w*$", "bundle|jar"),

    /**
     * Implementation project, implementing service interactions from an API or SPI project,
     * including dependencies on 3rd party libraries. Must have compile-scope dependencies to API or SPI projects
     * within the same component. May have test-scope dependencies on test and proof-of-concept projects.
     */
    IMPLEMENTATION(".*-impl-\\w*$", ".*\\.impl\\.\\w*$", "bundle|jar"),

    /**
     * Test artifact helper project, implementing libraries facilitating testing within
     * other projects. No dependency rules.
     */
    TEST(".*-test$", ".*\\.test\\.\\w*$", null),

    /**
     * Integration test artifact helper project, used to perform automated
     * tests for several projects. No dependency rules.
     */
    INTEGRATION_TEST(".*-it$", ".*\\.it\\.\\w*$", null),

    /**
     * Codestyle helper project, providing implementations for use within the build definition cycle.
     * Typically used within local reactors to supply changes or augmentations to build configurations
     * such as `checkstyle.xml`, or custom enforcer rule implementations. No dependency rules.
     */
    CODESTYLE(".*-codestyle$", ".*\\.codestyle$", "jar|bundle"),

    /**
     * Project, defining a Maven plugin.
     */
    PLUGIN(".*-maven-plugin$", null, "maven-plugin"),

    /**
     * Proof-of-concept helper project, holding proof of concept implementations. No dependency rules.
     */
    PROOF_OF_CONCEPT(".*-poc$", ".*\\.poc\\.\\w*$", null);

    // Internal state
    private val delegate: DefaultProjectType = DefaultProjectType(
            groupIdPattern,
            artifactIdPattern,
            packagingPattern,
            acceptNullValues)

    override fun isCompliantArtifactID(artifactID: String?): Boolean = delegate.isCompliantArtifactID(artifactID)

    override fun isCompliantGroupID(groupID: String?): Boolean = delegate.isCompliantGroupID(groupID)

    override fun isCompliantPackaging(packaging: String?): Boolean = delegate.isCompliantPackaging(packaging)

    companion object {

        /**
         * Acquires the ProjectType instance for the provided internal Artifact, or throws an
         * IllegalArgumentException holding an exception message if a ProjectType could not be
         * found for the provided [Artifact].
         *
         * @param anArtifact The Maven Artifact for which a [CommonProjectTypes] object should be retrieved.
         * @return The [ProjectType] corresponding to the given [Artifact].
         * @throws IllegalArgumentException if [anArtifact] did not match any [CommonProjectTypes]
         */
        @Throws(IllegalArgumentException::class)
        fun getProjectType(anArtifact: Artifact): ProjectType {

            val matches = CommonProjectTypes
                    .values()
                    .filter {
                        it.isCompliantArtifactID(anArtifact.artifactId) &&
                                it.isCompliantGroupID(anArtifact.groupId) &&
                                it.isCompliantPackaging(anArtifact.type)
                    }

            val errorPrefix = "Incorrect Artifact type definition for [${anArtifact.groupId} :: " +
                    "${anArtifact.artifactId} :: ${anArtifact.version} ]: "

            // Check sanity
            if (matches.isEmpty()) {
                throw IllegalArgumentException("$errorPrefix Not matching any CommonProjectTypes.")
            }
            if (matches.size > 1) {
                throw IllegalArgumentException("$errorPrefix Matching several project types ($matches).")
            }

            // All done.
            return matches[0]
        }
    }
}