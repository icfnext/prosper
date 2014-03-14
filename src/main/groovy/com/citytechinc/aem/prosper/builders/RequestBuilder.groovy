package com.citytechinc.aem.prosper.builders

import com.citytechinc.aem.prosper.mocks.MockSlingHttpServletRequest
import com.google.common.collect.LinkedHashMultimap
import com.google.common.collect.SetMultimap
import org.apache.sling.api.SlingHttpServletRequest
import org.apache.sling.api.resource.ResourceResolver

/**
 * Builder to assist in creating <code>SlingHttpServletRequest</code> objects.
 */
class RequestBuilder {

    private final ResourceResolver resourceResolver

    private SetMultimap<String, String> parameters = LinkedHashMultimap.create()

    private List<String> selectors = []

    private Map<String, Object> attributes = [:]

    private String path = "/"

    private String method = "GET"

    private String suffix = ""

    private String extension = ""

    /**
     * Create a request builder without a preset path.
     *
     * @param resourceResolver Sling resource resolver
     */
    RequestBuilder(ResourceResolver resourceResolver) {
        this.resourceResolver = resourceResolver
    }

    /**
     * Create a request builder for the given JCR path.
     *
     * @param resourceResolver Sling resource resolver
     * @param path
     */
    RequestBuilder(ResourceResolver resourceResolver, String path) {
        this.resourceResolver = resourceResolver
        this.path = path
    }

    /**
     * Set the request path.
     *
     * @param path JCR path
     */
    void path(String path) {
        this.path = path
    }

    /**
     * Set the request method (e.g. GET, POST).
     *
     * @param method request method
     */
    void method(String method) {
        this.method = method
    }

    /**
     * Set the request suffix.
     *
     * @param suffix suffix
     */
    void suffix(String suffix) {
        this.suffix = suffix
    }

    /**
     * Set the request extension
     *
     * @param extension extension
     */
    void extension(String extension) {
        this.extension = extension
    }

    /**
     * Add selectors to the request.
     *
     * @param selectors list of selectors
     */
    void selectors(List<String> selectors) {
        this.selectors.addAll(selectors)
    }

    /**
     * Add request parameters from a map.  Map values should either be strings or lists of strings (for multivalued
     * parameters).
     *
     * @param parameters map of parameter names and values
     */
    void parameters(Map<String, Object> parameters) {
        parameters.each { name, value ->
            if (value instanceof Collection) {
                value.each {
                    this.parameters.put(name, it)
                }
            } else {
                this.parameters.put(name, value)
            }
        }
    }

    /**
     * Add request parameters from a <a href="http://docs.guava-libraries.googlecode.com/git-history/v15
     * .0/javadoc/com/google/common/collect/SetMultimap.html">SetMultimap</a>.
     *
     * @param parameters map of parameter names and values
     */
    void parameters(SetMultimap<String, String> parameters) {
        this.parameters.putAll(parameters)
    }

    /**
     * Add a request attribute.
     *
     * @param name attribute name
     * @param value attribute value
     */
    void attribute(String name, Object value) {
        attributes.put(name, value)
    }

    /**
     * Add request attributes from a map.
     *
     * @param attributes map of attribute names and values
     */
    void attributes(Map<String, Object> attributes) {
        this.attributes.putAll(attributes)
    }

    /**
     * Build a Sling request with default values.
     *
     * @return request
     */
    SlingHttpServletRequest build() {
        build(null)
    }

    /**
     * Build a Sling request using a closure to set request properties.
     *
     * <pre>
     *  new RequestBuilder(resourceResolver).build {
     *      path "/content"
     *      method "GET"
     *      extension "html"
     *  }
     * </pre>
     *
     * @param closure closure that delegates to this builder
     * @return request
     */
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

        new MockSlingHttpServletRequest(resourceResolver, path, method, selectorString, extension, suffix,
            queryString, parameters, attributes)
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
                parameters.get(name).each { value ->
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
