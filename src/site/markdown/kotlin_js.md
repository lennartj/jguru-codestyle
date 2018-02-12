# Kotlin-to-JS (javascript) compilation

The Kotlin compiler can produce JavaScript which execute within a Browser or on NodeJS servers.
The `jGuru Codestyle Parent POM` is tailored to simplify working with Kotlin-only projects,
and has defined some properties to handle this in an extensible way. Typically these properties
control the versions of Kotlin, its emitted Ecmascript version, its documentation plugin and its modules. 

## `kotlin.version` property

With frequent Kotlin releases, the Kotlin language version is often changed. To update the Kotlin version 
used by the `kotlin-maven-plugin` (and hence the kotlin language and compiler version), change that property
within your project on the following form:

    <kotlin.version>1.2.21</kotlin.version>         

## `kotlinjs.outputfile` property

By default, the jGuru Codestyle Parent POM defines the kotlin compiler to emit its JavaScript code
into the `target/web/${project.artifactId}_${project.version}.js`.
If you prefer another output file, supply another value for the kotlinjs.outputfile property: 

    <kotlinjs.outputfile>target/foo/bar.js</kotlinjs.outputfile>
    
## `kotlinjs.modulekind` property

By default, the jGuru Codestyle Parent POM defines the kotlin compiler to emit plain JavaScript modules.
Assign the `kotlinjs.modulekind` property to another value should you desire another output.
Permitted values are:

1. plain (default)
2. amd
3. commonjs
4. umd

Hence - for example:

    <kotlinjs.modulekind>commonjs</kotlinjs.modulekind>
        