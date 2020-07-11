/*
 * Copyright (c) jGuru Europe AB.
 * All rights reserved.
 */

package se.jguru.codestyle.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Kotlin has classes and their members final by default, which makes it inconvenient to use frameworks and
 * libraries such as Spring AOP that require classes to be open. The `all-open` compiler plugin adapts Kotlin
 * to the requirements of those frameworks and makes classes annotated with a specific annotation and
 * their members open without the explicit open keyword.
 * 
 * @author <a href="mailto:lj@jguru.se">Lennart J&ouml;relid</a>, jGuru Europe AB
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
@Documented
public @interface UseOpenMembers {
}
