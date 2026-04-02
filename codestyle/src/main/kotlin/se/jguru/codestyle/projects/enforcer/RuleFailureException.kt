/*
 * Copyright (c) jGuru Europe AB.
 * All rights reserved.
 */

package se.jguru.codestyle.projects.enforcer

import org.apache.maven.artifact.Artifact

/**
 * Exception thrown when a Maven Enforcer rule detects a policy violation.
 *
 * @param message Description of the violation.
 * @param cause Optional underlying cause.
 * @param offendingArtifact Optional artifact that triggered this violation.
 *
 * @author <a href="mailto:lj@jguru.se">Lennart Jörelid</a>, jGuru Europe AB
 */
class RuleFailureException @JvmOverloads constructor(
    message: String? = null,
    cause: Throwable? = null,
    val offendingArtifact: Artifact? = null
) : RuntimeException(message, cause)
