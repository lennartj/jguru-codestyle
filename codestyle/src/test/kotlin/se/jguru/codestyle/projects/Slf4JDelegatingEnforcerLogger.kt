/*
 * Copyright (c) jGuru Europe AB.
 * All rights reserved.
 */
package se.jguru.codestyle.projects.enforcer.se.jguru.codestyle.projects

import org.apache.maven.enforcer.rule.api.EnforcerLogger
import org.slf4j.Logger
import org.slf4j.event.Level
import java.util.function.Supplier

open class Slf4JDelegatingEnforcerLogger(private var delegate: Logger) : EnforcerLogger {

    //
    //
    private fun logMsg(msg: String, level: Level) {
        when(level) {
            Level.TRACE -> delegate.trace(msg)
            Level.DEBUG -> delegate.debug(msg)
            Level.INFO -> delegate.info(msg)
            Level.WARN -> delegate.warn(msg)
            Level.ERROR -> delegate.error(msg)
            else -> throw IllegalArgumentException("Unsupported log level: $level")
        }
    }

    override fun warnOrError(message: CharSequence?) = logMsg(message.toString(), Level.WARN)

    override fun warnOrError(messageSupplier: Supplier<CharSequence>?) = logMsg(messageSupplier?.get().toString(), Level.WARN)

    override fun isDebugEnabled(): Boolean = delegate.isDebugEnabled

    override fun debug(message: CharSequence?) = logMsg(message.toString(), Level.DEBUG)

    override fun debug(messageSupplier: Supplier<CharSequence>?) = logMsg(messageSupplier?.get().toString(), Level.DEBUG)

    override fun isInfoEnabled(): Boolean = delegate.isInfoEnabled

    override fun info(message: CharSequence?) = logMsg(message.toString(), Level.INFO)

    override fun info(messageSupplier: Supplier<CharSequence>?) = logMsg(messageSupplier?.get().toString(), Level.INFO)

    override fun isWarnEnabled(): Boolean = delegate.isWarnEnabled

    override fun warn(message: CharSequence?) = logMsg(message.toString(), Level.WARN)

    override fun warn(messageSupplier: Supplier<CharSequence>?) = logMsg(messageSupplier?.get().toString(), Level.WARN)

    override fun isErrorEnabled(): Boolean = delegate.isErrorEnabled

    override fun error(message: CharSequence?) = logMsg(message.toString(), Level.ERROR)

    override fun error(messageSupplier: Supplier<CharSequence>?) = logMsg(messageSupplier?.get().toString(), Level.ERROR)
}