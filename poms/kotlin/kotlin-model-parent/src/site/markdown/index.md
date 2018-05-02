# About jguru-codestyle-kotlin-model-parent

The API parent should be used as the parent POM of all API (or SPI) artifact projects.
In addition to providing all facilities included in the `jguru-codestyle-kotlin-parent`, the 
`jguru-codestyle-kotlin-api-parent` generates a JAR manifest which contains export statements 
for all packages within the project. Such a manifest assists in accessing classes and 
resources from within the JAR if executing within a classloader-restricted runtime 
environment such as OSGi. Moreover, the kotlin-api-parent generates a module-info file 
compliant with Java 9 indicating that all packages/types defined in APIs should be public.