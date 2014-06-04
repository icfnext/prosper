package com.citytechinc.aem.prosper.builders

import com.citytechinc.aem.prosper.mocks.MockSlingHttpServletRequest
import org.apache.sling.api.resource.ResourceResolver
import org.springframework.mock.web.MockHttpServletRequest

/**
 * Builder to assist in creating <code>SlingHttpServletRequest</code> objects.
 */
class RequestBuilder {

    @Delegate
    private final MockHttpServletRequest mockRequest = new MockHttpServletRequest()

    private final ResourceResolver resourceResolver

    private final List<String> selectors = []

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
     * Build a Sling request using a closure to set request properties.  The closure delegates to this builder and an
     * instance of <a href="http://docs.spring.io/spring/docs/3.2.8
     * .RELEASE/javadoc-api/org/springframework/mock/web/MockHttpServletRequest.html">MockHttpServletRequest</a>,
     * so methods for these instances may be called directly in the closure (see example below).  This pattern is
     * similar to the Groovy <a href="http://groovy.codehaus.org/groovy-jdk/java/lang/Object.html#with(groovy.lang
     * .Closure)"><code>with</code></a> method.
     *
     * <pre>
     *  new RequestBuilder(resourceResolver).build {
     *      serverName = "localhost"
     *      path = "/content"
     *      method = "GET"
     *      parameters = ["a": ["1", "2"], "b": ["1"]]
     *      extension = "html"
     * }</pre>
     *
     * @param closure closure that delegates to this builder and <a href="http://docs.spring.io/spring/docs/3.2.8
     * .RELEASE/javadoc-api/org/springframework/mock/web/MockHttpServletRequest.html">MockHttpServletRequest</a>
     * @return request
     */
    MockSlingHttpServletRequest build(Closure closure) {
        if (closure) {
            closure.delegate = this
            closure.resolveStrategy = Closure.DELEGATE_ONLY
            closure()
        }

        new MockSlingHttpServletRequest(mockRequest, resourceResolver, path, selectors, extension, suffix)
    }
}
