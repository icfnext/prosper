package com.citytechinc.aem.prosper.mocks

import com.citytechinc.aem.prosper.builders.RequestBuilder
import com.citytechinc.aem.prosper.specs.ProsperSpec
import org.apache.sling.api.resource.SyntheticResource

class MockSlingHttpServletRequestSpec extends ProsperSpec {

    def "resolve resource for path"() {
        setup:
        def request = new RequestBuilder(resourceResolver, null).build {
            path = "/content"
        }

        expect:
        !(request.resource instanceof SyntheticResource)
        request.resource.path == "/content"
    }

    def "resolve resource for non-existent path"() {
        setup:
        def request = new RequestBuilder(resourceResolver, null).build {
            path = "/content/spock"
        }

        expect:
        request.resource instanceof SyntheticResource
        request.resource.path == "/content/spock"
    }

    def "get request parameter returns null"() {
        setup:
        def request = new RequestBuilder(resourceResolver, null).build()

        expect:
        !request.getRequestParameter("a")
        !request.getRequestParameters("a")
    }

    def "get request parameter"() {
        setup:
        def request = new RequestBuilder(resourceResolver, null).build {
            parameters = ["a": ["alpha"]]
        }

        expect:
        request.getRequestParameter("a").string == "alpha"
    }

    def "get request parameters"() {
        setup:
        def request = new RequestBuilder(resourceResolver, null).build {
            parameters = ["a": ["alpha1", "alpha2"]]
        }

        expect:
        request.getRequestParameters("a")*.string == ["alpha1", "alpha2"]
    }

    def "get parameter returns null"() {
        setup:
        def request = new RequestBuilder(resourceResolver, null).build()

        expect:
        !request.getParameter("a")
        !request.getParameterValues("a")
    }

    def "get parameter"() {
        setup:
        def request = new RequestBuilder(resourceResolver, null).build {
            parameters = ["a": ["alpha"]]
        }

        expect:
        request.getParameter("a") == "alpha"
    }

    def "get parameters"() {
        setup:
        def request = new RequestBuilder(resourceResolver, null).build {
            parameters = ["a": ["alpha1", "alpha2"]]
        }

        expect:
        request.getParameterValues("a") as List == ["alpha1", "alpha2"]
    }
}
