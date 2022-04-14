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

    <kotlin.version>1.6.20</kotlin.version>         

## `kotlin.jvm.target` property

By default, the jGuru Codestyle Parent POM defines the kotlin compiler to emit JDK 11-compliant bytecode.
If you prefer - say - JDK 8-compliant bytecode, simply change this property in subprojects. The recommended
approach is to set this property within the BOM (Bill-Of-Materials) pom file within your reactor, to make it 
be applied in all your artifact-generating projects: 

    <kotlin.jvm.target>8</kotlin.jvm.target>
    
## `kotlin.compiler.apiVersion` property

By default, the jGuru Codestyle Parent POM defines the kotlin compiler to interface with Kotlin 1.6-libraries.
If you prefer another Kotlin language version, simply change this property in subprojects: 

    <kotlin.compiler.apiVersion>1.4</kotlin.compiler.apiVersion>
    
## `kotlin.compiler.languageVersion` property

By default, the jGuru Codestyle Parent POM defines the kotlin compiler to emit Kotlin 1.6-compliant bytecode.
If you prefer another Kotlin language version, simply change this property in subprojects: 

    <kotlin.compiler.languageVersion>1.4</kotlin.compiler.languageVersion>
       
## `dokka.version` property

The Dokka Maven Plugin is the "official" documentation engine for Kotlin code. While it historically have been
problematic generating JavaDoc JARs using the Kotlin plugin for any JVM above 1.8, the 1.4+ strain of the Dokka
Maven plugin works better. Still being in an Alpha release at the time of this writing, we can be confident that
the biggest hurdle for releasing software accompanied by JavaDoc JARs for Kotlin running on Java platforms greater
than JDK 8 seems to be coming to a close.

The dokka plugin version is controlled by another property (`dokka.version`), and reads the versions of the 
Kotlin API and language from the above-mentioned properties: 

    <dokka.version>1.6.10</dokka.version>     
        