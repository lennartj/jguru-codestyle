# jGuru Codestyle: Definitions Overview

The codestyle project contains configuration for plugins embedded in the maven build, as well as IDEs intended
to be used for development. The configuration is roughly divided into four sections:

1. **Maven Enforcer Rules**. Implementation of enforcement rules used to ensure that the structure of
    Nazgul Software Components (NSC) is correctly created, including checking correctness of maven dependency
    imports in POM files. (For example, not permitting imports of in-reactor implementation projects into other
    implementation projects).

2. **IDE configuration**. Configuration documents used to quickly set up integrated development environments.
    Currently, support is only properly devised for IntelliJ IDEA, with alpha-level setup support for Eclipse.
    While certainly not impossible to integrate, support for other IDEs is currently lacking. Refer to the
    correct IDE link in the setup instructions below.

3. **License text configuration**. License texts used within the project are kept in section three of the
    codestyle project. These texts are used to configure the
    [Maven License Plugin](http://mojo.codehaus.org/license-maven-plugin/) for use within the 
    jGuru Codestyle build reactor.

4. **Maven or Gradle plugin configurations**. Checkstyle, PMD and findbugs are normally used for Java projects to 
    ensure consistency and similarity between projects and developers. At the time of this writing, there are
    considerably fewer code quality tools which work for Kotlin. Hence, this codestyle contains fewer
    configuration settings than similar codestyle projects for Java projects. 

## Setting up your IDE : IntelliJ IDEA

<img src="images/idea_settings_jar.png" style="float:right; border: solid DarkGray 1px;" width="269" height="213"/> 
For the IntelliJ IDEA development environment, import the settings from the
`settings.jar` embedded resource, found within the codestyle/idea resource directory.
Simply select the `File | Import Settings` menu button and locate the `settings.jar` file.
 