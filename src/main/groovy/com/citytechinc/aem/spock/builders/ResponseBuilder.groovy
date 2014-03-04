package com.citytechinc.aem.spock.builders

import com.citytechinc.aem.spock.mocks.MockSlingHttpServletResponse
import com.google.common.base.Charsets
import com.google.common.net.MediaType

import static javax.servlet.http.HttpServletResponse.SC_OK

class ResponseBuilder {

    private def status = SC_OK

    private def mediaType = MediaType.HTML_UTF_8

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
        def encoding = mediaType.charset().or(Charsets.UTF_8).name()

        new MockSlingHttpServletResponse(status, contentType, encoding)
    }
}
