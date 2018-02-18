/*
 * Copyright (c) jGuru Europe AB.
 * All rights reserved.
 */

package se.jguru.codestyle.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicator that the Kotlin compiler should add a Default (i.e. "no-argument")
 * constructor to the class annotated with {@code AddDefaultConstructor}.  This should engage the no-arg constructor
 * kotlin compiler plugin, which otherwise would be done with something like:
 *
 * <pre>
 * &lt;compilerPlugins&gt;
 *     &lt;plugin&gt;no-arg&lt;/plugin&gt;
 * &lt;/compilerPlugins&gt;
 * </pre>
 *
 * @author <a href="mailto:lj@jguru.se">Lennart J&ouml;relid</a>, jGuru Europe AB
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface AddDefaultConstructor {
}
