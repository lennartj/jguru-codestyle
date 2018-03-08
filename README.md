# [jGuru Codestyle](https://lennartj.github.io/jguru-codestyle)

<img src="src/site/resources/images/jGuruLogo.png" style="float:right" width="167" height="185"/> Put simply - the 
codestyle project contains all those settings and configurations which makes your development, deployment and 
runtime execution _just work_. The jGuru Codestyle project contains a
implemented set of best-pracises to start projects quickly - and scale those projects without needing to change your
development and delivery process. This is in part usability engineering for the development process, and in part a 
lot of experience in software development ... all in one repo.

# 2. Getting and building jGuru Codestyle

The `jguru-codestyle` is a normal Git-based Maven project, which is simple to clone and quick to build.

## 2.1. Getting the repository

Clone the repository, and fetch all tags:

```
git clone https://github.com/lennartj/jguru-codestyle.git

cd jguru-codestyle

git fetch --tags
```

## 2.2. Building the `jguru-codestyle` project

For the latest development build, simply run the build against the latest master branch revision:

```
mvn clean install
```

For a particular version, checkout its release tag and build normally:

```
git checkout jguru-codestyle-1.2.4

mvn clean install
```

All tags (and hence also all release versions) are visible using the command

```
git tag -l
```

### 2.2.1. Building with different Maven versions

For building the project with another Maven version, simply run the following
script, where the `${MAVEN_VERSION}` should be substituted for a version number
such as `3.3.3`:

```
mvn -N io.takari:maven:wrapper -Dmaven=${MAVEN_VERSION}

./mvnw --show-version --errors --batch-mode validate dependency:go-offline

./mvnw --show-version --errors --batch-mode clean verify site
```

In the windows operating system, use `mvnw.bat` instead.