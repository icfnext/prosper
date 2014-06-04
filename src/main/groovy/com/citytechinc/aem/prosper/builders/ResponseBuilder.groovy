package com.citytechinc.aem.prosper.builders

import com.citytechinc.aem.prosper.mocks.MockSlingHttpServletResponse
import org.springframework.mock.web.MockHttpServletResponse

/**
 * Builder to assist in creating <code>SlingHttpServletResponse</code> objects.
 */
class ResponseBuilder {

    @Delegate
    private final MockHttpServletResponse mockResponse = new MockHttpServletResponse()

    /**
     * Build a Sling response with default values.
     *
     * @return response
     */
    MockSlingHttpServletResponse build() {
        build(null)
    }

    /**
     * Build a Sling response using a closure to set response properties.  The closure delegates to this builder and an
     * instance of <a href="http://docs.spring.io/spring/docs/3.2.8
     * .RELEASE/javadoc-api/org/springframework/mock/web/MockHttpServletResponse.html">MockHttpServletResponse</a>,
     * so methods on the response instance may be called directly in the closure (see example below).  This pattern
     * is similar to the Groovy <a href="http://groovy.codehaus.org/groovy-jdk/java/lang/Object.html#with(groovy.lang
     * .Closure)"><code>with</code></a> method.
     *
     * <pre>
     *  new ResponseBuilder().build {
     *      status = 200
     *      characterEncoding = "UTF-8"
     *      contentType = "application/json"
     *      addHeader "Connection", "close"
     * }</pre>
     *
     * @param closure closure that delegates to this builder and <a href="http://docs.spring.io/spring/docs/3.2.8
     * .RELEASE/javadoc-api/org/springframework/mock/web/MockHttpServletResponse.html">MockHttpServletResponse</a>
     * @return response
     */
    MockSlingHttpServletResponse build(Closure closure) {
        if (closure) {
            closure.delegate = mockResponse
            closure.resolveStrategy = Closure.DELEGATE_ONLY
            closure()
        }

        new MockSlingHttpServletResponse(mockResponse)
    }
}
