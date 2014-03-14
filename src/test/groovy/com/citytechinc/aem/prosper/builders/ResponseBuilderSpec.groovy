package com.citytechinc.aem.prosper.builders
import com.google.common.net.MediaType
import spock.lang.Specification

class ResponseBuilderSpec extends Specification {

    def "build response"() {
        setup:
        def writer = new StringWriter()
        def response = new ResponseBuilder(writer).build()

        expect:
        response.characterEncoding == "UTF-8"
        response.contentType == "text/html"
        writer.toString() == ""
    }

    def "build response with closure"() {
        setup:
        def response = new ResponseBuilder().build {
            status 500
            mediaType MediaType.JSON_UTF_8
        }

        expect:
        response.characterEncoding == "UTF-8"
        response.contentType == "application/json"
    }
}
