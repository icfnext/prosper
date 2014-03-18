package com.citytechinc.aem.prosper.builders

import com.citytechinc.aem.prosper.mocks.MockSlingHttpServletResponse
import org.springframework.mock.web.MockHttpServletResponse

/**
 * Builder to assist in creating <code>SlingHttpServletResponse</code> objects.
 */
class ResponseBuilder {

    @Delegate(includes = ["addCookie", "addDateHeader", "addHeader", "addIncludedUrl", "addIntHeader", "setBufferSize",
        "setCharacterEncoding", "setContentLength", "setContentType", "setDateHeader", "setForwardedUrl", "setHeader",
        "setIncludedUrl", "setIntHeader", "setLocale", "setStatus"])
    private MockHttpServletResponse mockResponse = new MockHttpServletResponse()

    /**
     * Build a Sling response with default values.
     *
     * @return response
     */
    MockSlingHttpServletResponse build() {
        build(null)
    }

    /**
     * Build a Sling response using a closure to set response properties.
     *
     * <pre>
     *  new ResponseBuilder().build {*      status 200
     *      mediaType MediaType.JSON_UTF_8
     *}* </pre>
     *
     * @param closure closure that delegates to this builder
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
