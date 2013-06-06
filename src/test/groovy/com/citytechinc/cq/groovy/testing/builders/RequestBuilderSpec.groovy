package com.citytechinc.cq.groovy.testing.builders

import com.citytechinc.cq.groovy.testing.specs.AbstractSlingRepositorySpec
import com.google.common.collect.LinkedHashMultimap
import spock.lang.Unroll

class RequestBuilderSpec extends AbstractSlingRepositorySpec {

    def setupSpec() {
        session.rootNode.addNode("content")
        session.save()
    }

    def "build basic request"() {
        setup:
        def builder = new RequestBuilder(resourceResolver, "/content")

        def request = builder.build()
        def requestPathInfo = request.requestPathInfo

        expect:
        request.resource.path == "/content"
        request.queryString == ""
        !request.requestParameterMap
        !requestPathInfo.selectorString
        requestPathInfo.extension == ""
        requestPathInfo.suffix == ""
        requestPathInfo.resourcePath == "/content"
    }

    def "build complex request"() {
        setup:
        def builder = new RequestBuilder(resourceResolver, "/content")

        builder.setExtension(extension)
        builder.setSuffix(suffix)

        def requestPathInfo = builder.build().requestPathInfo

        expect:
        requestPathInfo.extension == extension
        requestPathInfo.suffix == suffix

        where:
        extension | suffix
        ""        | ""
        "html"    | "/a/b"
    }

    @Unroll
    def "build request with selector"() {
        setup:
        def builder = new RequestBuilder(resourceResolver, "/content")

        when:
        builder.addSelector(selector)

        and:
        def requestPathInfo = builder.build().requestPathInfo

        then:
        selectors.equals(requestPathInfo.selectors)
        requestPathInfo.selectorString == selectorString

        where:
        selector | selectors | selectorString
        ""       | []        | null
        "a"      | ["a"]     | "a"
    }

    @Unroll
    def "build request with selectors"() {
        setup:
        def builder = new RequestBuilder(resourceResolver, "/content")

        when:
        builder.addSelectors(selectors)

        and:
        def requestPathInfo = builder.build().requestPathInfo

        then:
        requestPathInfo.selectors as List == selectors
        requestPathInfo.selectorString == selectorString

        where:
        selectors  | selectorString
        []         | null
        ["a"]      | "a"
        ["a", "b"] | "a.b"
    }

    @Unroll
    def "build request with parameters"() {
        setup:
        def builder = new RequestBuilder(resourceResolver, "/content")

        when:
        def parameters = LinkedHashMultimap.create()

        map.each { name, values ->
            values.each { value ->
                parameters.put(name, value)
            }
        }

        builder.addParameters(parameters)

        and:
        def request = builder.build()

        then:
        request.queryString == queryString

        where:
        map                           | queryString
        [:]                           | ""
        ["a": ["1"]]                  | "a=1"
        ["a": ["1", "2"]]             | "a=1&a=2"
        ["a": ["1", "2"], "b": ["3"]] | "a=1&a=2&b=3"
    }

    def "build request with attributes"() {
        setup:
        def builder = new RequestBuilder(resourceResolver, "/content")

        when:
        builder.addAttribute("a", "1")
        builder.addAttribute("b", BigDecimal.ZERO)

        and:
        def request = builder.build()

        then:
        request.getAttribute("a") == "1"
        request.getAttribute("b") == BigDecimal.ZERO
    }
}
