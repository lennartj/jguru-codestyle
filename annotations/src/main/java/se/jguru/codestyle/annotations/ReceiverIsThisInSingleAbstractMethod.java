/*
 * Copyright (c) jGuru Europe AB.
 * All rights reserved.
 */

package se.jguru.codestyle.annotations;

/**
 * Add this annotation to a class to indicate to the Kotlin compiler that the
 * sam-with-receiver compiler module should be used.
 *
 * The sam-with-receiver compiler plugin makes the first parameter of the annotated Java "single abstract method"
 * (SAM) interface method a receiver in Kotlin. This conversion only works when the SAM interface is passed as a
 * Kotlin lambda, both for SAM adapters and SAM constructors.
 *
 * @author <a href="mailto:lj@jguru.se">Lennart J&ouml;relid</a>, jGuru Europe AB
 */
public @interface ReceiverIsThisInSingleAbstractMethod {
}
