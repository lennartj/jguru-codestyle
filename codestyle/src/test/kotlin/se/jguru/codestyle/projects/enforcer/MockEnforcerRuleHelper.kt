/*
 * Copyright (c) jGuru Europe AB.
 * All rights reserved.
 */

package se.jguru.codestyle.projects.enforcer

import org.apache.maven.enforcer.rule.api.EnforcerRuleHelper
import org.apache.maven.plugin.logging.Log
import org.apache.maven.plugin.logging.SystemStreamLog
import org.apache.maven.project.MavenProject
import org.codehaus.plexus.PlexusContainer
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluationException
import org.codehaus.plexus.component.repository.exception.ComponentLookupException
import java.io.File

/**
 *
 * @author [Lennart Jörelid](mailto:lj@jguru.se), jGuru Europe AB
 */
class MockEnforcerRuleHelper(val project: MavenProject) : EnforcerRuleHelper {

    override fun alignToBaseDirectory(file: File): File? = null

    @Throws(ExpressionEvaluationException::class)
    override fun evaluate(expression: String): Any? = when (expression) {
        "\${project}" -> project
        else -> null
    }

    @Throws(ComponentLookupException::class)
    override fun getComponent(clazz: Class<*>): Any {
        throw ComponentLookupException("Foo", "Bar", "Baz")
    }

    @Throws(ComponentLookupException::class)
    override fun getComponent(componentKey: String): Any = {
        throw ComponentLookupException("Foo", "Bar", "Baz")
    }

    @Throws(ComponentLookupException::class)
    override fun getComponent(role: String, roleHint: String): Any? = null

    @Throws(ComponentLookupException::class)
    override fun getComponentList(role: String): List<*>? = null

    @Throws(ComponentLookupException::class)
    override fun getComponentMap(role: String): Map<*, *>? = null

    override fun getContainer(): PlexusContainer? = null

    override fun getLog(): Log = SystemStreamLog()
}