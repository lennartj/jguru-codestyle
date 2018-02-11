# Types ("Stereotypes") of Projects

Several standard ProjectType implementations are defined within the jGuru Codestyle.
Most of those reside within [CommonProjectTypes] enumeration, defining the values 
within the table below - all of which are ProjectType implementations.
Each project type can be identified uniquely by its Maven Project's GAP (GroupID, ArtifactID, Packaging)
values, and for the exact criteria, please refer to the JavaDoc of each value.

The CommonProjectType values are:
 
<table>
    <tr>
        <th>Name</th>
        <th>Description</th>
    </tr>
    <tr>
        <td>REACTOR</td>
        <td>POM project containing modules but no dependencies. Creates a Maven build reactor, but no artifacts.</td>
    </tr>
    <tr>
        <td>PARENT</td>
        <td>POM project containing dependencies but no modules. Defines plugins for building artifacts, but no 
        build reactor.</td>
    </tr> 
    <tr>
        <td>ASSEMBLY</td>
        <td>Pom project, defining assemblies and/or aggregation projects. May not contain module definitions.</td>
    </tr>
    <tr>
        <td>ASPECT</td>
        <td>Aspect definition project, holding publicly available aspect implementations.</td>
    </tr>
    <tr>
        <td>MODEL</td>
        <td>Model project defining entities. May have test-scope dependencies on test and proof-of-concept projects.</td>
    </tr>
    <tr>
        <td>API</td>
        <td>API project, defining service interaction, abstract implementations and exceptions. May have 
        compile-scope dependencies on model projects within the same component, and test-scope dependencies on test and
        proof-of-concept projects.</td>
    </tr>
    <tr>
        <td>SPI</td>
        <td>SPI project, defining service interaction, abstract implementations and exceptions. Must have 
        compile-scope dependencies to API projects within the same component. May have test-scope dependencies on 
        test and proof-of-concept projects.</td>
    </tr>
    <tr>
        <td>IMPLEMENTATION</td>
        <td>Implementation project, implementing service interactions from an API or SPI project, including 
        dependencies on 3rd party libraries. Must have compile-scope dependencies to API or SPI projects within the 
        same component. May have test-scope dependencies on test and proof-of-concept projects.</td>
    </tr>
    <tr>
        <td>TEST</td>
        <td>Test artifact helper project, implementing libraries facilitating testing within other projects. No 
        dependency rules.</td>
    </tr>                
    <tr>
        <td>JEE_APPLICATION</td>
        <td>Application project defining JEE-deployable artifacts (EAR/WAR/EJB). 
        Injections of implementation projects are permitted here.</td>
    </tr>
    <tr>
        <td>STANDALONE_APPLICATION</td>
        <td>Standalone application project defining runnable Java applications. 
        Injections of implementation projects are permitted here.</td>
    </tr>
    <tr>
        <td>INTEGRATION_TEST</td>
        <td>Integration test artifact helper project, used to perform automated tests for several projects. 
        No dependency rules.</td>
    </tr>
    <tr>
        <td>CODESTYLE</td>
        <td>Codestyle helper project, providing implementations for use within the build definition cycle.
        Typically used within local reactors to supply changes or augmentations to build configurations
        such as <em>checkstyle.xml</em>, or custom enforcer rule implementations. No dependency rules.</td>
    </tr>         
    <tr>
        <td>EXAMPLE</td>
        <td>Example project providing runnable example code for showing the typical scenarios of the component. 
        Should contain relevant documentation as well as cut-and-paste code. No dependency rules.</td>
    </tr>
    <tr>
        <td>PLUGIN</td>
        <td>Project defining a Maven plugin.</td>
    </tr>    
    <tr>
        <td>JAVA_AGENT</td>
        <td><em>javaagent</em> definition project, holding implementation of a JVM agent to be launched 
        in-process on the form
        <code>-javaagent:[yourpath/][agentjar].jar=[option1]=[value1],[option2]=[value2]</code>
        This project type can import/inject implementation dependencies, as it is considered an application 
        entrypoint.</td>
    </tr>
    <tr>
        <td>PROOF_OF_CONCEPT</td>
        <td>Proof-of-concept helper project, holding proof of concept implementations. No dependency rules.</td>
    </tr>                             
</table>

## What is the difference between a Parent and a Reactor POM?

The POM defines lots of configuration and build settings for Maven projects.
jGuru Codestyle divides these POMs in 2 categories:

<table>
    <tr>
        <th>Type</th>
        <th>Leaf?</th>
        <th>Children?</th>
        <th>Description</th>
    </tr>
    <tr>
        <td>Parent</td>
        <td>Yes</td>
        <td>No</td>
        <td>Used as parents for projects producing artifacts and <b>not</b> having any modules (project children)</td>
    </tr>
    <tr>
        <td>Reactor</td>
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