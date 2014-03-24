package com.citytechinc.aem.prosper.mocks.request

import org.springframework.mock.web.MockHttpServletRequest
import spock.lang.Specification

class MockRequestParameterMapSpec extends Specification {

    def "get values"() {
        setup:
        def request = new MockHttpServletRequest()

        request.addParameter "a", "1"
        request.addParameter "a", "2"

        def requestParameterMap = MockRequestParameterMap.create(request)

        def values = requestParameterMap.getValues("a")

        expect:
        values.length == 2
        values*.string == ["1", "2"]
    }

    def "get value"() {
        setup:
        def request = new MockHttpServletRequest()

        request.setParameter "a", "1"

        def requestParameterMap = MockRequestParameterMap.create(request)

        expect:
        requestParameterMap.getValue("a").string == "1"
    }

    def "get value when multiple values are present"() {
        setup:
        def request = new MockHttpServletRequest()

        request.addParameter "a", "1"
        request.addParameter "a", "2"

        def requestParameterMap = MockRequestParameterMap.create(request)

        expect:
        requestParameterMap.getValue("a").string == "1"
    }
}
