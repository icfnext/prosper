package com.citytechinc.aem.prosper.builders

import com.citytechinc.aem.prosper.mocks.MockSlingHttpServletRequest
import org.apache.sling.api.resource.ResourceResolver
import org.springframework.mock.web.MockHttpServletRequest

/**
 * Builder to assist in creating <code>SlingHttpServletRequest</code> objects.
 */
class RequestBuilder {

    private final ResourceResolver resourceResolver

    @Delegate
    private MockHttpServletRequest mockRequest = new MockHttpServletRequest()

    private List<String> selectors = []

    private String path = "/"

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
    void setPath(String path) {
        this.path = path
    }

    /**
     * Set the request suffix.
     *
     * @param suffix suffix
     */
    void setSuffix(String suffix) {
        this.suffix = suffix
    }

    /**
     * Set the request extension
     *
     * @param extension extension
     */
    void setExtension(String extension) {
        this.extension = extension
    }

    /**
     * Add selectors to the request.
     *
     * @param selectors list of selectors
     */
    void setSelectors(List<String> selectors) {
        this.selectors.addAll(selectors)
    }

    /**
     * Add request parameters from a map.  Map values should either be strings or lists of strings (for multivalued
     * parameters).
     *
     * @param parameters map of parameter names and values
     */
    void setParameters(Map<String, Object> parameters) {
        parameters.each { name, value ->
            if (value instanceof Collection) {
                setParameter(name, value as String[])
            } else {
                setParameter(name, value as String)
            }
        }
    }

    /**
     * Build a Sling request with default values.
     *
     * @return request
     */
    MockSlingHttpServletRequest build() {
        build(null)
    }

    /**
     * Build a Sling request using a closure to set request properties.
     *
     * <pre>
     *  new RequestBuilder(resourceResolver).build {*      path "/content"
     *      method "GET"
     *      extension "html"
     *}* </pre>
     *
     * @param closure closure that delegates to this builder
     * @return request
     */
    MockSlingHttpServletRequest build(Closure closure) {
        if (closure) {
            closure.delegate = this
            closure.resolveStrategy = Closure.DELEGATE_ONLY
            closure()
        }

        buildRequest()
    }

    private def buildRequest() {
        def selectorString = buildSelectorString() ?: null

        new MockSlingHttpServletRequest(mockRequest, resourceResolver, path, selectorString, extension, suffix)
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
}
