# CITYTECH, Inc. Spock AEM Testing Library

[CITYTECH, Inc.](http://www.citytechinc.com)

## Overview

Testing library for Adobe AEM (CQ5) projects using [Spock](http://spockframework.org/), a Groovy-based testing framework notable for it's expressive specification language.  The library contains abstract Spock specifications using an in-memory repository for JCR session-based testing and also includes basic Sling resource implementations for testing interactions between CQ objects.

## Features

* Test AEM projects outside of the OSGi container in the standard Maven build lifecycle.
* Write test specifications in [Groovy](http://groovy.codehaus.org) using Spock, a JUnit-based testing framework with an elegant syntax for writing tests more quickly and efficiently.
* Extends and augments the transient JCR implementation provided by Apache Sling Testing Tools (link) to eliminate the need to deploy tests in OSGi bundles.
* While accepting the limitations of testing outside the container, provides minimal implementations of required classes (e.g. `ResourceResolver`, `SlingHttpServletRequest`) to test common API usages.
* Utilizes Groovy's builder syntax to provide a simple [DSL](link) for creating test content.
* Provides additional builders for Sling requests and responses to simplify setup of test cases.

## Getting Started

1. Add dependency to project POM.

    &lt;dependency&gt;
        &lt;groupId&gt;com.citytechinc.cq&lt;/groupId&gt;
        &lt;artifactId&gt;cq-groovy-testing&lt;/artifactId&gt;
        &lt;version&gt;0.5.0&lt;/version&gt;
        &lt;scope&gt;test&lt;/scope&gt;
    &lt;/dependency&gt;

2. ...

## Versioning

Follows [Semantic Versioning](http://semver.org/) guidelines.