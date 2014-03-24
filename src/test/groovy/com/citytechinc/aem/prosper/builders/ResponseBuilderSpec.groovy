package com.citytechinc.aem.prosper.builders

import com.google.common.base.Charsets
import spock.lang.Specification

class ResponseBuilderSpec extends Specification {

    def "build response"() {
        setup:
        def response = new ResponseBuilder().build()

        expect:
        response.characterEncoding == Charsets.ISO_8859_1.name()
        !response.contentType
        !response.contentAsString
    }

    def "build response with closure"() {
        setup:
        def response = new ResponseBuilder().build {
            status = 500
            setHeader "foo", "bar"
        }

        expect:
        response.status == 500
        response.getHeader("foo") == "bar"
    }
}
