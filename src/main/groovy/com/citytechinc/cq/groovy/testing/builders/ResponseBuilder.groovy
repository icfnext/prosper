package com.citytechinc.cq.groovy.testing.builders

import com.citytechinc.cq.groovy.testing.mocks.MockSlingHttpServletResponse
import com.google.common.base.Charsets
import com.google.common.net.MediaType

import static javax.servlet.http.HttpServletResponse.SC_OK

class ResponseBuilder {

    def status = SC_OK

    def mediaType = MediaType.HTML_UTF_8

    void status(int status) {
        this.status = status
    }

    void mediaType(MediaType mediaType) {
        this.mediaType = mediaType
    }

    MockSlingHttpServletResponse build() {
        build(null)
    }

    MockSlingHttpServletResponse build(Closure closure) {
        if (closure) {
            closure.delegate = this
            closure.resolveStrategy = Closure.DELEGATE_ONLY
            closure()
        }

        buildResponse()
    }

    private def buildResponse() {
        def contentType = mediaType.withoutParameters().toString()

        def charset = mediaType.charset()
        def encoding

        if (charset.present) {
            encoding = charset.get()
        } else {
            encoding = Charsets.UTF_8.name()
        }

        new MockSlingHttpServletResponse(status, contentType, encoding)
    }
}
