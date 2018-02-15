# About jguru-codestyle-kotlin-war-parent

The WAR parent should be used as the parent POM of all WAR artifact projects, implying both presentation
projects and restful WARs for backend usage.

In addition to providing all facilities included in the jguru-codestyle-kotlin-api-parent, the 
jguru-codestyle-kotlin-war-parent sets up required facilities to simplify running restful unit tests. 
For this purpose, the Undertow web server is used.