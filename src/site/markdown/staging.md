# Creating a (staged) Documentation site

Maven's [Site Plugin](http://maven.apache.org/plugins/maven-site-plugin/) is a nifty, powerful and rather complex
plugin, which generates much required documentation for Maven reactor builds. The site plugin is configured for
somewhat sensible default values within the jGuru Codestyle reactor. These defaults - and how to change them to match
your requirements - are shown below.

> What's a *staged* Documentation site?
>
> A *staged site* means that documentation is aggregated from several projects within a
> [multi-project maven reactor](http://maven.apache.org/plugins/maven-site-plugin/examples/multimodule.html).
> The [jGuru Codestyle](https://github.com/lennartj/jguru-codestyle) and 
> [jGuru Shared Components](https://github.com/lennartj/jguru-shared) are two examples of multi-project reactors 
> which contain documentation intended for staging.

## Building the Documentation site

Maven normally builds documentation sites using the standard commands

    mvn site

    mvn site:stage

These commands are assumed to be executed in the top directory of the maven build reactor.
When executed from the jGuru Codestyle repository root directory, these commands produce the staged site
in the directory `/tmp/jguru-codestyle/Documentation/${project.version}`, where `${project.version}` is
substituted for the actual version of the Nazgul Tools reactor.

### Changing the local staging directory

Add the parameter `site.staging.localDirectory` to the stage command to make the staged
site appear in another directory:

    mvn site

    mvn site:stage -Dsite.staging.localDirectory=/another/location

Again, these commands are assumed to be executed in the top directory of the maven build reactor.
When executed from the jGuru Codestyle root directory, these commands produce the staged site
in the directory `/another/location/Documentation/${project.version}`, where `${project.version}`
is substituted for the actual version of the jGuru Codestyle reactor.