package com.citytechinc.cq.groovy.testing.builders
import com.citytechinc.cq.groovy.testing.mocks.MockSlingHttpServletRequest
import com.google.common.collect.LinkedHashMultimap
import com.google.common.collect.SetMultimap

class RequestBuilder {

    def parameters = LinkedHashMultimap.create()

    def selectors = []

    def attributes = [:]

    def resourceResolver

    def path = "/"

    def method = "GET"

    def suffix = ""

    def extension = ""

    RequestBuilder(resourceResolver) {
        this.resourceResolver = resourceResolver
    }

    RequestBuilder(resourceResolver, path) {
        this.resourceResolver = resourceResolver
        this.path = path
    }

    def build(Closure closure) {
        if (closure) {
            closure.delegate = this
            closure.resolveStrategy = Closure.DELEGATE_ONLY
            closure()
        }

        buildInternal()
    }

    void parameters(Map<String, List<String>> map) {
        map.each { name, values ->
            values.each { value ->
                parameters.put(name, value)
            }
        }
    }

    void parameters(SetMultimap<String, String> map) {
        parameters.putAll(map)
    }

    void attributes(Map<String, Object> map) {
        attributes.putAll(map)
    }

    private def buildInternal() {
        def selectorString = buildSelectorString() ?: null
        def queryString = buildQueryString()

        def request = new MockSlingHttpServletRequest(resourceResolver, path, method, selectorString, extension, suffix,
            queryString, parameters)

        attributes.each { name, value ->
            request.setAttribute(name, value)
        }

        request
    }

    private def buildSelectorString() {
        def builder = new StringBuilder()

        if (selectors) {
            selectors.each { selector ->
                builder.append(selector)
                builder.append('.')
            }

            builder.deleteCharAt(builder.length() - 1)
        }

        builder.toString()
    }

    private def buildQueryString() {
        def builder = new StringBuilder()

        if (!parameters.empty) {
            parameters.keySet().each { name ->
                def values = parameters.get(name)

                values.each { value ->
                    builder.append(name)
                    builder.append('=')
                    builder.append(value)
                    builder.append('&')
                }
            }

            builder.deleteCharAt(builder.length() - 1)
        }

        builder.toString()
    }

    def methodMissing(String name, arguments) {
        if (arguments.length == 1) {
            this."$name" = arguments[0]
        }
    }
}
