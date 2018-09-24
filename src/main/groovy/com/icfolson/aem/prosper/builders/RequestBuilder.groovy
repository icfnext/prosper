package com.icfolson.aem.prosper.builders

import com.icfolson.aem.prosper.mocks.ProsperMockSlingHttpServletRequest
import org.apache.sling.api.resource.ResourceResolver
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest
import org.osgi.framework.BundleContext

/**
 * Builder to assist in creating <code>SlingHttpServletRequest</code> objects.
 */
class RequestBuilder {

    private final ResourceResolver resourceResolver

    private final BundleContext bundleContext

    /**
     * Create a request builder for a test spec.
     *
     * @param resourceResolver Sling resource resolver
     */
    RequestBuilder(ResourceResolver resourceResolver, BundleContext bundleContext) {
        this.resourceResolver = resourceResolver
        this.bundleContext = bundleContext
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
     *  new RequestBuilder(resourceResolver).build {*      serverName = "localhost"
     *      path = "/content"
     *      method = "GET"
     *      parameters = ["a": ["1", "2"], "b": ["1"]]
     *      extension = "html"
     *}</pre>
     *
     * @param closure closure that delegates to this builder and <a href="http://docs.spring.io/spring/docs/3.2.8
     * .RELEASE/javadoc-api/org/springframework/mock/web/MockHttpServletRequest.html">MockHttpServletRequest</a>
     * @return request
     */
    MockSlingHttpServletRequest build(@DelegatesTo(ProsperMockSlingHttpServletRequest) Closure closure) {
        def request = new ProsperMockSlingHttpServletRequest(resourceResolver, bundleContext)

        if (closure) {
            closure.delegate = request
            closure.resolveStrategy = Closure.DELEGATE_ONLY
            closure()
        }

        request
    }
}
