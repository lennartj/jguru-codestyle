/*
 * Copyright (c) jGuru Europe AB.
 * All rights reserved.
 */

package se.jguru.codestyle.projects.enforcer

import org.apache.maven.enforcer.rule.api.AbstractEnforcerRule
import org.apache.maven.enforcer.rule.api.EnforcerLevel
import org.apache.maven.enforcer.rule.api.EnforcerRuleException
import org.apache.maven.project.MavenProject
import java.util.regex.Pattern
import java.util.regex.PatternSyntaxException
import javax.inject.Inject

/**
 * Abstract base for jGuru Codestyle Maven enforcer rules.
 *
 * Handles the boilerplate of catching [RuleFailureException] from [performValidation] and
 * re-throwing it as a formatted [EnforcerRuleException] that includes project coordinates and
 * the optional offending artifact.
 *
 * @param enforcerLevel The enforcement level reported to the Maven enforcer plugin.
 * Defaults to [EnforcerLevel.ERROR].
 *
 * @author [Lennart Jörelid](mailto:lj@jguru.se), jGuru Europe AB
 */
abstract class AbstractSimplifiedEnforcerRule @JvmOverloads constructor(
    protected open val enforcerLevel: EnforcerLevel = EnforcerLevel.ERROR
) : AbstractEnforcerRule() {

    /** Whether results of this rule may be cached across Maven module executions. */
    @Suppress("MemberVisibilityCanBePrivate")
    protected var cacheable = false

    @set:Inject
    lateinit var project: MavenProject

    override fun getLevel(): EnforcerLevel = enforcerLevel

    /**
     * Entry point called by the Maven enforcer plugin. Delegates to [performValidation] and
     * converts any [RuleFailureException] into a pretty-printed [EnforcerRuleException].
     */
    @Throws(EnforcerRuleException::class)
    override fun execute() {
        try {
            performValidation(project)
        } catch (e: RuleFailureException) {

            val art = e.offendingArtifact
            val artifactLine = if (art != null)
                "\n# Offending artifact [${art.groupId} : ${art.artifactId} : ${art.version}]\n#"
            else "\n"

            val message = buildString {
                appendLine()
                appendLine("#")
                appendLine("# Structure rule failure:")
                appendLine("# ${getShortRuleDescription()}")
                appendLine("# ")
                appendLine("# Message: ${e.localizedMessage.replace("\n ", "\n# ")}")
                appendLine("# ")
                append("# Offending project [${project.groupId}:${project.artifactId}:${project.version}]\n#")
                append(artifactLine)
            }

            throw EnforcerRuleException(message)
        }
    }

    /**
     * Performs the actual rule validation. Implemented by concrete subclasses.
     *
     * @param project The active [MavenProject].
     * @throws RuleFailureException if the rule is violated.
     */
    @Throws(RuleFailureException::class)
    abstract fun performValidation(project: MavenProject)

    /**
     * Returns a short, human-readable description of what this rule enforces.
     * Shown in the failure banner when the rule is violated.
     * Example: `"No -impl dependencies permitted in this project"`
     */
    abstract fun getShortRuleDescription(): String

    /**
     * Returns `true` if the results of this rule may be cached for reuse in downstream modules.
     */
    protected fun isCacheable(): Boolean = cacheable

    /**
     * Splits [toSplice] on [delimiter] (default: comma) and returns the trimmed, non-blank tokens.
     */
    protected fun splice(toSplice: String, delimiter: String = ","): List<String> =
        toSplice.split(delimiter).map { it.trim() }.filter { it.isNotBlank() }

    /**
     * Splits [toSplice] on commas and compiles each token into a [Pattern].
     *
     * @throws PatternSyntaxException if any token is not a valid regular expression.
     */
    @Throws(PatternSyntaxException::class)
    protected fun splice2Pattern(toSplice: String): List<Pattern> =
        splice(toSplice).map { Pattern.compile(it) }

    /**
     * Returns `true` if any [Regex] in [patternList] fully matches [toMatch].
     */
    protected fun matches(toMatch: String, patternList: List<Regex>): Boolean =
        patternList.any { it.matches(toMatch) }

    /**
     * Returns `true` if any string in [source] is a prefix of [toCheck]
     * (i.e. `toCheck.startsWith(element)`).
     */
    protected fun containsPrefix(source: List<String>?, toCheck: String): Boolean =
        source?.any { toCheck.startsWith(it) } ?: false

    override fun toString(): String = "jGuru Codestyle MavenEnforcerRule: ${this.javaClass.simpleName}"
}

/**
 * [AbstractSimplifiedEnforcerRule] specialisation whose results are never cached.
 *
 * @property enforcerLevel The enforcement level. Defaults to [EnforcerLevel.ERROR].
 *
 * @author [Lennart Jörelid](mailto:lj@jguru.se), jGuru Europe AB
 */
abstract class AbstractNonCacheableEnforcerRule @JvmOverloads constructor(
    lvl: EnforcerLevel = EnforcerLevel.ERROR
) : AbstractSimplifiedEnforcerRule(lvl) {

    /** Always returns `null`, disabling caching for this rule. */
    override fun getCacheId(): String? = null
}
