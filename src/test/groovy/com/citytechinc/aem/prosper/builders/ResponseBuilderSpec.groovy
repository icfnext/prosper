package com.citytechinc.aem.prosper.builders

import com.citytechinc.aem.prosper.specs.ProsperSpec
import com.google.common.base.Charsets

class ResponseBuilderSpec extends ProsperSpec {

    def "build response"() {
        setup:
        def response = responseBuilder.build()

        expect:
        response.characterEncoding == Charsets.ISO_8859_1.name()
        !response.contentType
        !response.contentAsString
    }

    def "build response with closure"() {
        setup:
        def response = responseBuilder.build {
            status = 500
            setHeader "foo", "bar"
        }

        expect:
        response.status == 500
        response.getHeader("foo") == "bar"
    }
}
