/*
 * Copyright (c) jGuru Europe AB.
 * All rights reserved.
 */

package se.jguru.codestyle.projects.enforcer

import org.apache.maven.enforcer.rule.api.EnforcerLevel
import org.apache.maven.enforcer.rule.api.EnforcerRule
import org.apache.maven.enforcer.rule.api.EnforcerRule2
import org.apache.maven.enforcer.rule.api.EnforcerRuleException
import org.apache.maven.enforcer.rule.api.EnforcerRuleHelper
import org.apache.maven.project.MavenProject
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluationException
import java.util.ArrayList
import java.util.StringTokenizer
import java.util.regex.Pattern
import java.util.regex.PatternSyntaxException

/**
 * Abstract Enforcer rule specification, handling some pretty printing
 * of Maven's Enforcement exceptions.
 *
 * @param enforcerLevel The level of enforcement within this Rule. Defaults to `EnforcerLevel.ERROR`.
 *
 * @author [Lennart Jörelid](mailto:lj@jguru.se), jGuru Europe AB
 */
abstract class AbstractEnforcerRule @JvmOverloads constructor(

    /**
     * Assigns the EnforcerLevel of this AbstractEnforcerRule.
     *
     * @see EnforcerRule2.getLevel
     */
    protected open val enforcerLevel: EnforcerLevel = EnforcerLevel.ERROR) : EnforcerRule2 {

    /**
     * Defines if the results of this AbstractEnforcerRule is cacheable.
     */
    protected var cacheable = false

    /**
     * Retrieves the enforcer level of this AbstractEnforcerRule
     */
    override fun getLevel(): EnforcerLevel = enforcerLevel

    /**
     * This is the interface into the rule. This method should throw an exception containing a reason message if the
     * rule fails the check. The plugin will then decide based on the fail flag if it should stop or just log the
     * message as a warning.
     *
     * @param helper The helper provides access to the log, MavenSession and has helpers to get common components. It
     * is also able to lookup components by class name.
     * @throws org.apache.maven.enforcer.rule.api.EnforcerRuleException the enforcer rule exception
     */
    @Throws(EnforcerRuleException::class)
    override fun execute(helper: EnforcerRuleHelper) {

        // Get the MavenProject or die trying
        val project: MavenProject = try {
            helper.evaluate("\${project}") as MavenProject
        } catch (e: ExpressionEvaluationException) {

            // Whoops.
            val msg = ("Could not acquire MavenProject. (Expression lookup failure for: "
                + e.localizedMessage + ")")
            throw EnforcerRuleException(msg, e)
        }

        // Delegate performing the Validation
        try {
            performValidation(project, helper)
        } catch (e: RuleFailureException) {

            // Create a somewhat verbose failure message.
            var message = ("\n"
                + "\n#"
                + "\n# Structure rule failure:"
                + "\n# " + getShortRuleDescription()
                + "\n# "
                + "\n# Message: " + e.getLocalizedMessage()
                + "\n# " + "\n# Offending project [" + project.groupId + ":"
                + project.artifactId + ":" + project.version + "]" + "\n#")

            val art = e.offendingArtifact
            message += when (art) {
                null -> "\n"
                else -> ("\n# Offending artifact [${art.groupId} : ${art.artifactId} : ${art.version}]\n#")
            }

            // Re-throw for pretty print
            throw EnforcerRuleException(message)
        }
    }

    /**
     * Delegate method, implemented by concrete subclasses.
     *
     * @param project The active MavenProject.
     * @param helper  The EnforcerRuleHelper instance, from which the MavenProject has been retrieved.
     * @throws RuleFailureException If the enforcer rule was not satisfied.
     */
    @Throws(RuleFailureException::class)
    abstract fun performValidation(project: MavenProject, helper: EnforcerRuleHelper)

    /**
     * @return A human-readable short description for this AbstractEnforcerRule.
     * (Example: "No -impl dependencies permitted in this project")
     */
    abstract fun getShortRuleDescription(): String

    /**
     * This method tells the enforcer if the rule results may be cached.
     * If the result is true, the results will be remembered for future executions
     * in the same build (ie children).
     * Subsequent iterations of the rule will be queried to see if they are also cacheable.
     * This will allow the rule to be uncached further down the tree if needed.
     *
     * @return `true` if rule is cacheable
     */
    override fun isCacheable(): Boolean {
        return cacheable
    }

    /**
     * Helper method which splices the provided string into a List, separating on commas.
     *
     * @param toSplice The string to splice
     * @param delimiter The delimiter to splice with
     * @return A list holding the elements of the spliced string.
     */
    protected fun splice(toSplice: String, delimiter: String = ","): List<String> {

        val toReturn = ArrayList<String>()

        val tok = StringTokenizer(toSplice, delimiter, false)
        while (tok.hasMoreTokens()) {
            toReturn.add(tok.nextToken())
        }

        return toReturn
    }

    /**
     * Helper method which splices the provided String into a List of Pattern instances.
     *
     * @param toSplice The string to splice
     * @return A List holding the elements of the spliced string, converted to Patterns
     * @throws PatternSyntaxException if the `Pattern.compile` method could not compile the provided string.
     */
    @Throws(PatternSyntaxException::class)
    protected fun splice2Pattern(toSplice: String): List<Pattern> =
        splice(toSplice).mapTo(ArrayList<Pattern>()) { Pattern.compile(it) }

    /**
     * Matches the provided `toMatch` string with all [Regex] in the patternList.
     * If one pattern matches, this method returns `true`.
     *
     * @param toMatch     The string to match to every [Regex] in the supplied patternList.
     * @param patternList The List of [Regex] to use in matching.
     * @return `true` if one pattern in the patternList matches, this method returns `true`.
     */
    protected fun matches(toMatch: String, patternList: List<Regex>): Boolean =
        patternList.any { it.matches(toMatch) }

    /**
     * Checks if any element within source startsWith the provided toCheck string.
     *
     * @param source  The list of strings which could possibly contain toCheck.
     * @param toCheck The string to validate.
     * @return `true` if any element within source returns true to
     * `toCheck.startsWith(element)`.
     */
    protected fun containsPrefix(source: List<String>?, toCheck: String): Boolean {

        source?.forEach { current ->
            if (toCheck.startsWith(current)) {

                // Found the prefix within the provided toCheck String.
                return true
            }
        }

        // The prefix was not found within the provided string toCheck.
        return false
    }

    override fun toString(): String {
        return "jGuru Codestyle MavenEnforcerRule: ${this.javaClass.simpleName}"
    }
}

/**
 * AbstractEnforcerRule implementation which implements a non-cacheable behaviour.
 *
 * @property enforcerLevel The level of enforcement within this Rule. Defaults to `EnforcerLevel.ERROR`.
 * @author [Lennart Jörelid](mailto:lj@jguru.se), jGuru Europe AB
 */
abstract class AbstractNonCacheableEnforcerRule @JvmOverloads constructor(

    /**
     * Assigns the EnforcerLevel of this AbstractEnforcerRule.
     *
     * @see EnforcerRule2.getLevel
     */
    lvl: EnforcerLevel = EnforcerLevel.ERROR) : AbstractEnforcerRule(lvl) {

    /**
     * Always returns `null`.
     */
    override fun getCacheId(): String? = null

    /**
     * Always returns `false`.
     */
    override fun isResultValid(cachedRule: EnforcerRule): Boolean = false
}