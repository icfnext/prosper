# Prosper

[CITYTECH, Inc.](http://www.citytechinc.com)

## Overview

Prosper is an integration testing library for AEM (Adobe CQ) projects using [Spock](http://spockframework.org/), a [Groovy](http://groovy.codehaus.org)-based testing framework notable for it's expressive specification language.  The library contains a base Spock specification using an in-memory repository for JCR session-based testing and also includes basic Sling request and resource implementations for testing interactions between CQ objects.

## Features

* Test AEM projects outside of an OSGi container in the standard Maven build lifecycle.
* Write test specifications in [Groovy](http://groovy.codehaus.org) using [Spock](http://spockframework.org/), a JUnit-based testing framework with an elegant syntax for writing tests more quickly and efficiently.
* Extends and augments the transient JCR implementation provided by the [Apache Sling Testing Tools](http://sling.apache.org/documentation/development/sling-testing-tools.html) to eliminate the need to deploy tests in OSGi bundles for most testing scenarios.
* While accepting the limitations of testing outside the container, provides minimal/mock implementations of Sling interfaces (e.g. `ResourceResolver`, `SlingHttpServletRequest`) to test common API usages.
* Utilizes Groovy builders from our [AEM Groovy Extension](https://github.com/Citytechinc/aem-groovy-extension) to provide a simple DSL for creating test content.
* Provides additional builders for Sling requests and responses to simplify setup of test cases.

## Requirements

* Maven 3.x
* Familiarity with Groovy language and the Spock specification syntax (or see included tests for examples).

## Getting Started

Add Maven dependency to project `pom.xml`.

```xml
<dependency>
    <groupId>com.citytechinc.aem.prosper</groupId>
    <artifactId>prosper</artifactId>
    <version>0.9.0</version>
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

Configure Groovy compiler and Surefire plugin in Maven `pom.xml`.  Additional configurations details can be found [here](http://groovy.codehaus.org/Groovy-Eclipse+compiler+plugin+for+Maven).

```xml
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
```

Finally, run `mvn test` from the command line to verify that specifications are found and execute successfully.

## User Guide

### Specification Anatomy

The [Spock documentation](https://code.google.com/p/spock/wiki/SpockBasics) outlines the features and methods that define a Spock specification (and their JUnit analogues, for those more familiar with Java-based testing), but the `setupSpec` fixture method is of critical importance when testing AEM classes.  This method, executed prior to the first feature method of the specification, is the conventional location for creating test content in the JCR.  Likewise, the `cleanup` and `cleanupSpec` fixture methods are the appropriate place to remove test content following the execution of a test (or set of tests).  However, the `cleanupSpec` method will be implemented less frequently, as the base `ProsperSpec` removes all test content from the JCR after every specification is executed to prevent cross-contamination of content between specifications.

```groovy
class ExampleSpec extends ProsperSpec {

    def setupSpec() {
        // runs before first feature method, create test content here
    }

    def setup() {
        // runs before each feature method, less commonly used
    }

    def "a feature method"() {
        setup:
        // create test-specific content, instances and/or mocks

        expect:
        // stimulus/response
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

### Content Builders

A test specification will often require content such as pages, components, or supporting node structures to facilitate the interactions of the class under test.  Creating a large and/or complex content hierarchy using the JCR and Sling APIs can be tedious and time consuming.  The base `ProsperSpec` simplifies the content creation process by defining two Groovy [builder](http://groovy.codehaus.org/Builders) instances, `pageBuilder` and `nodeBuilder`, that greatly reduce the amount of code needed to produce a working content structure in the JCR.

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

In addition to the provided builders, the [session](http://www.day.com/maven/jsr170/javadocs/jcr-2.0/javax/jcr/Session.html) and [pageManager](http://dev.day.com/content/docs/en/cq/current/javadoc/com/day/cq/wcm/api/PageManager.html) instances provided by the base specification can be used directly to create test content in the JCR.

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

All available `assert...` methods are detailed in the Prosper [GroovyDoc](http://code.citytechinc.com/prosper/groovydoc/com/citytechinc/aem/prosper/specs/ProsperSpec.html).

### Mocking Requests and Responses

Testing servlets and request-scoped supporting classes require mocking the `SlingHttpServletRequest` and `SlingHttpServletResponse` objects.  The `RequestBuilder` and `ResponseBuilder` instances acquired through the base spec leverage Groovy closures to set the necessary properties and state on these mock objects in a lightweight manner.

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

The mock request and response objects delegate to the [MockHttpServletRequest](http://docs.spring.io/spring/docs/3.2.8.RELEASE/javadoc-api/org/springframework/mock/web/MockHttpServletRequest.html) and [MockHttpServletResponse](http://docs.spring.io/spring/docs/3.2.8.RELEASE/javadoc-api/org/springframework/mock/web/MockHttpServletResponse.html) objects from the Spring Test Framework.  The setter methods exposed by these classes are thus made available in the `build` closures for the request and response builders to assist in setting appropriate mock values for the class under test.

### Adding Sling Adapters

Specs can add adapters by adding `AdapterFactory` instances or by providing mappings from adapter instances to closures that instantiate these instances from either a `Resource` or a `ResourceResolver`.  The methods for adding adapters are illustrated in the examples below.

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

    @Override
    Collection<AdapterFactory> addAdapterFactories() {
        [new ExampleAdapterFactory(), new OtherAdapterFactory()]
    }

    @Override
    Map<Class, Closure> addResourceAdapters() {
        def adapters = [:]

        // key is adapter type, value is closure that returns adapter instance from resource argument
        adapters[Integer] = { Resource resource -> resource.name.length() }
        adapters[Map] = { Resource resource -> resource.resourceMetadata }

        adapters
    }

    @Override
    Map<Class, Closure> addResourceResolverAdapters() {
        def adapters = [:]

        // key is adapter type, value is closure that returns adapter instance from resource resolver argument
        adapters[Integer] = { ResourceResolver resourceResolver -> resourceResolver.searchPath.length }
        adapters[Node] = { ResourceResolver resourceResolver -> resourceResolver.getResource("/").adaptTo(Node) }

        adapters
    }

    def "resource is adaptable to multiple types"() {
        setup:
        def resource = resourceResolver.getResource("/")

        expect:
        resource.adaptTo(Integer) == 0
        resource.adaptTo(Map).size() == 0
    }

    def "resource resolver is adaptable to multiple types"() {
        expect:
        resourceResolver.adaptTo(String) == "Hello."
        resourceResolver.adaptTo(Integer) == 0
        resourceResolver.adaptTo(Node).path == "/"
    }
}
```

### Mocking Services

OSGi services can be mocked (fully or partially) using Spock's [mocking API](http://docs.spockframework.org/en/latest/interaction_based_testing.html#creating-mock-objects).  Classes that inject services using the [Apache Felix SCR annotations](http://felix.apache.org/documentation/subprojects/apache-felix-maven-scr-plugin/scr-annotations.html) (as in the example servlet below) should use `protected` visibility to allow setting of service fields to mocked instances during testing.

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
    protected Replicator replicator

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

The Prosper specification for this servlet can then set a mocked `Replicator` instance and verify the expected [interactions](http://docs.spockframework.org/en/latest/interaction_based_testing.html) using the Spock  syntax.

```groovy
def "servlet with mock service"() {
    setup:
    def servlet = new CustomReplicationServlet()
    def replicator = Mock(Replicator)

    servlet.replicator = replicator

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

### Testing JSP Tag Libraries

`JspTagSpec` is a separate base spec for testing JSP tag libraries; this spec extends `ProsperSpec` and includes all of the functionality described thus far in addition to some tag-specific considerations.

Tag specs are required to implement the `createTag` method to instantiate the tag under test.  The base spec automatically handles the mocking of the underlying page context and JSP writer to capture tag output and provides an accessor method for tests to verify the output value of tag operations.

```groovy
import javax.servlet.jsp.JspException
import javax.servlet.jsp.tagext.TagSupport

class SimpleTag extends TagSupport {

    @Override
    int doEndTag() throws JspException {
        pageContext.out.write("hello")

        EVAL_PAGE
    }
}
```
```groovy
class SimpleTagSpec extends JspTagSpec {

    @Override
    TagSupport createTag() {
        new SimpleTag()
    }

    def "get result"() {
        when:
        tag.doEndTag() // 'tag' field is exposed by base class

        then:
        result == "hello" // 'result' is Groovy shorthand for getResult method from base spec
    }
}

```

Tag specs can also override the `addPageContextAttributes` method to populate the mocked page context with additional attribute key-value pairs.

### References

* [Prosper GroovyDocs](http://code.citytechinc.com/prosper/groovydoc/index.html)
* [Spock Documentation](http://docs.spockframework.org/en/latest/index.html)
* [Spock Wiki](https://code.google.com/p/spock/w/list)
* [Groovy Documentation](http://groovy.codehaus.org/Documentation)

## Versioning

Follows [Semantic Versioning](http://semver.org/) guidelines.