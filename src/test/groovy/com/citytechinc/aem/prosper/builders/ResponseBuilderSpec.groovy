package com.citytechinc.aem.prosper.builders

import com.google.common.net.MediaType
import spock.lang.Specification

import static javax.servlet.http.HttpServletResponse.SC_OK

class ResponseBuilderSpec extends Specification {

    def "build response"() {
        setup:
        def response = new ResponseBuilder().build()

        expect:
        response.status == SC_OK
        response.characterEncoding == "UTF-8"
        response.contentType == "text/html"
        response.output == ""
    }

    def "build response with closure"() {
        setup:
        def response = new ResponseBuilder().build {
            status 500
            mediaType MediaType.JSON_UTF_8
        }

        expect:
        response.status == 500
        response.characterEncoding == "UTF-8"
        response.contentType == "application/json"
    }
}
