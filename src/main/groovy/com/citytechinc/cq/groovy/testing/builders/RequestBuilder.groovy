package com.citytechinc.cq.groovy.testing.builders

import com.citytechinc.cq.groovy.testing.mocks.MockSlingHttpServletRequest
import com.google.common.collect.LinkedHashMultimap
import com.google.common.collect.SetMultimap
import org.apache.sling.api.SlingHttpServletRequest

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

    void path(String path) {
        this.path = path
    }

    void method(String method) {
        this.method = method
    }

    void suffix(String suffix) {
        this.suffix = suffix
    }

    void extension(String extension) {
        this.extension = extension
    }

    void selectors(List<String> selectors) {
        this.selectors.addAll(selectors)
    }

    void parameters(Map<String, List<String>> parameters) {
        parameters.each { name, values ->
            values.each { value ->
                this.parameters.put(name, value)
            }
        }
    }

    void parameters(SetMultimap<String, String> parameters) {
        this.parameters.putAll(parameters)
    }

    void attributes(Map<String, Object> attributes) {
        this.attributes.putAll(attributes)
    }

    SlingHttpServletRequest build() {
        build(null)
    }

    SlingHttpServletRequest build(Closure closure) {
        if (closure) {
            closure.delegate = this
            closure.resolveStrategy = Closure.DELEGATE_ONLY
            closure()
        }

        buildRequest()
    }

    private def buildRequest() {
        def selectorString = buildSelectorString() ?: null
        def queryString = buildQueryString()

        def request = new MockSlingHttpServletRequest(resourceResolver, path, method, selectorString, extension, suffix,
            queryString, parameters, attributes)

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
}
