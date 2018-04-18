/*
 * Copyright (c) jGuru Europe AB.
 * All rights reserved.
 */

package se.jguru.codestyle.projects.enforcer

import org.apache.maven.artifact.Artifact

/**
 * Exception indicating that a Maven Enforcer rule implementation has indicated failure.
 *
 * @param message           the detail message (which is saved for later retrieval by the [.getMessage] method).
 * @param cause             the cause (which is saved for later retrieval by the [.getCause] method). (A
 * <tt>null</tt> value is permitted, and indicates that the cause is nonexistent or unknown.)
 * @param offendingArtifact The artifact which triggered this RuleFailureException.
 * @author <a href="mailto:lj@jguru.se">Lennart J&ouml;relid</a>, jGuru Europe AB
 */
class RuleFailureException @JvmOverloads constructor(
    message: String? = null,
    cause: Throwable? = null,
    var offendingArtifact: Artifact?) : RuntimeException(message, cause) {

    /**
     * Constructs a new runtime exception with the specified detail message.
     * The cause is not initialized, and may subsequently be initialized by a
     * call to [.initCause].
     *
     * @param message the detail message. The detail message is saved for
     * later retrieval by the [.getMessage] method.
     */
    constructor(message: String) : this(message, null, null)

    /**
     * Constructs a new runtime exception with the specified detail message and
     * cause.
     *
     *Note that the detail message associated with
     * `cause` is *not* automatically incorporated in
     * this runtime exception's detail message.
     *
     * @param message the detail message (which is saved for later retrieval by the [.getMessage] method).
     * @param cause   the cause (which is saved for later retrieval by the
     * [.getCause] method).  (A <tt>null</tt> value is permitted, and indicates that the cause is nonexistent or
     * unknown.)
     * @since 1.4
     */
    constructor(message: String, cause: Throwable) : this(message, cause, null)
}