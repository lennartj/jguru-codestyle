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
are currently few and far between. Also, most build-style plugins are currently geared towards the Java 8 release, 
while this Codestyle targets the Java 9 release.   

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