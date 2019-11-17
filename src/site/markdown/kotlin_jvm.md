# Kotlin-to-JVM (bytecode) compilation

The Kotlin compiler can produce bytecode, which execute on the Java Virtual Machine.
The `jGuru Codestyle Parent POM` is tailored to simplify working with Kotlin-only projects,
and has defined some properties to handle this in an extensible way. Typically these properties
control the versions of Kotlin, its emitted Bytecode, its documentation plugin and its modules. 

## `kotlin.version` property

With frequent Kotlin releases, the Kotlin language version is often changed. To update the Kotlin version 
used by the `kotlin-maven-plugin` (and hence the kotlin language and compiler version), change that property
within your project on the following form:

    <kotlin.version>1.3.60</kotlin.version>         

## `kotlin.jvm.target` property

By default, the jGuru Codestyle Parent POM defines the kotlin compiler to emit JDK 8-compliant bytecode.
If you prefer - say - JDK 6-compliant bytecode, simply change this property in subprojects: 

    <kotlin.jvm.target>1.8</kotlin.jvm.target>
    
## `kotlin.jvm.target` property

By default, the jGuru Codestyle Parent POM defines the kotlin compiler to emit JDK 8-compliant bytecode.
If you prefer - say - JDK 6-compliant bytecode, simply change this property in subprojects: 

    <kotlin.jvm.target>1.8</kotlin.jvm.target>
        