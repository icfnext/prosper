package com.icfolson.aem.prosper.builders

import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletResponse

/**
 * Builder to assist in creating <code>SlingHttpServletResponse</code> objects.
 */
class ResponseBuilder {

    /**
     * Build a Sling response with default values.
     *
     * @return response
     */
    MockSlingHttpServletResponse build() {
        build(null)
    }

    /**
     * Build a Sling response using a closure to set response properties.  The closure delegates to an instance of
     * <a href="http://static.javadoc.io/org.apache.sling/org.apache.sling.testing.sling-mock/2.2.20/org/apache/sling/testing/mock/sling/servlet/MockSlingHttpServletResponse.html">MockSlingHttpServletResponse</a>,
     * so methods on the response instance may be called directly in the closure (see example below).
     *
     * <pre>
     *  new ResponseBuilder().build {
     *      status = 200
     *      characterEncoding = "UTF-8"
     *      contentType = "application/json"
     *      addHeader "Connection", "close"
     *}</pre>
     *
     * @param closure closure that delegates to <a href="http://static.javadoc.io/org.apache.sling/org.apache.sling.testing.sling-mock/2.2.20/org/apache/sling/testing/mock/sling/servlet/MockSlingHttpServletResponse.html">MockSlingHttpServletResponse</a>
     * @return mock response
     */
    MockSlingHttpServletResponse build(@DelegatesTo(MockSlingHttpServletResponse) Closure closure) {
        def response = new MockSlingHttpServletResponse()

        if (closure) {
            closure.delegate = response
            closure.resolveStrategy = Closure.DELEGATE_ONLY
            closure()
        }

        response
    }
}
