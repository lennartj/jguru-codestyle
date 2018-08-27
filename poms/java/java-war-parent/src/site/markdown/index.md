# About jGuru-codestyle-java-war-parent

The WAR parent should be used as the parent POM of all WAR artifact projects, implying both presentation
projects and restful WARs for backend usage.

In addition to providing all facilities included in the jGuru-codestyle-java-api-parent, the 
jGuru-codestyle-java-war-parent sets up required facilities to simplify running restful unit tests. 
For this purpose, the Undertow web server is used.

### Dependency Graph

The dependency graph for this project is shown below:

![Dependency Graph](./images/dependency_graph.png)