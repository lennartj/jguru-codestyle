/*
 * Copyright (c) jGuru Europe AB.
 * All rights reserved.
 */

package se.jguru.codestyle.projects

import org.apache.maven.artifact.Artifact
import org.apache.maven.model.Dependency
import org.apache.maven.project.MavenProject

/**
 * Commonly known and used ProjectTypes, collected within an enum.
 *
 * @author [Lennart Jörelid](mailto:lj@jguru.se), jGuru Europe AB
 */
enum class CommonProjectTypes(artifactIdPattern: String?,
                              groupIdPattern: String?,
                              packagingPattern: String?,
                              acceptNullValues: Boolean = true) : ProjectType {

    /**
     * Reactor project, of type pom. May not contain anything except module definitions.
     */
    REACTOR(".*-reactor$", null, "pom", false),

    /**
     * Parent pom project, of type pom, defining dependencies and/or build
     * life cycles. May not contain module definitions.
     */
    PARENT(".*-parent$", null, "pom", false),

    /**
     * Pom project, defining assemblies and/or aggregation projects. May not contain module definitions.
     */
    ASSEMBLY(".*-assembly$", null, "pom"),

    /**
     * Aspect definition project, holding publicly available aspect implementations.
     */
    ASPECT(".*-aspect$", ".*\\.aspect$", "bundle|jar", false),

    /**
     * Model project defining entities. May have test-scope dependencies on test and proof-of-concept projects.
     */
    MODEL(".*-model$", ".*\\.model$", "bundle|jar"),

    /**
     * Application project defining JEE-deployable artifacts.
     * Injections of implementation projects are permitted here.
     */
    JEE_APPLICATION(null, null, "war|ear|ejb", false),

    /**
     * Standalone application project defining runnable Java applications.
     * Injections of implementation projects are permitted here.
     */
    STANDALONE_APPLICATION(".*-application$", ".*\\.application$", "bundle|jar", false),

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
    API(".*-api$", ".*\\.api$", "bundle|jar", false),

    /**
     * SPI project, defining service interaction, abstract implementations and exceptions. Must have compile-scope
     * dependencies to API projects within the same component. May have test-scope dependencies on test and
     * proof-of-concept projects.
     */
    SPI(".*-spi-\\w*$", ".*\\.spi\\.\\w*$", "bundle|jar", false),

    /**
     * Implementation project, implementing service interactions from an API or SPI project,
     * including dependencies on 3rd party libraries. Must have compile-scope dependencies to API or SPI projects
     * within the same component. May have test-scope dependencies on test and proof-of-concept projects.
     */
    IMPLEMENTATION(".*-impl-\\w*$", ".*\\.impl\\.\\w*$", "bundle|jar", false),

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

    override fun toString(): String = "CommonProjectType.$name"

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
        fun getProjectType(anArtifact: Artifact): CommonProjectTypes {

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

        /**
         * Acquires the ProjectType instance for the provided MavenProject,
         * or throws an IllegalArgumentException holding an exception message
         * if a ProjectType could not be found for the provided MavenProject.
         *
         * @param project The MavenProject to classify.
         * @return The corresponding ProjectType.
         * @throws IllegalArgumentException if the given project could not be mapped to a [single] ProjectType.
         * The exception message holds
         */
        @Throws(IllegalArgumentException::class)
        fun getProjectType(project: MavenProject): CommonProjectTypes {

            val matches = CommonProjectTypes
                .values()
                .filter {
                    it.isCompliantArtifactID(project.artifactId) &&
                        it.isCompliantGroupID(project.groupId) &&
                        it.isCompliantPackaging(project.packaging)
                }

            val errorPrefix = "Incorrect project type definition for [${project.groupId} " +
                ":: ${project.artifactId} :: ${project.version}]: "

            // Check sanity
            if (matches.isEmpty()) {
                throw IllegalArgumentException("$errorPrefix Not matching any CommonProjectTypes.")
            }
            if (matches.size > 1) {
                throw IllegalArgumentException("$errorPrefix Matching several project types ($matches).")
            }

            // Validate the internal requirements for the two different pom projects.
            val toReturn = matches[0]
            when (toReturn) {

                PARENT, ASSEMBLY ->

                    // This project should not contain modules.
                    if (project.modules != null && !project.modules.isEmpty()) {
                        throw IllegalArgumentException("${CommonProjectTypes.PARENT.name} projects may not contain " +
                            "module definitions. (Modules are reserved for reactor projects).")
                    }

                REACTOR -> {

                    val errorText = "${CommonProjectTypes.REACTOR.name} projects may not contain " +
                        "dependency [incl. Management] definitions. (Dependencies should be defined " +
                        "within parent projects)."

                    fun containsElements(depList: List<Dependency>?): Boolean = depList != null && !depList.isEmpty()

                    // This project not contain Dependency definitions.
                    if (containsElements(project.dependencies)) {
                        throw IllegalArgumentException(errorText)
                    }

                    // This kind of project should not contain DependencyManagement definitions.
                    val dependencyManagement = project.dependencyManagement
                    if (dependencyManagement != null && containsElements(dependencyManagement.dependencies)) {
                        throw IllegalArgumentException(errorText)
                    }
                }

                else -> {

                    // Do nothing:
                    // No action should be taken for other project types.
                }
            }

            // All done.
            return toReturn
        }
    }

    /**
     * Special handling to separate PARENT, REACTOR and ASSEMBLY pom types.
     */
    override fun isCompliantWith(project: MavenProject): Boolean {

        // First, check standard compliance.
        val standardCompliance = super.isCompliantWith(project)

        // All Done.
        return standardCompliance && when (this) {

            REACTOR -> {

                fun containsElements(depList: List<Dependency>?): Boolean = depList != null && !depList.isEmpty()

                // This project not contain Dependency definitions.
                val hasNoDependencies = !containsElements(project.dependencies)

                // This kind of project should not contain DependencyManagement definitions.
                val dependencyManagement = project.dependencyManagement
                val hasNoManagementDependencies = dependencyManagement == null
                    || !containsElements(dependencyManagement.dependencies)

                // All Done.
                hasNoDependencies && hasNoManagementDependencies
            }
            PARENT, ASSEMBLY -> {

                // This project should not contain modules.
                project.modules.isEmpty()
            }
            else -> true
        }
    }
}