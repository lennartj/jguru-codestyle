# jGuru Codestyle Annotations: Overview

The codestyle annotations project contains globally available annotations, 
some for controlling the Kotlin compiler and some for controlling unit tests through 
the [Maven Surefire plugin](http://maven.apache.org/surefire/maven-surefire-plugin/).

## Kotlin compilation annotations

Kotlin build-time annotations provides control over the kotlin compiler's operation to 
include or exclude the [Kotlin compiler plugins](https://kotlinlang.org/docs/reference/compiler-plugins.html)
to apply different compiler features during compilation.

The exact plugin configuration is provided in the Maven POM; default configurations within this reactor
which are used unless overridden are: 

          <pluginOptions>
              <option>no-arg:annotation=se.jguru.codestyle.annotations.AddDefaultConstructor</option>
              <option>spring:annotation=se.jguru.codestyle.annotations.UsesSpringFramework</option>
              <option>all-open:annotation=se.jguru.codestyle.annotations.UseOpenMembers</option>
              <option>sam-with-receiver:annotation=se.jguru.codestyle.annotations.ReceiverIsThisInSingleAbstractMethod</option>
              <!-- Call instance initializers in the synthetic constructor -->
              <option>no-arg:invokeInitializers=true</option>
          </pluginOptions> 
 
### Usage Example

In any project where you want a Kotlin class to add a default (no-arg) constructor, simply 
annotate the class with `@AddDefaultConstructor`. 

           @AddDefaultConstructor 
           class SomeClass(with : String, many : String, constructorArgs : StringBuilder) { ... }
           
In a similar fashion, the function of the other annotations as described within 
[Kotlin compiler plugins](https://kotlinlang.org/docs/reference/compiler-plugins.html) are:

1. **`@UseOpenMembers`**: Kotlin has classes and their members final by default, which makes it inconvenient to use 
   frameworks and libraries such as Spring AOP that require classes to be open. The all-open compiler plugin adapts 
   Kotlin to the requirements of those frameworks and makes classes annotated with a specific annotation and their 
   members open without the explicit open keyword. For instance, when you use Spring, you don't need all the classes 
   to be open, but only classes annotated with specific annotations like `@Configuration` or `@Service`. 
   All-open allows to specify such annotations.
   
2. **`@UsesSpringFramework`**: if you use Spring, you can enable the kotlin-spring compiler plugin 
   instead of specifying Spring annotations manually. `kotlin-spring` is a wrapper on top of `all-open`, and 
   it behaves exactly the same way.

3. **`@AddDefaultConstructor`**: generates an additional zero-argument constructor. The generated constructor is 
   synthetic so it can’t be directly called from Java or Kotlin, but it can be called using reflection.
   This allows the Java Persistence API (JPA) to instantiate the data class although it does not have the 
   zero-parameter constructor from Kotlin or Java point of view.

## Test-scope / Surefire annotations

The `@IntegrationTest` annotation indicates that a test class should be regarded as an automatic integration test.
The `@RequiresInjection` annotation indicates that a test class should use the cdi-unit test runner to enable injection. 
 