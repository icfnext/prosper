package com.citytechinc.aem.prosper.builders

import com.citytechinc.aem.prosper.mocks.MockSlingHttpServletResponse
import com.google.common.base.Charsets
import com.google.common.net.MediaType
import org.apache.sling.api.SlingHttpServletResponse

import static javax.servlet.http.HttpServletResponse.SC_OK

/**
 * Builder to assist in creating <code>SlingHttpServletResponse</code> objects.
 */
class ResponseBuilder {

    private int status = SC_OK

    private MediaType mediaType = MediaType.HTML_UTF_8

    private final Writer writer

    /**
     * Create a response builder.
     */
    ResponseBuilder() {
        writer = new StringWriter()
    }

    /**
     * Create a response builder with a <code>Writer</code> for capturing output.
     *
     * @param writer writer instance for capturing response output
     */
    ResponseBuilder(Writer writer) {
        this.writer = writer
    }

    /**
     * Set the response status code.
     *
     * @param status status code
     */
    void status(int status) {
        this.status = status
    }

    /**
     * Set the response content type and encoding using a <code>MediaType</code> value.
     *
     * @param mediaType media type
     */
    void mediaType(MediaType mediaType) {
        this.mediaType = mediaType
    }

    /**
     * Build a Sling response with default values.
     *
     * @return response
     */
    SlingHttpServletResponse build() {
        build(null)
    }

    /**
     * Build a Sling response using a closure to set response properties.
     *
     * <pre>
     *  new ResponseBuilder().build {
     *      status 200
     *      mediaType MediaType.JSON_UTF_8
     *  }
     * </pre>
     *
     * @param closure closure that delegates to this builder
     * @return response
     */
    SlingHttpServletResponse build(Closure closure) {
        if (closure) {
            closure.delegate = this
            closure.resolveStrategy = Closure.DELEGATE_ONLY
            closure()
        }

        buildResponse()
    }

    private def buildResponse() {
        def contentType = mediaType.withoutParameters().toString()
        def encoding = mediaType.charset().or(Charsets.UTF_8).name()

        new MockSlingHttpServletResponse(writer, status, contentType, encoding)
    }
}
