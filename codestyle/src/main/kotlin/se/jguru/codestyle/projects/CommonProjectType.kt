/*
 * Copyright (c) jGuru Europe AB.
 * All rights reserved.
 */

package se.jguru.codestyle.projects

import org.apache.maven.artifact.Artifact
import org.apache.maven.model.Dependency
import org.apache.maven.project.MavenProject

/**
 * Enumeration of well-known Maven project types, each defined by regex patterns for
 * artifactId, groupId, and packaging, plus an optional internal-structure checker.
 *
 * Use [getProjectType] to classify a [MavenProject] or [Artifact] against this catalogue.
 *
 * @param artifactIdPattern Regex that the project's artifactId must match, or `null` to match any.
 * @param groupIdPattern Regex that the project's groupId must match, or `null` to match any.
 * @param packagingPattern Regex that the project's packaging must match, or `null` to match any.
 * @param acceptNullValues If `true`, a `null` input for any checked field is treated as compliant.
 * @param structureChecker Lambda for extra structural validation beyond GAV pattern matching.
 * Returns a non-compliance reason, or `null` when the project is compliant.
 *
 * @author [Lennart Jörelid](mailto:lj@jguru.se), jGuru Europe AB
 */
enum class CommonProjectType(
    artifactIdPattern: String?,
    groupIdPattern: String?,
    packagingPattern: String?,
    acceptNullValues: Boolean = true,
    structureChecker: (MavenProject, List<Regex>?) -> String? = { _, _ -> null }
) : ProjectType {

    /**
     * Reactor project (`pom` packaging). Holds only module references — no dependencies
     * or dependency-management entries.
     */
    REACTOR(".*-reactor$", null, "pom", false),

    /**
     * Parent POM project (`pom` packaging). Defines shared dependencies and build lifecycle.
     * Must not contain module definitions.
     */
    PARENT(".*-parent$", null, "pom", false),

    /**
     * Bill-of-Materials project (`pom` packaging). Defines only `<dependencyManagement>` entries.
     * Must not contain direct dependency definitions or module definitions.
     */
    BILL_OF_MATERIALS(".*-bom$", null, "pom", false, { project, dontEvaluateGroupIds ->

        val containsNoModules = project.modules.isNullOrEmpty()
        val parentDependencies = try {
            project.parent?.dependencies ?: emptySet<Dependency>()
        } catch (_: Exception) {
            emptySet<Dependency>()
        }

        val onlyOwnDependencies = project.dependencies
            .filter { ownDep ->
                parentDependencies.none { ProjectType.DEPENDENCY_COMPARATOR.compare(it, ownDep) == 0 }
            }
            .filter { ownDep ->
                dontEvaluateGroupIds.isNullOrEmpty() || !dontEvaluateGroupIds.any { it.matches(ownDep.groupId) }
            }

        if (containsNoModules && onlyOwnDependencies.isEmpty()) null
        else "BILL_OF_MATERIALS projects should not contain Dependency definitions — only " +
            "DependencyManagement definitions. (Found: $onlyOwnDependencies)."
    }),

    /**
     * Assembly/aggregation project (`pom` packaging). Must not contain module definitions.
     */
    ASSEMBLY(".*-assembly$", null, "pom"),

    /**
     * Aspect definition project (`bundle` or `jar` packaging).
     * Holds publicly available AspectJ aspect implementations.
     */
    ASPECT(".*-aspect$", ".*\\.aspect$", "bundle|jar", false),

    /**
     * Model project (`bundle` or `jar` packaging). Defines domain entities.
     * May have test-scope dependencies on test and proof-of-concept projects.
     */
    MODEL(".*-model$", ".*\\.model$", "bundle|jar"),

    /**
     * JEE application project (`war`, `ear`, or `ejb` packaging). May inject implementation
     * projects as dependencies.
     */
    JEE_APPLICATION(null, null, "war|ear|ejb", false),

    /**
     * Microservice project (`bundle` or `jar` packaging). A runnable Java application.
     * May inject implementation projects as dependencies.
     */
    MICROSERVICE(".*-service$", ".*\\.service$", "bundle|jar", false),

    /**
     * Standalone application project (`bundle` or `jar` packaging). A runnable Java application.
     * May inject implementation projects as dependencies.
     */
    STANDALONE_APPLICATION(".*-application$", ".*\\.application$", "bundle|jar", false),

    /**
     * Example project. Provides runnable sample code and cut-and-paste documentation.
     * No dependency rules apply.
     */
    EXAMPLE(".*-example$", ".*\\.example$", null),

    /**
     * JVM agent project (`bundle` or `jar` packaging). Launched via
     * `-javaagent:/path/to/agent.jar=key=value`. May inject implementation dependencies as it is
     * an application entry-point.
     */
    JAVA_AGENT(".*-agent$", ".*\\.agent$", "bundle|jar"),

    /**
     * API project (`bundle` or `jar` packaging). Defines service interactions, abstract
     * implementations, and exceptions. May have compile-scope dependencies on model projects
     * within the same component.
     */
    API(".*-api$", ".*\\.api$", "bundle|jar", false),

    /**
     * SPI project (`bundle` or `jar` packaging). Defines service-provider contracts.
     * Must have compile-scope dependencies to API projects within the same component.
     */
    SPI(".*-spi-\\w*$", ".*\\.spi\\.\\w*$", "bundle|jar", false),

    /**
     * Implementation project (`bundle` or `jar` packaging). Implements API/SPI contracts,
     * including third-party library dependencies. Must have compile-scope dependencies to API
     * or SPI projects within the same component.
     */
    IMPLEMENTATION(".*-impl-\\w*$", ".*\\.impl\\.\\w*$", "bundle|jar", false),

    /**
     * Test-helper project. Provides libraries that facilitate testing in other projects.
     * No dependency rules apply.
     */
    TEST(".*-test$", ".*\\.test\\.\\w*$", null),

    /**
     * Integration-test project. Used to run automated tests spanning several modules.
     * No dependency rules apply.
     */
    INTEGRATION_TEST(".*-it$", ".*\\.it\\.\\w*$", null),

    /**
     * Codestyle helper project (`jar` or `bundle` packaging). Supplies build-time resources such
     * as `checkstyle.xml` configurations or custom enforcer rule implementations.
     * No dependency rules apply.
     */
    CODESTYLE(".*-codestyle$", ".*\\.codestyle$", "jar|bundle"),

    /**
     * Maven plugin project (`maven-plugin` packaging).
     */
    PLUGIN(".*-maven-plugin$", null, "maven-plugin"),

    /**
     * Proof-of-concept project. Holds exploratory implementations.
     * No dependency rules apply.
     */
    PROOF_OF_CONCEPT(".*-poc$", ".*\\.poc\\.\\w*$", null);

    // Internal state — delegate all pattern matching to DefaultProjectType.
    private val delegate: DefaultProjectType = DefaultProjectType(
        groupIdPattern,
        artifactIdPattern,
        packagingPattern,
        acceptNullValues,
        name,
        structureChecker
    )

    override fun artifactIDNonComplianceMessage(artifactID: String?): String? =
        delegate.artifactIDNonComplianceMessage(artifactID)

    override fun groupIDNonComplianceMessage(groupID: String?): String? =
        delegate.groupIDNonComplianceMessage(groupID)

    override fun packagingNonComplianceMessage(packaging: String?): String? =
        delegate.packagingNonComplianceMessage(packaging)

    override fun internalStructureNonComplianceMessage(
        project: MavenProject?,
        dontEvaluateGroupIds: List<Regex>?
    ): String? = delegate.internalStructureNonComplianceMessage(project, dontEvaluateGroupIds)

    override fun toString(): String = "CommonProjectType.$name"

    override fun getIdentifier(): String = name

    /**
     * Extended compliance check that adds type-specific structural rules on top of the standard
     * GAV pattern matching. Handles the distinct semantics of [BILL_OF_MATERIALS], [REACTOR],
     * [PARENT], and [ASSEMBLY] regarding modules and dependency declarations.
     *
     * @param project A Maven project to validate.
     * @param dontEvaluateGroupIds Optional patterns for groupIds to exclude from evaluation.
     * @return A [ComplianceStatusHolder] describing the compliance result.
     */
    override fun getComplianceStatus(project: MavenProject, dontEvaluateGroupIds: List<Regex>?): ComplianceStatusHolder {

        val standardCompliance = super.getComplianceStatus(project, dontEvaluateGroupIds)
        if (!standardCompliance.isCompliant) return standardCompliance

        // Returns true when depList is non-null, non-empty, and contains at least one dependency
        // whose groupId is not excluded by the supplied patterns.
        fun containsEvaluatedDeps(depList: List<Dependency>?): Boolean =
            !depList.isNullOrEmpty() && depList.any { dep ->
                dontEvaluateGroupIds.isNullOrEmpty() || !dontEvaluateGroupIds.any { it.matches(dep.groupId) }
            }

        return when (this) {

            BILL_OF_MATERIALS -> {
                val parentDeps = project.parent?.dependencies ?: emptySet<Dependency>()
                val ownDeps = project.dependencies
                    .filter { ownDep -> parentDeps.none { ProjectType.DEPENDENCY_COMPARATOR.compare(it, ownDep) == 0 } }
                    .filter { ownDep ->
                        dontEvaluateGroupIds.isNullOrEmpty() || !dontEvaluateGroupIds.any { it.matches(ownDep.groupId) }
                    }

                if (ownDeps.isEmpty()) ComplianceStatusHolder.OK
                else ComplianceStatusHolder(
                    internalStructureComplianceFailure = "BILL_OF_MATERIALS projects should not contain " +
                        "Dependency definitions — only DependencyManagement definitions. (Found: $ownDeps)."
                )
            }

            REACTOR -> {
                val hasDeps = containsEvaluatedDeps(project.dependencies)
                val hasMgmt = containsEvaluatedDeps(project.dependencyManagement?.dependencies)
                if (hasDeps || hasMgmt)
                    ComplianceStatusHolder(
                        internalStructureComplianceFailure = "REACTOR projects should not contain " +
                            "Dependency or DependencyManagement — only Modules."
                    )
                else ComplianceStatusHolder.OK
            }

            PARENT, ASSEMBLY ->
                if (project.modules.isNullOrEmpty()) ComplianceStatusHolder.OK
                else ComplianceStatusHolder(
                    internalStructureComplianceFailure = "$name projects should not contain Modules (Child Projects)."
                )

            else -> ComplianceStatusHolder.OK
        }
    }

    companion object {

        /**
         * Returns the [CommonProjectType] matching [anArtifact]'s GAV coordinates.
         *
         * @throws IllegalArgumentException if [anArtifact] matches zero or more than one type.
         */
        @Throws(IllegalArgumentException::class)
        fun getProjectType(anArtifact: Artifact): CommonProjectType {

            val matches = CommonProjectType.entries.filter {
                it.artifactIDNonComplianceMessage(anArtifact.artifactId) == null &&
                    it.groupIDNonComplianceMessage(anArtifact.groupId) == null &&
                    it.packagingNonComplianceMessage(anArtifact.type) == null
            }

            val errorPrefix = "Incorrect Artifact type definition for " +
                "[${anArtifact.groupId} :: ${anArtifact.artifactId} :: ${anArtifact.version}]: "

            return when {
                matches.isEmpty() -> throw IllegalArgumentException("$errorPrefix Not matching any CommonProjectTypes.")
                matches.size > 1 -> throw IllegalArgumentException("$errorPrefix Matching several project types ($matches).")
                else -> matches[0]
            }
        }

        /**
         * Returns the [CommonProjectType] matching [project]'s GAV coordinates and internal structure.
         *
         * @throws IllegalArgumentException if [project] matches zero or more than one type, or if its
         * internal structure violates the matched type's rules.
         */
        @Throws(IllegalArgumentException::class)
        fun getProjectType(project: MavenProject): CommonProjectType {

            val matches = CommonProjectType.entries.filter {
                it.artifactIDNonComplianceMessage(project.artifactId) == null &&
                    it.groupIDNonComplianceMessage(project.groupId) == null &&
                    it.packagingNonComplianceMessage(project.packaging) == null &&
                    it.internalStructureNonComplianceMessage(project) == null
            }

            val errorPrefix = "Incorrect project type definition for " +
                "[${project.groupId} :: ${project.artifactId} :: ${project.version}]: "

            if (matches.isEmpty()) throw IllegalArgumentException("$errorPrefix Not matching any CommonProjectTypes.")
            if (matches.size > 1) throw IllegalArgumentException("$errorPrefix Matching several project types ($matches).")

            fun hasDeps(depList: List<Dependency>?): Boolean = !depList.isNullOrEmpty()

            val toReturn = matches[0]
            when (toReturn) {

                PARENT, ASSEMBLY ->
                    if (!project.modules.isNullOrEmpty())
                        throw IllegalArgumentException(
                            "${toReturn.name} projects may not contain module definitions. " +
                                "(Modules are reserved for reactor projects)."
                        )

                BILL_OF_MATERIALS ->
                    if (!BILL_OF_MATERIALS.getComplianceStatus(project).isCompliant)
                        throw IllegalArgumentException(
                            "${toReturn.name} projects may not contain dependency definitions. " +
                                "(Bill-of-Material projects should only contain DependencyManagement definitions)."
                        )

                REACTOR -> {
                    val errorText = "${toReturn.name} projects may not contain dependency [incl. Management] " +
                        "definitions. (Dependencies should be defined within parent projects)."
                    if (hasDeps(project.dependencies) ||
                        (project.dependencyManagement != null && hasDeps(project.dependencyManagement.dependencies))
                    ) throw IllegalArgumentException(errorText)
                }

                else -> {
                    val complianceStatus = toReturn.getComplianceStatus(project)
                    if (!complianceStatus.isCompliant) throw IllegalArgumentException(complianceStatus.toString())
                }
            }

            return toReturn
        }
    }
}
