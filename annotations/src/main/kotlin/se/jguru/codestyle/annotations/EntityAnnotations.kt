/*
 * Copyright (c) jGuru Europe AB.
 * All rights reserved.
 */

package se.jguru.codestyle.annotations

/**
 * Indicator that the Kotlin compiler should add a Default (i.e. "no-argument")
 * constructor to the class annotated with `AddDefaultConstructor`.
 *
 * This should engage the no-arg constructor kotlin compiler
 * plugin, which otherwise would be done with somethling like:
 *
 * &lt;compilerPlugins&gt;
 *     &lt;plugin&gt;no-arg&lt;/plugin&gt;
 * &lt;/compilerPlugins&gt;
 *
 * @author [Lennart J&ouml;relid](mailto:lj@jguru.se), jGuru Europe AB
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
@MustBeDocumented
annotation class AddDefaultConstructor

/**
 * Kotlin has classes and their members final by default, which makes it inconvenient to use frameworks and
 * libraries such as Spring AOP that require classes to be open. The `all-open` compiler plugin adapts Kotlin
 * to the requirements of those frameworks and makes classes annotated with a specific annotation and
 * their members open without the explicit open keyword.
 *
 * @author [Lennart J&ouml;relid](mailto:lj@jguru.se), jGuru Europe AB
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class UseOpenMembers

/**
 * Add this annotation to indicate to the Kotlin compiler that the Spring framework is used in the code.
 * The plugin specifies the following annotations: @Component, @Async, @Transactional, @Cacheable
 * and @SpringBootTest. Thanks to meta-annotations support classes annotated
 * with @Configuration, @Controller, @RestController, @Service or @Repository are automatically
 * opened since these annotations are meta-annotated with @Component.
 *
 * @author [Lennart J&ouml;relid](mailto:lj@jguru.se), jGuru Europe AB
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class UseSpring

/**
 * Add this annotation to a class to indicate to the Kotlin compiler that the
 * sam-with-receiver compiler module should be used.
 *
 * The sam-with-receiver compiler plugin makes the first parameter of the annotated Java "single abstract method"
 * (SAM) interface method a receiver in Kotlin. This conversion only works when the SAM interface is passed as a
 * Kotlin lambda, both for SAM adapters and SAM constructors.
 *
 * @author [Lennart J&ouml;relid](mailto:lj@jguru.se), jGuru Europe AB
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class ReceiverIsThisInSingleAbstractMethod