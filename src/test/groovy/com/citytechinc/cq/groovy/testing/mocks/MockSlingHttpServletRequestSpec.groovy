package com.citytechinc.cq.groovy.testing.mocks

import com.citytechinc.cq.groovy.testing.builders.RequestBuilder
import com.citytechinc.cq.groovy.testing.specs.AbstractSlingRepositorySpec
import org.apache.sling.api.resource.SyntheticResource

class MockSlingHttpServletRequestSpec extends AbstractSlingRepositorySpec {

    def "resolve resource for path"() {
        setup:
        session.rootNode.addNode("content")
        session.save()

        def request = new RequestBuilder(resourceResolver).build {
            path "/content"
        }

        expect:
        !(request.resource instanceof SyntheticResource)
        request.resource.path == "/content"
    }

    def "resolve resource for non-existent path"() {
        setup:
        def request = new RequestBuilder(resourceResolver).build {
            path "/content/foo"
        }

        expect:
        request.resource instanceof SyntheticResource
        request.resource.path == "/content/foo"
    }

    def "get request parameter returns null"() {
        setup:
        def request = new RequestBuilder(resourceResolver).build()

        expect:
        !request.getRequestParameter("a")
        !request.getRequestParameters("a")
    }

    def "get request parameter"() {
        setup:
        def request = new RequestBuilder(resourceResolver).build {
            parameters map
        }

        expect:
        request.getRequestParameter(name).string == value

        where:
        map              | name | value
        ["a": ["alpha"]] | "a"  | "alpha"
    }

    def "get request parameters"() {
        setup:
        def request = new RequestBuilder(resourceResolver).build {
            parameters map
        }

        expect:
        request.getRequestParameters(name)*.string == values

        where:
        map                         | name | values
        ["a": ["alpha1", "alpha2"]] | "a"  | ["alpha1", "alpha2"]
    }

    def "get parameter returns null"() {
        setup:
        def request = new RequestBuilder(resourceResolver).build()

        expect:
        !request.getParameter("a")
        !request.getParameterValues("a")
    }

    def "get parameter"() {
        setup:
        def request = new RequestBuilder(resourceResolver).build {
            parameters map
        }

        expect:
        request.getParameter(name) == value

        where:
        map              | name | value
        ["a": ["alpha"]] | "a"  | "alpha"
    }

    def "get parameters"() {
        setup:
        def request = new RequestBuilder(resourceResolver).build {
            parameters map
        }

        expect:
        request.getParameterValues(name) as List == values

        where:
        map                         | name | values
        ["a": ["alpha1", "alpha2"]] | "a"  | ["alpha1", "alpha2"]
    }
}
