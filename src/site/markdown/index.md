# What is a _Codestyle_ Project?

<img src="images/jGuruLogo.png" style="float:right" width="167" height="185"/> Put simply - the codestyle 
project contains all those settings and configurations which makes your development, deployment and runtime execution
_just work_. The jGuru Codestyle project contains a
implemented set of best-pracises to start projects quickly - and scale those projects without needing to change your
development and delivery process. This is in part usability engineering for the development process, and in part a 
lot of experience in software development ... all in one repo. 

The jGuru Codestyle project provides parents and plugin configuration for 
[Kotlin development](http://www.kotlinlang.org), mainly on the Java Virtual Machine and JavaScript platforms.
At the time of this writing, most code-quality maven plugins require Java sources to operate on - Kotlin equivalents 
are currently few and far between. Also, most build-style plugins are currently geared towards the Java 8 release.
The intended structure and responsibilities of projects/artifacts within repositories could/should be ordered as 
illustrated in the image below:

![Repository Dependencies](images/plantuml/repo_structure.png "Repository Dependency Structure")

### Reactor Parts

The main parts of the `jGuru Codestyle` build reactor are:

1. **jguru-codestyle-annotations**. The jguru-codestyle-annotations project contains annotations to control the kotlin
         compiler and jUnit test grouping.

2. **jguru-codestyle**. The jguru-codestyle project contains configurations and implementations for defining and
         enforcing the [Nazgul Software Component](theory/software_components.html) ("NSC") patterns.
         The maven build rules defined within this project are used throughout all projects within the reactor, and 
         also applied to all projects using the provided parents from this project (i.e. jguru-kotlin-parent).
         The configuration and plugin codestyle defined here is adapted to Kotlin projects.

3. **jguru-codestyle-kotlin-parent**. Including definitions from the codestyle project, this pom is the internal
        parent for all projecs within the jguru-codestyle reactor. The jguru-kotlin-parent pom configures most of the 
        plugins used for the build cycle definition. This parent is, however, internal to the Nazgul Tools reactor 
        and should not be used as parent for any project outside it.

4. **jguru-codestyle-kotlin-api-parent**. This is a pom which should be used as a parent pom API and SPI projects. 
        It includes infrastructure required to export all packages into an OSGi container (manifest entries), and will
        arrange Java 9 module properties according to best practises.
        
5. **jguru-codestyle-kotlin-model-parent**. This is a pom which should be used as a parent pom for Model projects which
        contains JAXB and/or JPA annotated POJO entity classes. This POM includes facilities for unit testing such 
        annotations and classes in simple manners.
        
5. **jguru-codestyle-kotlin-war-parent**. Use this POM as parent for WAR projects. 

6. **jguru-codestyle-kotlin-js-parent**. Use this POM as parent for JavaScript projects. 

## "JVM" target: Java9 interoperability

The Jigsaw module system of Java9 is similar to OSGi and JBoss Modules in that it attempts to define how to access 
(and limit access to) classes or resources. In this sense, the 3 module systems are similar - but the way to make 
those module systems interact smoothly with the build process are different. Therefore, the codestyle 
reactor aims to achieve complete interoperability by defining project types which define what kind of access are 
given to resources within the artifacts of said project.

## "JS" target: CommonJS interoperability

The JavaScript development for Kotlin can use and access standard AMD and CommonJS-packaged dependencies/artifacts 
from standard package managers such as [Node Package Modules](https://www.npmjs.com). This implies that Kotlin can be
used as the source language to create web applications in addition to compiled binaries. Codestyle assists in 
reducing the build-system impedance in coding Web applications in Kotlin.