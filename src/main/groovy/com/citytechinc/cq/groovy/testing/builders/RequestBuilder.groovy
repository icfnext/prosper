package com.citytechinc.cq.groovy.testing.builders

import com.citytechinc.cq.groovy.testing.mocks.request.MockSlingHttpServletRequest
import com.google.common.collect.LinkedHashMultimap
import com.google.common.collect.SetMultimap

class RequestBuilder {

    def parameters = LinkedHashMultimap.create()

    def selectors = []

    def attributes = [:]

    def resourceResolver

    def path

    def suffix = ""

    def extension = ""

    RequestBuilder(resourceResolver, path) {
        this.resourceResolver = resourceResolver
        this.path = path
    }

    def build() {
        def selectorString = buildSelectorString() ?: null
        def queryString = buildQueryString()

        def request = new MockSlingHttpServletRequest(resourceResolver, path, selectorString, extension, suffix,
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

    RequestBuilder addParameters(SetMultimap<String, String> parameters) {
        this.parameters.putAll(parameters)

        this
    }

    RequestBuilder addParameter(String name, String value) {
        parameters.put(name, value)

        this
    }

    RequestBuilder addSelectors(List<String> selectors) {
        this.selectors.addAll(selectors)

        this
    }

    RequestBuilder addSelector(String selector) {
        selectors.add(selector)

        this
    }

    RequestBuilder addAttribute(String name, Object value) {
        attributes[name] = value

        this
    }

    RequestBuilder setSuffix(suffix) {
        this.suffix = suffix

        this
    }

    RequestBuilder setExtension(extension) {
        this.extension = extension

        this
    }
}
