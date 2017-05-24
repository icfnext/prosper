package com.icfolson.aem.prosper.builders

import com.icfolson.aem.prosper.mocks.MockSlingHttpServletResponse
import org.springframework.mock.web.MockHttpServletResponse

import javax.servlet.http.Cookie

/**
 * Builder to assist in creating <code>SlingHttpServletResponse</code> objects.
 */
class ResponseBuilder {

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
    MockSlingHttpServletResponse build(@DelegatesTo(ResponseBuilder) Closure closure) {
        if (closure) {
            closure.delegate = this
            closure.resolveStrategy = Closure.DELEGATE_ONLY
            closure()
        }

        new MockSlingHttpServletResponse(mockResponse)
    }

    // delegate methods

    ResponseBuilder addCookie(Cookie cookie) {
        mockResponse.addCookie(cookie)
        this
    }

    ResponseBuilder addDateHeader(String name, long value) {
        mockResponse.addDateHeader(name, value)
        this
    }

    ResponseBuilder addHeader(String name, String value) {
        mockResponse.addHeader(name, value)
        this
    }

    ResponseBuilder addIncludedUrl(String includedUrl) {
        mockResponse.addIncludedUrl(includedUrl)
        this
    }

    ResponseBuilder addIntHeader(String name, int value) {
        mockResponse.addIntHeader(name, value)
        this
    }

    ResponseBuilder setBufferSize(int bufferSize) {
        mockResponse.setBufferSize(bufferSize)
        this
    }

    ResponseBuilder setCharacterEncoding(String characterEncoding) {
        mockResponse.setCharacterEncoding(characterEncoding)
        this
    }

    ResponseBuilder setCommitted(boolean committed) {
        mockResponse.setCommitted(committed)
        this
    }

    ResponseBuilder setContentLength(int contentLength) {
        mockResponse.setContentLength(contentLength)
        this
    }

    ResponseBuilder setContentLengthLong(long contentLength) {
        mockResponse.setContentLengthLong(contentLength)
        this
    }

    ResponseBuilder setContentType(String contentType) {
        mockResponse.setContentType(contentType)
        this
    }

    ResponseBuilder setDateHeader(String name, long value) {
        mockResponse.setDateHeader(name, value)
        this
    }

    ResponseBuilder setForwardedUrl(String forwardedUrl) {
        mockResponse.setForwardedUrl(forwardedUrl)
        this
    }

    ResponseBuilder setHeader(String name, String value) {
        mockResponse.setHeader(name, value)
        this
    }

    ResponseBuilder setIncludedUrl(String includedUrl) {
        mockResponse.setIncludedUrl(includedUrl)
        this
    }

    ResponseBuilder setIntHeader(String name, int value) {
        mockResponse.setIntHeader(name, value)
        this
    }

    ResponseBuilder setLocale(Locale locale) {
        mockResponse.setLocale(locale)
        this
    }

    ResponseBuilder setOutputStreamAccessAllowed(boolean outputStreamAccessAllowed) {
        mockResponse.setOutputStreamAccessAllowed(outputStreamAccessAllowed)
        this
    }

    ResponseBuilder setStatus(int status) {
        mockResponse.setStatus(status)
        this
    }

    ResponseBuilder setStatus(int status, String errorMessage) {
        mockResponse.setStatus(status, errorMessage)
        this
    }

    ResponseBuilder setWriterAccessAllowed(boolean writerAccessAllowed) {
        mockResponse.setWriterAccessAllowed(writerAccessAllowed)
        this
    }
}
