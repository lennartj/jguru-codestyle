# jGuru Codestyle: Java Internal Parent - overview

There are 3 POM projects to mind when you are using the jGuru Codestyle reactor POMs for your development. 
Each of these projects contain a single POM intended to be used as parent for other POMs. 
However, the tasks and uses of these 3 POM projects differ:

1. **jGuru-codestyle-java-internal-parent**: The topmost Parent POM, where most of the integration with 
   Maven plugins and Maven build mechanics (i.e. plugin definitions and their configuration) is done. 
   Configuration files for plugins are normally found in the `jGuru-codestyle` project and imported as a dependency 
   into plugin configurations. The jGuru-codestyle-java-internal-parent is **not** intended to be included as
   parent directly in your development projects; use either of the POMs below instead.   

2. **jGuru-codestyle-java-parent**: This is a POM intended as parent for projects
   which produce an artifact. Such projects typically have a POM whose packaging is JAR, WAR, EAR
   or bundle - but typically **not** with packaging POM and not defining any modules (Maven sub-projects).
   The `jGuru-codestyle-java-parent` POM extends the `jGuru-codestyle-java-internal-parent` POM by including 
   some basic [aspects](http://en.wikipedia.org/wiki/Aspect-oriented_programming) which can be
   used to ensure that objects are created with correct state (i.e. validate the internal state
   of objects). Typically, state validation is done after calling a constructor or after converting
   an object from another form (like XML or JSON during transport).

3. **jGuru-codestyle-external-reactor**: This is a POM intended as parent for projects that do
   **not** produce artifacts, but instead define the build reactor. Such projects normally have
   "pom" packaging and define modules.

## What is the difference between a "Parent" and a "Reactor" POM?

The Maven POM defines many configuration and build settings for Maven projects.
Both "parent" and "reactor" projects produce "pom.xml" files as their only artifacts (i.e. they
both have a `<packaging>pom</packaging>` element), as exemplified below: 

        <groupId>some.group.id</groupId>
        <artifactId>some-artifact-id</artifactId>
        <packaging>pom</packaging>
 
However, there are 2 fundamental tasks for projects with a single POM artifact, as illustrated in the table below. 
Therefore, these projects are given 2 different names - "parent" and "reactor" POMs - in the jGuru Codestyle vocabulary:

<table>
    <tr>
        <th>Type</th>
        <th>Leaf?</th>
        <th>Dependencies?</th>
        <th>Children?</th>
        <th>Description</th>
    </tr>
    <tr>
        <td>Parent</td>
        <td>Yes</td>
        <td>Yes</td>
        <td>No</td>
        <td>Used as parents for projects producing artifacts and <b>not</b> having any modules (project children)</td>
    </tr>
    <tr>
        <td>Reactor</td>
        <td>No</td>
        <td>No</td>
        <td>Yes</td>
        <td>Used as parents for projects <b>not</b> producing artifacts and having modules (project children)</td>
    </tr>
</table>

**Parent** poms define dependencies and plugins required to produce an artifact.
Parent poms are intended to be used as parents for projects producing artifacts and **not** having
any modules (project children). The jGuru Codestyle structures projects in leaf projects
which create artifacts, and non-leaf projects which create the build reactor by defining modules.

**Reactor** poms define the build reactor, implying that they are parents for POMs which have
POM packaging and defines modules.

### Dependency Graph

The dependency graph for this project is shown below:

![Dependency Graph](./images/dependency_graph.png)