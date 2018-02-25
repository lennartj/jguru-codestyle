/*
 * Copyright (c) jGuru Europe AB.
 * All rights reserved.
 */

package se.jguru.codestyle.annotations.junit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * jUnit group category interface, indicating tests which must be run
 * using the `CdiRunner` jUnit Runner.
 * 
 * @author <a href="mailto:lj@jguru.se">Lennart J&ouml;relid</a>, jGuru Europe AB
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequiresInjection {
}