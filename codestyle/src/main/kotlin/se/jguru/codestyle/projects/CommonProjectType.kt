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
 * @param artifactIdPattern The [java.util.regex.Pattern] which must match the artifactID of a Maven POM for this
 * CommonProjectType to be valid.
 * @param groupIdPattern The [java.util.regex.Pattern] which must match the groupID of a Maven POM for this
 * CommonProjectType to be valid.
 * @param packagingPattern The [java.util.regex.Pattern] which must match the packaging of a Maven POM for this
 * CommonProjectType to be valid.
 * @param acceptNullValues if true, this CommonProjectTypes value can accept null values within corresponding Maven POM
 *
 * @author [Lennart Jörelid](mailto:lj@jguru.se), jGuru Europe AB
 */
enum class CommonProjectType(
    artifactIdPattern: String?,
    groupIdPattern: String?,
    packagingPattern: String?,
    acceptNullValues: Boolean = true,
    structureChecker: (MavenProject, List<Regex>?) -> String? = { _, _ -> null }) : ProjectType {

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
     * Bill-of-Materials project, of type pom, defining DependencyManagement entries.
     * May not contain module definitions.
     */
    BILL_OF_MATERIALS(".*-bom$", null, "pom", false, {
        project : MavenProject, dontEvaluateGroupIds : List<Regex>? ->

        val containsNoModules = project.modules.isNullOrEmpty()
        val parentProject = try {
            project.parent
        } catch (e: Exception) {
            null
        }

        // Find the immediate dependencies of this project.
        val parentDependencies = when (parentProject == null) {
            true -> mutableSetOf<Dependency>()
            else -> parentProject.dependencies
        }


        val onlyOwnDependencies = project.dependencies
            .filter { ownDependency -> parentDependencies
                .firstOrNull { ProjectType.DEPENDENCY_COMPARATOR.compare(it, ownDependency) == 0 } == null
            }
            .filter { ownDependency ->

                when (dontEvaluateGroupIds != null && dontEvaluateGroupIds.isNotEmpty()) {

                    // Nothing to filter
                    false -> true

                    // Pass through dependencies that do *not* match the supplied ignore patterns
                    else -> !dontEvaluateGroupIds.any { it.matches(ownDependency.groupId) }
                }
            }

        // All Done.
        when (containsNoModules && onlyOwnDependencies.isNullOrEmpty()) {
            true -> null
            else -> "BILL_OF_MATERIALS " +
                "projects should not contain Dependency definitions - only " +
                "DependencyManagement definitions. (Found: $onlyOwnDependencies)."
        }
    }),

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
     * (Micro)Service project defining runnable Java applications.
     * Injections of implementation projects are permitted here.
     */
    MICROSERVICE(".*-service$", ".*\\.service$", "bundle|jar", false),

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
        acceptNullValues,
        name,
        structureChecker)

    constructor(
        artifactIdPattern: String?,
        groupIdPattern: String?,
        packagingPattern: String?,
        acceptNullValues: Boolean = true) : this(artifactIdPattern,
                                                 groupIdPattern,
                                                 packagingPattern,
                                                 acceptNullValues,
                                                 { _, _ -> null })

    override fun artifactIDNonComplianceMessage(artifactID: String?): String? =
        delegate.artifactIDNonComplianceMessage(artifactID)

    override fun groupIDNonComplianceMessage(groupID: String?): String? = delegate.groupIDNonComplianceMessage(groupID)

    override fun packagingNonComplianceMessage(packaging: String?): String? =
        delegate.packagingNonComplianceMessage(packaging)

    override fun internalStructureNonComplianceMessage(project: MavenProject?, dontEvaluateGroupIds: List<Regex>?): String? =
        delegate.internalStructureNonComplianceMessage(project, dontEvaluateGroupIds)

    override fun toString(): String = "CommonProjectType.$name"

    override fun getIdentifier(): String = name

    companion object {

        /**
         * Acquires the ProjectType instance for the provided internal Artifact, or throws an
         * IllegalArgumentException holding an exception message if a ProjectType could not be
         * found for the provided [Artifact].
         *
         * @param anArtifact The Maven Artifact for which a [CommonProjectType] object should be retrieved.
         * @return The [ProjectType] corresponding to the given [Artifact].
         * @throws IllegalArgumentException if [anArtifact] did not match any [CommonProjectType]
         */
        @Throws(IllegalArgumentException::class)
        fun getProjectType(anArtifact: Artifact): CommonProjectType {

            val matches = CommonProjectType
                .values()
                .filter {
                    it.artifactIDNonComplianceMessage(anArtifact.artifactId) == null &&
                        it.groupIDNonComplianceMessage(anArtifact.groupId) == null &&
                        it.packagingNonComplianceMessage(anArtifact.type) == null
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
        fun getProjectType(project: MavenProject): CommonProjectType {

            val matches = CommonProjectType
                .values()
                .filter {
                    it.artifactIDNonComplianceMessage(project.artifactId) == null &&
                        it.groupIDNonComplianceMessage(project.groupId) == null &&
                        it.packagingNonComplianceMessage(project.packaging) == null &&
                        it.internalStructureNonComplianceMessage(project) == null
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

            fun containsElements(depList: List<Dependency>?): Boolean = depList != null && !depList.isEmpty()

            // Validate the internal requirements for the two different pom projects.
            val toReturn = matches[0]
            when (toReturn) {

                CommonProjectType.PARENT, CommonProjectType.ASSEMBLY ->

                    // This project should not contain modules.
                    if (project.modules != null && !project.modules.isEmpty()) {
                        throw IllegalArgumentException("${toReturn.name} projects may not contain " +
                                                           "module definitions. (Modules are reserved for reactor projects).")
                    }

                CommonProjectType.BILL_OF_MATERIALS -> {

                    val errorText = "${toReturn.name} projects may not contain dependency definitions. " +
                        "(Bill-of-Material projects should only contain DependencyManagement definitions)."

                    // This project should not contain Own Dependency definitions.
                    if (!CommonProjectType.BILL_OF_MATERIALS.getComplianceStatus(project).isCompliant) {
                        throw IllegalArgumentException(errorText)
                    }
                }

                CommonProjectType.REACTOR -> {

                    val errorText = "${toReturn.name} projects may not contain " +
                        "dependency [incl. Management] definitions. (Dependencies should be defined " +
                        "within parent projects)."

                    // This project should not contain Dependency definitions.
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

                    // Fallback to standard compliance handling.
                    val complianceStatus = toReturn.getComplianceStatus(project)
                    if (!complianceStatus.isCompliant) {
                        throw IllegalArgumentException(complianceStatus.toString())
                    }
                }
            }

            // All done.
            return toReturn
        }
    }

    /**
     * Special handling to separate BILL_OF_MATERIALS, PARENT, REACTOR and ASSEMBLY pom types.
     *
     * @param project A Maven Project to validate for compliance with this [ProjectType].
     * @param dontEvaluateGroupIds Optional list of patterns fot groupIDs to ignore in evaluation.
     *
     * @return A [ComplianceStatusHolder] containing information about compliance status or causes
     * to lack of such compliance.
     */
    override fun getComplianceStatus(project: MavenProject, dontEvaluateGroupIds: List<Regex>?): ComplianceStatusHolder {

        // First, check standard compliance.
        val standardCompliance = super.getComplianceStatus(project, dontEvaluateGroupIds)

        fun containsElements(depList: List<Dependency>?): Boolean = depList != null
            && depList.isNotEmpty()
            && depList.any { aDependency ->
            when (dontEvaluateGroupIds != null && dontEvaluateGroupIds.isNotEmpty()) {

                // Nothing to process; all dependencies should be evaluated
                false -> true

                // Pass through dependencies that do *not* match the supplied ignore patterns
                else -> !dontEvaluateGroupIds.any { it.matches(aDependency.groupId) }
            }
        }

        // All Done.
        return when (standardCompliance.isCompliant) {
            false -> standardCompliance
            else -> when (this) {

                BILL_OF_MATERIALS -> {

                    // Find the immediate dependencies of this project.
                    val parentDependencies = when (project.parent == null) {
                        true -> mutableSetOf<Dependency>()
                        else -> project.parent.dependencies
                    }

                    val onlyOwnDependencies = project.dependencies
                        .filter { ownDependency ->
                            parentDependencies.firstOrNull {
                                ProjectType.DEPENDENCY_COMPARATOR.compare(it, ownDependency) == 0
                            } == null
                        }
                        .filter { ownDependency ->

                            when (dontEvaluateGroupIds != null && dontEvaluateGroupIds.isNotEmpty()) {

                                // Nothing to filter
                                false -> true

                                // Pass through dependencies that do *not* match the supplied ignore patterns
                                else -> !dontEvaluateGroupIds.any { it.matches(ownDependency.groupId) }
                            }
                        }

                    when (containsElements(onlyOwnDependencies)) {
                        true -> ComplianceStatusHolder(internalStructureComplianceFailure = "BILL_OF_MATERIALS " +
                            "projects should not contain Dependency definitions - only " +
                            "DependencyManagement definitions. (Found: $onlyOwnDependencies).")
                        false -> ComplianceStatusHolder.OK
                    }
                }

                REACTOR -> {

                    val hasDependencies = containsElements(project.dependencies)
                    val hasDependencyManagementEntries = containsElements(project.dependencyManagement.dependencies)

                    when (hasDependencies || hasDependencyManagementEntries) {
                        true -> ComplianceStatusHolder(internalStructureComplianceFailure = "REACTOR " +
                            "projects should not contain Dependency or DependencyManagement - only Modules")
                        else -> ComplianceStatusHolder.OK
                    }
                }

                PARENT, ASSEMBLY -> {

                    // This project should not contain modules.
                    when (project.modules.isNullOrEmpty()) {
                        true -> ComplianceStatusHolder.OK
                        else -> ComplianceStatusHolder(internalStructureComplianceFailure = "$name projects " +
                            "should not contain Modules (Child Projects)")
                    }
                }
                else -> ComplianceStatusHolder.OK
            }
        }
    }
}