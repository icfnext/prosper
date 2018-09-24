package com.icfolson.aem.prosper.builders

import com.icfolson.aem.prosper.specs.ProsperSpec
import groovy.json.JsonBuilder

class ResponseBuilderSpec extends ProsperSpec {

    def "build response"() {
        setup:
        def response = responseBuilder.build()

        expect:
        !response.contentType
        !response.outputAsString
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

    def "write JSON to response"() {
        setup:
        def response = responseBuilder.build()

        when:
        new JsonBuilder([path: "/content/prosper"]).writeTo(response.writer)

        then:
        response.outputAsString == '{"path":"/content/prosper"}'
    }
}
