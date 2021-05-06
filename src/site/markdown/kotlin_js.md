# Kotlin-to-JS (javascript) compilation

The Kotlin compiler can produce JavaScript which execute within a Browser or on NodeJS servers.
The `jGuru Codestyle Parent POM` is tailored to simplify working with Kotlin-only projects,
and has defined some properties to handle this in an extensible way. Typically these properties
control the versions of Kotlin, its emitted Ecmascript version, its documentation plugin and its modules.

## `kotlin.version` property

With frequent Kotlin releases, the Kotlin language version is often changed. To update the Kotlin version 
used by the `kotlin-maven-plugin` (and hence the kotlin language and compiler version), change that property
within your project on the following form:

    <kotlin.version>1.4.32</kotlin.version>         

## `kotlin.js.sourceMap` property

Boolean variable indicating whether or not the emitted JavaScript should contain a source map. 

    <kotlin.js.sourceMap>true</kotlin.js.sourceMap>
    
## `kotlin.js.moduleKind` property

By default, the jGuru Codestyle Parent POM defines the kotlin compiler to emit plain JavaScript modules.
Assign the `kotlin.js.moduleKind` property to another value should you desire another output.
Permitted values are:

1. plain (default)
2. amd
3. commonjs
4. umd

Hence - for example:

    <kotlin.js.moduleKind>commonjs</kotlin.js.moduleKind>
        