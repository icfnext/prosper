# Spock AEM Testing Library

[CITYTECH, Inc.](http://www.citytechinc.com)

## Overview

Integration testing library for AEM (Adobe CQ) projects using [Spock](http://spockframework.org/), a Groovy-based testing framework notable for it's expressive specification language.  The library contains abstract Spock specifications using an in-memory repository for JCR session-based testing and also includes basic Sling resource implementations for testing interactions between CQ objects.

## Features

* Test AEM projects outside of an OSGi container in the standard Maven build lifecycle.
* Write test specifications in [Groovy](http://groovy.codehaus.org) using [Spock](http://spockframework.org/), a JUnit-based testing framework with an elegant syntax for writing tests more quickly and efficiently.
* Extends and augments the transient JCR implementation provided by the Apache Sling Testing Tools (link) to eliminate the need to deploy tests in OSGi bundles.
* While accepting the limitations of testing outside the container, provides minimal implementations of required classes (e.g. `ResourceResolver`, `SlingHttpServletRequest`) to test common API usages.
* Utilizes Groovy builders from the [AEM Groovy Extension](http://code.citytechinc.com/aem-groovy-extension) to provide a simple DSL for creating test content.
* Provides additional builders for Sling requests and responses to simplify setup of test cases.

## Requirements

* Maven 3.x
* Familiarity with Spock specification syntax (or see included tests for examples).

## Getting Started

1. Add Maven dependency to project `pom.xml`.

        <dependency>
            <groupId>com.citytechinc.aem.spock</groupId>
            <artifactId>spock-aem</artifactId>
            <version>0.6.0</version>
            <scope>test</scope>
        </dependency>

2. Create a `src/test/groovy` directory in your project structure and add a Spock specification.

        import com.citytechinc.aem.spock.specs.AbstractSlingRepositorySpec

        class ExampleSpec extends AbstractSlingRepositorySpec {

            def setupSpec() {
                // use PageBuilder from base spec to create test content
                pageBuilder.content {
                    home("Home") {
                        "jcr:content"("sling:resourceType": "foundation/components/page")
                    }
                }
            }

            // basic content assertions provided
            def "home page exists"() {
                expect:
                assertNodeExists("/content/home")
            }

            // Node metaclass provided by AEM Groovy Extension simplifies JCR operations
            def "home page has expected resource type"() {
                setup:
                def contentNode = session.getNode("/content/home/jcr:content")

                expect:
                contentNode.get("sling:resourceType") == "foundation/components/page"
            }
        }

3. Configure Groovy compiler and Surefire plugin in Maven `pom.xml`.

        <build>
            <sourceDirectory>src/main/groovy</sourceDirectory>
            <testSourceDirectory>src/test/groovy</testSourceDirectory>
            <plugins>
                <plugin>
                    <groupId>org.codehaus.groovy</groupId>
                    <artifactId>groovy-eclipse-compiler</artifactId>
                    <version>2.8.0-01</version>
                    <extensions>true</extensions>
                    <dependencies>
                        <dependency>
                            <groupId>org.codehaus.groovy</groupId>
                            <artifactId>groovy-eclipse-batch</artifactId>
                            <version>2.1.8-01</version>
                        </dependency>
                    </dependencies>
                </plugin>
                <plugin>
                    <artifactId>maven-compiler-plugin</artifactId>
                    <version>3.1</version>
                    <configuration>
                        <compilerId>groovy-eclipse-compiler</compilerId>
                        <source>1.6</source>
                        <target>1.6</target>
                        <encoding>utf-8</encoding>
                    </configuration>
                    <dependencies>
                        <dependency>
                            <groupId>org.codehaus.groovy</groupId>
                            <artifactId>groovy-eclipse-compiler</artifactId>
                            <version>2.8.0-01</version>
                        </dependency>
                        <dependency>
                            <groupId>org.codehaus.groovy</groupId>
                            <artifactId>groovy-eclipse-batch</artifactId>
                            <version>2.1.8-01</version>
                        </dependency>
                    </dependencies>
                </plugin>
                <plugin>
                    <artifactId>maven-surefire-plugin</artifactId>
                    <version>2.16</version>
                    <configuration>
                        <includes>
                            <include>**/*Spec*</include>
                        </includes>
                    </configuration>
                </plugin>
            </plugins>
        </build>

4. Run `mvn test` from the command line to verify that specifications are found and execute successfully.

## Versioning

Follows [Semantic Versioning](http://semver.org/) guidelines.