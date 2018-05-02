# Content and Dependency Injection (CDI) within jguru-codestyle

The [CDI Specification (JSR 299)](cdi_spec_jsr_299.pdf) provides a common
specification for type-safe injection of values within fields, as well 
as a well-defined lifecycle for beans. 

To simplify running tests containing classes relying on CDI to inject
field values, the `cdi-unit` dependency is provided as part of dependency
management within the kotlin-parent POM of the jguru codestyle. To use it
within a test, simply annotate all test classes with the `RunWith` annotation
and submit the CdiRunner as its value.

    @RunWith(CdiRunner.class)
    class SomeTestClass {
    
        ...
         
    }

This also means that the project must include the cdi-unit dependency within the pom.
The junit dependency is already included within this POM. 

        <dependencies>
            ...
            
            <dependency>
                <groupId>org.jglue.cdi-unit</groupId>
                <artifactId>cdi-unit</artifactId>
                <scope>test</scope>
            </dependency>
            
            ...
            
        </dependencies>    