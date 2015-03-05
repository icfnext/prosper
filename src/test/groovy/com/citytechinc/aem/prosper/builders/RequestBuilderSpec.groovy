package com.citytechinc.aem.prosper.builders

import com.citytechinc.aem.prosper.specs.ProsperSpec
import spock.lang.Unroll

@Unroll
class RequestBuilderSpec extends ProsperSpec {

    def "build request with no arguments"() {
        setup:
        def request = requestBuilder.build()
        def requestPathInfo = request.requestPathInfo

        expect:
        request.resource.path == "/"
        !request.queryString
        !request.requestParameterMap
        !requestPathInfo.selectorString
        !requestPathInfo.extension
        !requestPathInfo.suffix
    }

    def "build request"() {
        setup:
        def request = requestBuilder.build {
            path = "/content"
        }

        expect:
        request.resource.path == "/content"
    }

    def "build complex request"() {
        setup:
        def request = requestBuilder.build {
            path = "/content"
            method = testMethod
            suffix = testSuffix
            extension = testExtension
        }

        def requestPathInfo = request.requestPathInfo

        expect:
        request.method == testMethod
        requestPathInfo.extension == testExtension
        requestPathInfo.suffix == testSuffix

        where:
        testMethod | testExtension | testSuffix
        "GET"      | ""            | ""
        "POST"     | "html"        | "/a/b"
    }

    def "build request with selectors"() {
        setup:
        def request = requestBuilder.build {
            path = "/content"
            selectors = selectorList
        }

        def requestPathInfo = request.requestPathInfo

        expect:
        requestPathInfo.selectors as List == selectorList
        requestPathInfo.selectorString == selectorString

        where:
        selectorList | selectorString
        []           | null
        ["a"]        | "a"
        ["a", "b"]   | "a.b"
    }

    def "build request with parameters argument"() {
        setup:
        def request = requestBuilder.build {
            path = "/content"
            parameters = ["a": ["1", "2"], "b": ["1"]]
        }

        expect:
        request.queryString == "a=1&a=2&b=1"
    }

    def "build request with parameters"() {
        setup:
        def request = requestBuilder.build {
            path = "/content"
            parameters = map
        }

        expect:
        request.queryString == queryString

        where:
        map                                     | queryString
        [:]                                     | ""
        ["a": ["1"]]                            | "a=1"
        ["a": "1"]                              | "a=1"
        ["a": ["1", "2"]]                       | "a=1&a=2"
        ["a": ["1", "2"], "b": ["3"]]           | "a=1&a=2&b=3"
        ["a": ["1", "2"], "b": ["3"], "c": "4"] | "a=1&a=2&b=3&c=4"
    }

    def "build request with attributes"() {
        setup:
        def request = requestBuilder.build {
            path = "/content"
            setAttribute "a", "1"
            setAttribute "b", BigDecimal.ZERO
        }

        expect:
        request.getAttribute("a") == "1"
        request.getAttribute("b") == BigDecimal.ZERO
    }
}
