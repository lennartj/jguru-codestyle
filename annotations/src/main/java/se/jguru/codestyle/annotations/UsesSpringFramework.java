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
 * Add this annotation to indicate to the Kotlin compiler that the Spring framework is used in the code.
 * The plugin specifies the following annotations: @Component, @Async, @Transactional, @Cacheable
 * and @SpringBootTest. Thanks to meta-annotations support classes annotated
 * with @Configuration, @Controller, @RestController, @Service or @Repository are automatically
 * opened since these annotations are meta-annotated with @Component.
 * 
 * @author <a href="mailto:lj@jguru.se">Lennart J&ouml;relid</a>, jGuru Europe AB
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface UsesSpringFramework {
}
