# Kotlin-to-JVM (bytecode) compilation

The Kotlin compiler can produce bytecode, which execute on the Java Virtual Machine.
The `jGuru Codestyle Parent POM` is tailored to simplify working with Kotlin-only projects,
and has defined some properties to handle this in an extensible way. Typically these properties
control the versions of Kotlin, its emitted Bytecode, its documentation plugin and its modules.

4 main properties control the Kotlin versions used in building artifacts, and an additional property
controls the version of the documentation generator, Dokka:        

## `kotlin.version` property

With frequent Kotlin releases, the Kotlin language version may change often. Hence, it makes sense to 
supply a maven property defining the Kotlin version used within the projects. 
The property `kotlin.version` is used by the `kotlin-maven-plugin` to define the underlying Kotlin platform:

    <kotlin.version>2.1.10</kotlin.version>         

## `kotlin.jvm.target` property

By default, the jGuru Codestyle Parent POM defines the kotlin compiler to emit JDK 11-compliant bytecode.
If you prefer - say - JDK 8-compliant bytecode, simply change this property in subprojects. The recommended
approach is to set this property within the BOM (Bill-Of-Materials) pom file within your reactor, to make it 
be applied in all your artifact-generating projects: 

    <kotlin.jvm.target>17</kotlin.jvm.target>

## `kotlin.compiler.apiVersion` property

By default, the jGuru Codestyle Parent POM defines the kotlin compiler to interface with Kotlin 2.1-libraries.
If you prefer another Kotlin language version, simply change this property in subprojects: 

    <kotlin.compiler.apiVersion>2.0</kotlin.compiler.apiVersion>

## `kotlin.compiler.languageVersion` property

By default, the jGuru Codestyle Parent POM defines the kotlin compiler to emit Kotlin 1.9-compliant bytecode.
If you prefer another Kotlin language version, simply change this property in subprojects: 

    <kotlin.compiler.languageVersion>2.0</kotlin.compiler.languageVersion>
       
## `dokka.version` property

The Dokka Maven Plugin is the "official" documentation engine for Kotlin code. While it historically have been
problematic generating JavaDoc JARs using the Kotlin plugin for any JVM above 1.8, the current Dokka Maven plugin 
is stabler.

The dokka plugin version is controlled by another property (`dokka.version`), and reads the versions of the 
Kotlin API and language from the above-mentioned properties: 

    <dokka.version>2.0.0</dokka.version>     
        