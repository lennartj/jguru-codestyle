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
import java.util.function.Supplier

/**
 *
 * @author [Lennart Jörelid](mailto:lj@jguru.se), jGuru Europe AB
 */
class MockEnforcerRuleHelper(val project: MavenProject,
                             val logLevel : Level = Level.DEBUG) : EnforcerRuleHelper {

    override fun alignToBaseDirectory(file: File): File? = null

    @Throws(ExpressionEvaluationException::class)
    override fun evaluate(expression: String): Any? = when (expression) {
        "\${project}" -> project
        else -> null
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
    override fun <T : Any?> getComponent(clazz: Class<T>?): T {
        throw ComponentLookupException("Foo", "Bar", "Baz")
    }

    @Throws(ComponentLookupException::class)
    override fun <T : Any?> getComponent(clazz: Class<T>?, roleHint: String?): T {
        throw ComponentLookupException("Foo", "Bar", "Baz")
    }

    @Throws(ComponentLookupException::class)
    override fun getComponentMap(role: String): Map<String, *>? = null

    override fun getContainer(): PlexusContainer? = null

    override fun getLog(): Log = MutableStateLog(logLevel)

    @Throws(ComponentLookupException::class)
    override fun getCache(key: String?, producer: Supplier<*>?): Any {
        throw ComponentLookupException("Foo", "Bar", "Baz")
    }

    enum class Level {

        WARN,

        INFO,

        DEBUG
    }

    class MutableStateLog(private val level: Level = Level.DEBUG) : SystemStreamLog() {

        override fun isInfoEnabled(): Boolean = level <= Level.INFO

        override fun isWarnEnabled(): Boolean = level <= Level.WARN

        override fun isDebugEnabled(): Boolean = level == Level.DEBUG
    }
}