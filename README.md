# Prosper

[CITYTECH, Inc.](http://www.citytechinc.com)

## Overview

Prosper is an integration testing library for AEM (Adobe Experience Manager, formerly CQ5) projects using [Spock](http://docs.spockframework.org/), a [Groovy](http://www.groovy-lang.org/)-based testing framework notable for it's expressive specification language.  The library contains a base Spock specification that provides an in-memory JCR, Sling framework support, and a mock OSGi context for registering and testing services.

## Features

* Test AEM projects outside of an OSGi container in the standard Maven build lifecycle.
* Write test specifications in [Groovy](http://www.groovy-lang.org/) using [Spock](http://docs.spockframework.org/), a JUnit-based testing framework with an elegant syntax for writing tests quickly and efficiently.
* Supplies mock OSGi bundle and [Sling contexts](https://sling.apache.org/documentation/development/sling-mock.html) for registering services, adapters, and Sling models.
* Full Sling `ResourceResolver` and `Resource` instances backed by an in-memory Jackrabbit Oak JCR instance. 
* Utilizes Groovy builders from the [AEM Groovy Extension](https://github.com/Citytechinc/aem-groovy-extension) to provide a simple DSL for creating test content.
* Provides additional builders for mock Sling requests and responses to simplify setup of test cases.
* Supports testing of JSP tag classes using a mixable Groovy trait.

## Requirements

* AEM 6.1 for versions 4.x.x and above
* AEM 6.0 for versions 3.x.x, 2.x.x, and 1.x.x (versions prior to 0.10.0 are compatible with CQ 5.6)
* Maven 3.x
* Familiarity with Groovy language and the Spock specification syntax (or see included tests for examples).

## Getting Started

Add Maven dependency to project `pom.xml`.

```xml
<dependency>
    <groupId>com.citytechinc.aem.prosper</groupId>
    <artifactId>prosper</artifactId>
    <version>7.0.0</version>
    <scope>test</scope>
</dependency>
```

Create a `src/test/groovy` directory in your project structure and add a Spock specification extending the base `ProsperSpec`.

```groovy
import com.citytechinc.aem.prosper.specs.ProsperSpec

class ExampleSpec extends ProsperSpec {

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
        assertPageExists("/content/home")
    }

    // Node metaclass provided by AEM Groovy Extension simplifies JCR operations
    def "home page has expected resource type"() {
        setup:
        def contentNode = session.getNode("/content/home/jcr:content")

        expect:
        contentNode.get("sling:resourceType") == "foundation/components/page"
    }
}
```

Configure Groovy compiler and Surefire plugin in Maven `pom.xml`.  Additional configurations details for projects with mixed Java/Groovy sources can be found [here](https://github.com/groovy/groovy-eclipse/wiki/Groovy-Eclipse-Maven-plugin).

```xml
<build>
    <sourceDirectory>src/main/groovy</sourceDirectory>
    <testSourceDirectory>src/test/groovy</testSourceDirectory>
    <plugins>
        <plugin>
            <artifactId>maven-compiler-plugin</artifactId>
            <version>3.5</version>
            <configuration>
                <compilerId>groovy-eclipse-compiler</compilerId>
                <source>1.8</source>
                <target>1.8</target>
                <encoding>utf-8</encoding>
            </configuration>
            <dependencies>
                <dependency>
                    <groupId>org.codehaus.groovy</groupId>
                    <artifactId>groovy-eclipse-compiler</artifactId>
                    <version>2.9.2-01</version>
                </dependency>
                <dependency>
                    <groupId>org.codehaus.groovy</groupId>
                    <artifactId>groovy-eclipse-batch</artifactId>
                    <version>2.4.3-01</version>
                </dependency>
            </dependencies>
        </plugin>
        <plugin>
            <artifactId>maven-surefire-plugin</artifactId>
            <version>2.19.1</version>
            <configuration>
                <includes>
                    <!-- Spock naming convention.  Change as needed if your test classes have a different naming strategy. -->
                    <include>**/*Spec*</include>
                </includes>
            </configuration>
        </plugin>
    </plugins>
</build>

<dependencies>
    <dependency>
        <groupId>org.codehaus.groovy</groupId>
        <artifactId>groovy-all</artifactId>
        <version>2.4.6</version>
    </dependency>
</dependencies>
```

Finally, run `mvn test` from the command line to verify that specifications are found and execute successfully.

## User Guide

### Specification Anatomy

The [Spock documentation](http://spockframework.github.io/spock/docs/1.0/spock_primer.html) outlines the features and methods that define a Spock specification (and their JUnit analogues, for those more familiar with Java-based testing), but the `setupSpec` fixture method is of critical importance when testing AEM classes.  This method, executed prior to the first feature method of the specification, is the conventional location for creating test content in the JCR.  Likewise, the `cleanup` and `cleanupSpec` fixture methods are the appropriate place to remove test content following the execution of a test (or set of tests).  However, the `cleanupSpec` method will be implemented less frequently, as the base `ProsperSpec` removes all test content from the JCR after every specification is executed to prevent cross-contamination of content between specifications.

```groovy
class ExampleSpec extends ProsperSpec {

    def setupSpec() {
        // runs before first feature method, create test content and initialize shared resources here
    }

    def setup() {
        // runs before each feature method, less commonly used
    }

    def "a feature method"() {
        setup:
        // create test-specific content, instances and/or mocks

        expect:
        // result
    }

    def "another feature method"() {
        setup:
        // create test content, etc.

        when:
        // stimulus

        then:
        // response
    }

    def cleanup() {
        // optionally call the method below to remove all test content after each feature method
        removeAllNodes()
    }

    def cleanupSpec() {
        // runs after all feature methods, implemented in base spec but less commonly used
    }
}
```

### Available Fields and Methods

The base specification exposes a number of fields and methods for use in tests.

Field Name | Type | Description
:---------|:---------|:-----------
session | [javax.jcr.Session](http://www.day.com/maven/jsr170/javadocs/jcr-2.0/javax/jcr/Session.html) | Administrative JCR session
resourceResolver | [org.apache.sling.api.resource.ResourceResolver](http://sling.apache.org/apidocs/sling7/org/apache/sling/api/resource/ResourceResolver.html) | Administrative Sling Resource Resolver
pageManager | [com.day.cq.wcm.api.PageManager](https://docs.adobe.com/docs/en/aem/6-1/ref/javadoc/com/day/cq/wcm/api/PageManager.html) | AEM Page Manager
nodeBuilder | [com.citytechinc.aem.groovy.extension.builders.NodeBuilder](http://code.citytechinc.com/aem-groovy-extension/groovydocs/com/citytechinc/aem/groovy/extension/builders/NodeBuilder.html) | JCR [Node Builder](https://github.com/Citytechinc/prosper#content-builders)
pageBuilder | [com.citytechinc.aem.groovy.extension.builders.PageBuilder](http://code.citytechinc.com/aem-groovy-extension/groovydocs/com/citytechinc/aem/groovy/extension/builders/PageBuilder.html) | AEM [Page Builder](https://github.com/Citytechinc/prosper#content-builders)
slingContext | [com.citytechinc.aem.prosper.context.SlingContextProvider](https://sling.apache.org/documentation/development/sling-mock.html) | Prosper extension of Sling/OSGi Context

See the `ProsperSpec` [GroovyDoc](http://code.citytechinc.com/prosper/groovydocs/com/citytechinc/aem/prosper/specs/ProsperSpec.html) for details on available methods.

### Content Builders

A test specification will often require content such as pages, components, or supporting node structures to facilitate the interactions of the class under test.  Creating a large and/or complex content hierarchy using the JCR and Sling APIs can be tedious and time consuming.  The base `ProsperSpec` simplifies the content creation process by defining two Groovy [builder](http://groovy-lang.org/dsls.html#_builders) instances, `pageBuilder` and `nodeBuilder`, that greatly reduce the amount of code needed to produce a working content structure in the JCR.

#### Page Builder

`PageBuilder` creates nodes of type `cq:Page` by default.

```groovy
def setupSpec() {
    pageBuilder.content {
        beer { // page with no title
            styles("Styles") { // create page with title "Styles"
                "jcr:content"("jcr:lastModifiedBy": "mdaugherty", "jcr:lastModified": Calendar.instance) {
                    data("sling:Folder")  // descendant of "jcr:content", argument sets node type instead of title
                    navigation("sling:resourceType: "components/navigation", "rootPath": "/content/beer")
                }
                dubbel("Dubbel")
                tripel("Tripel")
                saison("Saison")
            }
            // create a page with title "Breweries" and set properties on it's "jcr:content" node
            breweries("Breweries", "jcr:lastModifiedBy": "mdaugherty", "jcr:lastModified": Calendar.instance)
        }
    }
}
```

Nodes named `jcr:content`, however, are treated as unstructured nodes to allow the creation of descendant component/data nodes that are not of type `cq:Page`.  Descendants of `jcr:content` nodes can specify a node type using a `String` value as the first argument in the tree syntax (see `/content/beer/styles/jcr:content/data` in the above example).  As with page nodes, additional properties can be passed with a map argument.

#### Node Builder

This builder should be used when creating non-page content hierarchies, such as descendants of `/etc` in the JCR.  The syntax is similar to `PageBuilder`, but the first argument is used to specify the node type rather than the `jcr:title` property.

```groovy
def setupSpec() {
    nodeBuilder.etc { // unstructured node
        designs("sling:Folder") { // node with type
            site("sling:Folder", "jcr:title": "Site", "inceptionYear": 2014) // node with type and properties
        }
    }
}
```

The above example will create an `nt:unstructured` (the default type) node at `/etc` and `sling:Folder` nodes at `/etc/designs` and `/etc/designs/site`.  An additional map argument will set properties on the target node in the same manner as `PageBuilder`.

Both builders automatically save the underlying JCR session after executing the provided closure.

In addition to the content builders, the [session](http://www.day.com/maven/jsr170/javadocs/jcr-2.0/javax/jcr/Session.html) and [pageManager](http://dev.day.com/content/docs/en/cq/current/javadoc/com/day/cq/wcm/api/PageManager.html) instances provided by the base specification can be used directly to create test content in the JCR.

### Content Import

Another way to generate supporting content is to import a vault exported/packaged content structure.  The content import is completely automatic and will run for all of your specs when it detects content to import.  To take advantage of the content import, simply create a `SLING-INF/content` directory within your project's test resources location (ex. `src/test/resources/SLING-INF/content`).  The `content` directory must contain a child `jcr_root` directory and `META-INF` directory.  The `jcr_root` directory will contain all the vault exported/packaged content.  The `META-INF` directory will contain all the vault configuration XML files typically found within an AEM package.

#### Specifying a Filter File

You can specify an alternative `filter.xml` file by using the class level `com.citytechinc.aem.prosper.annotations.ContentFilters` annotation.  Simply provide the path to the `filter.xml` file in the XML element and it will be used instead of the `filter.xml` file within the META-INF/vault directory.  The example below shows how you can provide a path to a non-default `filter.xml` file.

```groovy
@ContentFilters(
    xml = "/SLING-INF/content/META-INF/vault/alt-filter.xml"
)
class MySpec extends ProsperSpec {

}
```

#### Dynamic Filters

You can also dynamically generate spec-specific filters by using the various content filter annotations.  This allows you to isolate the content you need for your individual specs.  The example below shows how you can define a dynamic filter.

```groovy
@ContentFilters(
    filters = [
        @ContentFilter(
            root = "/etc",
            mode = ImportMode.REPLACE,
            rules = [
                @ContentFilterRule(
                    type = ContentFilterRuleType.EXCLUDE,
                    pattern = "/etc/tags"
                )
            ]
        )
    ]
)
class MySpec extends ProsperSpec {

}
```

It is also possible to extend the provided `filter.xml` file through dynamic filters.  This allows you to provide common filters in the XML file and define specific filters for your spec with the annotations.  The example below shows how you can extend an existing `filter.xml` file.

```groovy
@ContentFilters(
    xml = "/SLING-INF/content/META-INF/vault/alt-filter.xml",
    filters = [
        @ContentFilter(
            root = "/etc",
            mode = ImportMode.REPLACE,
            rules = [
                @ContentFilterRule(
                    type = ContentFilterRuleType.EXCLUDE,
                    pattern = "/etc/tags"
                )
            ]
        )
    ]
)
class MySpec extends ProsperSpec {

}
```

#### Skipping Content Import

In some cases, you may not want to import content for your spec.  To skip the content import, annotate your spec class with `@SkipContentImport`.  The example below shows a spec that skips the content import.

```groovy
@SkipContentImport
class MySpec extends ProsperSpec {

}
```

### Metaclasses

The [AEM Groovy Extension](https://github.com/Citytechinc/aem-groovy-extension) decorates the `com.day.cq.wcm.api.Page`, `javax.jcr.Node`, and `javax.jcr.Binary` classes with additional methods to simplify common operations.  See the extension library [Groovydocs](http://code.citytechinc.com/aem-groovy-extension/groovydocs/com/citytechinc/aem/groovy/extension/metaclass/GroovyExtensionMetaClassRegistry.html) for details of these additions.  The metaclasses are registered automatically and available for use in all test methods.

### Assertions

Prosper's built-in assertion methods are used within Spock's `then` and `expect` blocks to verify the state of content in the transient repository following execution of a test.  For example, a test that creates a node with property values (either directly or as a side effect of other operations) will want to confirm that the node was created and that the desired property name and values exist in the JCR.

Since expressions in these blocks are implicitly treated as boolean conditions by Spock, Prosper's assertion methods eliminate the need to logically combine expressions for the complex conditions required to assert JCR state.  This is best illustrated with an example.

```groovy
import com.day.cq.commons.jcr.JcrConstants
import com.day.cq.wcm.api.NameConstants

def "create content"() {
    setup: "create a page with some properties"
    def pageProperties = ["sling:resourceType": "foundation/components/page",
        "jcr:description": "Prosper is an integration testing library for AEM."]

    pageBuilder.content {
        prosper("Prosper") {
            "jcr:content"(pageProperties)
        }
    }

    expect: "page is created and properties match expected values"
    session.nodeExists("/content/prosper")
    && session.getNode("/content/prosper").primaryNodeType.name == NameConstants.NT_PAGE
    && session.getNode("/content/prosper").hasNode(JcrConstants.JCR_CONTENT)
    && pageProperties.every { name, value ->
        session.getNode("/content/prosper").getNode(JcrConstants.JCR_CONTENT).get(name) == value
    }
}
```

Thankfully, the `expect` block can be simplified using an assertion method from the base `ProsperSpec`.

```groovy
expect: "page is created and properties match expected values"
assertPageExists("/content/prosper", pageProperties)
```

All available `assert...` methods are detailed in the Prosper [GroovyDocs](http://code.citytechinc.com/prosper/groovydocs/com/citytechinc/aem/prosper/specs/ProsperSpec.html).

### Mocking Requests and Responses

Testing servlets and request-scoped supporting classes require mocking the `SlingHttpServletRequest` and `SlingHttpServletResponse` objects.  The `RequestBuilder` and `ResponseBuilder` instances acquired through the base spec leverage Groovy closures to set the necessary properties and state on these mock objects with a minimal amount of initialization code.

Given a Sling servlet:

```groovy
import groovy.json.JsonBuilder
import org.apache.sling.api.SlingHttpServletRequest
import org.apache.sling.api.SlingHttpServletResponse
import org.apache.sling.api.servlets.SlingAllMethodsServlet

import javax.jcr.Session
import javax.servlet.ServletException

import static com.google.common.base.Preconditions.checkNotNull

class TestServlet extends SlingAllMethodsServlet {

    @Override
    protected void doPost(SlingHttpServletRequest request,
        SlingHttpServletResponse response) throws ServletException, IOException {
        def path = checkNotNull(request.getParameter("path"))
        def selector = request.requestPathInfo.selectors[1]

        def session = request.resourceResolver.adaptTo(Session)

        def node = session.getNode(path)

        node.setProperty "testProperty", selector

        session.save()

        new JsonBuilder([path: path, value: selector]).writeTo(response.writer)
    }
}
```

A Prosper specification for this servlet will use the request and response builders to create the necessary method arguments to simulate a POST request to the servlet and verify both the JCR content updates and the JSON output resulting from the servlet execution.

```groovy
class ServletSpec extends ProsperSpec {

    def setupSpec() {
        nodeBuilder.content {
            prosper()
        }
    }

    def "missing request parameter throws exception"() {
        setup:
        def servlet = new TestServlet()

        // requestBuilder and responseBuilder are inherited from the base spec
        def request = requestBuilder.build()
        def response = responseBuilder.build()

        when:
        servlet.doPost(request, response)

        then:
        thrown(NullPointerException)
    }

    def "valid request parameter sets node property and returns JSON response"() {
        setup:
        def servlet = new TestServlet()

        def request = requestBuilder.build {
            parameters = [path: "/content/prosper"]
            selectors = ["one", "two"]
            contentType = "application/json"
        }

        def response = responseBuilder.build()

        when:
        servlet.doPost(request, response)

        then:
        assertNodeExists("/content/prosper", [testProperty: "two"])

        and:
        response.contentAsString == '{"path":"/content/prosper","value":"two"}'
    }
}
```

The mock request and response objects delegate to the [MockHttpServletRequest](http://docs.spring.io/spring/docs/4.1.7.RELEASE/javadoc-api/org/springframework/mock/web/MockHttpServletRequest.html) and [MockHttpServletResponse](http://docs.spring.io/spring/docs/4.1.7.RELEASE/javadoc-api/org/springframework/mock/web/MockHttpServletResponse.html) objects from the Spring Test Framework.  The setter methods exposed by these classes are thus made available in the `build` closures for the request and response builders to assist in setting appropriate mock values for the class under test.

### Sling Context

See the [Groovydoc](http://code.citytechinc.com/prosper/groovydocs/com/citytechinc/aem/prosper/context/SlingContextProvider.html) for complete details of the available service registration and additional context methods; specific Sling adaptable examples are provided below.

#### OSGi Services

OSGi services can be mocked (fully or partially) using Spock's [mocking API](http://docs.spockframework.org/en/latest/interaction_based_testing.html#creating-mock-objects).  Real service objects can also be used if all required dependency services are registered in the Sling context.

```groovy
import com.day.cq.replication.ReplicationActionType
import com.day.cq.replication.ReplicationException
import com.day.cq.replication.Replicator
import groovy.util.logging.Slf4j
import org.apache.felix.scr.annotations.Reference
import org.apache.felix.scr.annotations.sling.SlingServlet
import org.apache.sling.api.SlingHttpServletRequest
import org.apache.sling.api.SlingHttpServletResponse

import javax.jcr.Session
import javax.servlet.ServletException

@SlingServlet(paths = "/bin/replicate/custom")
@Slf4j("LOG")
class CustomReplicationServlet extends SlingAllMethodsServlet {

    @Reference
    Replicator replicator

    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) throws
        ServletException, IOException {
        def path = request.getParameter("path")
        def session = request.resourceResolver.adaptTo(Session)

        try {
            replicator.replicate(session, ReplicationActionType.ACTIVATE, path)
        } catch (ReplicationException e) {
            LOG.error "replication error", e
        }
    }
}
```

The Prosper specification for this servlet can then set a mocked `Replicator` instance and verify the expected [interactions](http://docs.spockframework.org/en/latest/interaction_based_testing.html) using the Spock syntax.  Alternatively, services can inject other service references using the `slingContext.registerInjectActivateService` methods.

```groovy
def "servlet with mock service"() {
    setup:
    def servlet = new CustomReplicationServlet()
    def replicator = Mock(Replicator)
    
    slingContext.registerService(Replicator, replicator)
    slingContext.registerInjectActivateService(servlet)

    def request = requestBuilder.build {
        parameters = [path: "/content"]
    }
    
    def response = responseBuilder.build()

    when:
    servlet.doPost(request, response)

    then:
    1 * replicator.replicate(_, _, "/content")
}
```

#### Registering Adapters

Specs can register adapters in the Sling context by adding `AdapterFactory` instances or by providing mappings from adapter instances to closures that instantiate these instances from a `Resource`, `ResourceResolver` or `SlingHttpRequestServlet`.  Adapters will be registered with the mock `BundleContext` and their adaptables and adapters properties will be respected when an adapter is chosen.  Registered `AdapterFactory` instances will pull these properties from the XML metadata files located in the classpath at /OSGI-INF, or the adaptable/adapter properites can be explicitly specified in the method arguments.  Registered adapter closures will use the `Resource`, `ResourceResolver` or `SlingHttpRequestServlet` as the adaptables property and the adapter instance class as the adapters property.  The methods for registering adapters are illustrated in the examples below.

```groovy
class ExampleAdapterFactory implements AdapterFactory {

    @Override
    public <AdapterType> AdapterType getAdapter(Object adaptable, Class<AdapterType> type) {
        AdapterType result = null

        if (type == String) {
            if (adaptable instanceof ResourceResolver) {
                result = "Hello."
            } else if (adaptable instanceof Resource) {
                result = "Goodbye."
            }
        }

        result
    }
}

class ExampleSpec extends ProsperSpec {

    def setupSpec() {
        // example adapter factory
        slingContext.registerAdapterFactory(new ExampleAdapterFactory(), 
            ["org.apache.sling.api.resource.ResourceResolver", "org.apache.sling.api.resource.Resource"] as String[], 
            ["java.lang.String"] as String[])
        
        // resource adapters
        slingContext.registerResourceAdapter(Integer, { Resource resource -> 
            resource.name.length() 
        })
        slingContext.registerResourceAdapter(Map, { Resource resource -> 
            resource.metadata 
        })
        
        // resource resolver adapters
        slingContext.registerResourceResolverAdapter(Integer, { ResourceResolver resourceResolver -> 
            resourceResolver.searchPath.length 
        })
        slingContext.registerResourceResolverAdapter(Node, { ResourceResolver resourceResolver -> 
            resourceResolver.getResource("/").adaptTo(Node) 
        })
        
        // request adapters
        slingContext.registerRequestAdapter(Integer, { SlingHttpServletRequest request -> 
            request.pathInfo.length() 
        })
    }

    def "resource is adaptable to multiple types"() {
        setup:
        def resource = resourceResolver.getResource("/")

        expect:
        resource.adaptTo(type) == result

        where:
        type    | result
        Integer | 0
        Map     | [:]
    }

    def "resource resolver is adaptable to multiple types"() {
        expect:
        resourceResolver.adaptTo(String) == "Hello."
        resourceResolver.adaptTo(Integer) == 0
        resourceResolver.adaptTo(Node).path == "/"
    }

    def "request is adaptable"() {
        expect:
        requestBuilder.build {
            path = "/request/path.html"
        }.adaptTo(Integer) == 18
    }
}
```

#### Sling Models

Sling Model classes and injectors can also be registered in the Sling context for use in tests.  The `addModelsForPackage` method from the `SlingContextProvider` scans the given package for classes annotated with `@org.apache.sling.models.annotations.Model` and registers them in the mock bundle context, while the `registerInjector` allows for registration of custom Sling injectors.  A model instance can then be acquired by adapting from a Sling resource or request as shown below.

```groovy
package com.citytechinc.aem.prosper

import org.apache.sling.models.annotations.Model
import org.apache.sling.models.annotations.injectorspecific.Self

@Model(adaptables = [Resource, SlingHttpServletRequest])
class ProsperModel {

    @Self
    Resource resource

    String getPath() {
        resource.path
    }
}
```
```groovy
class ProsperModelSpec extends ProsperSpec {

    def setupSpec() {
        pageBuilder.content {
            prosper()
        }
    }

    def "adapt resource to model"() {
        setup:
        slingContext.addModelsForPackage("com.citytechinc.aem.prosper")

        def resource = getResource("/content/prosper")
        def model = resource.adaptTo(ProsperModel)

        expect:
        model.path == "/content/prosper"
    }
}

```

### Adding JCR Namespaces and Node Types

Many of the common AEM, JCR, and Sling namespaces and node types are registered when the Prosper test repository is created.  Additional namespaces and node types may be added at runtime by annotating a test spec with the `@NodeTypes` annotation and supplying an array containing paths to classpath .cnd file resources.  For more information on the CND node type notation, see [Node Type Notation](http://jackrabbit.apache.org/node-type-notation.html) in the Apache Jackrabbit documentation.  An example of the annotation usage is presented below.

```groovy
import com.citytechinc.aem.prosper.annotations.NodeTypes

@NodeTypes("SLING-INF/nodetypes/spock.cnd")
class ExampleSpec extends ProsperSpec {

}
```

### Traits

Groovy traits are a language feature that is not specific to Prosper, but can nonetheless be utilized to "mix in" new functionality to test specs.  The JSP tag trait is the only one currently provided, but custom traits can be defined to support domain-specific features.

```groovy
import com.citytechinc.aem.prosper.builders.RequestBuilder
import org.apache.sling.api.SlingHttpServletRequest

trait MobileRequestTrait {

    abstract RequestBuilder getRequestBuilder()

    SlingHttpServletRequest buildMobileRequest(Map<String, Object> parameters) {
        requestBuilder.setSelectors(["mobile"]).setParameters(parameters).build()
    }
}
```

Specs can then implement the trait to add the new functionality.  Note that the `getRequestBuilder()` abstract method does not have to be implemented by the new spec, since this spec (as seen below) already inherits the required method from `ProsperSpec`.  Traits can thus "borrow" functionality from the base `ProsperSpec` by defining abstract methods that match the corresponding method signatures in `ProsperSpec`.

```groovy
import spock.lang.Shared

class MobileRequestTraitSpec extends ProsperSpec implements MobileRequestTrait {

    def "trait usage"() {
        setup:
        def request = buildMobileRequest([:])

        expect:
        request.requestPathInfo.selectors[0] == "mobile"
    }
}
```

#### JSP Tag Trait

The `init` methods in `com.citytechinc.aem.prosper.traits.JspTagTrait` initialize `TagSupport` instances with a mock `PageContext` containing a `Writer` for capturing tag output.  The returned proxy allows test cases to evaluate page context attributes and verify the written output (i.e. calls to `pageContext.getOut().write()`.  Tags can also be initialized with additional page context attributes.

If the `init` method's `resourcePath` argument maps to valid JCR path, the mock `PageContext` will be initialized with the appropriate `Resource`, `Page`, `Node`, and `ValueMap` attributes for the addressed resource.  See the `JspTagTrait` implementation for the specific attribute names and values.

```groovy
import javax.servlet.jsp.JspException
import javax.servlet.jsp.tagext.TagSupport

class SimpleTag extends TagSupport {

    String name

    @Override
    int doStartTag() throws JspException {
        pageContext.out.write("hello")

        EVAL_PAGE
    }

    @Override
    int doEndTag() throws JspException {
        def prefix = pageContext.getAttribute("prefix") as String

        pageContext.setAttribute("name", prefix + name)

        EVAL_PAGE
    }
}
```
```groovy
import com.citytechinc.aem.prosper.traits.JspTagTrait

class SimpleTagSpec extends ProsperSpec implements JspTagTrait {

    def "start tag writes 'hello'"() {
        setup:
        def proxy = init(SimpleTag, "/")

        when:
        proxy.tag.doStartTag()

        then:
        proxy.output == "hello"
    }

    def "end tag sets page context attribute"() {
        setup:
        def proxy = init(SimpleTag, "/", ["prefix": "LiveLongAnd"])
        def tag = proxy.tag as SimpleTag

        tag.name = "Prosper"

        when:
        tag.doEndTag()

        then:
        proxy.pageContext.getAttribute("name") == "LiveLongAndProsper"
    }
}

```

## References

* [Prosper GroovyDocs](http://code.citytechinc.com/prosper/groovydocs/index.html)
* [Spock Documentation](http://spockframework.github.io/spock/docs/1.0/index.html)
* [Groovy Documentation](http://www.groovy-lang.org/documentation.html)

## Versioning

Follows [Semantic Versioning](http://semver.org/) guidelines.