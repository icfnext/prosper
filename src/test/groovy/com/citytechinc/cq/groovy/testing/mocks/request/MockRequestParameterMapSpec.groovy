package com.citytechinc.cq.groovy.testing.mocks.request

import com.google.common.collect.LinkedHashMultimap
import spock.lang.Specification

class MockRequestParameterMapSpec extends Specification {

    def "get values"() {
        setup:
        def parameters = LinkedHashMultimap.create()

        parameters.put("a", "1")
        parameters.put("a", "2")

        def requestParameterMap = MockRequestParameterMap.create(parameters)

        def values = requestParameterMap.getValues("a")

        expect:
        values.length == 2
        values*.string == ["1", "2"]
    }

    def "get value"() {
        setup:
        def parameters = LinkedHashMultimap.create()

        parameters.put("a", "1")

        def requestParameterMap = MockRequestParameterMap.create(parameters)

        expect:
        requestParameterMap.getValue("a").string == "1"
    }

    def "get value when multiple values are present"() {
        setup:
        def parameters = LinkedHashMultimap.create()

        parameters.put("a", "1")
        parameters.put("a", "2")

        def requestParameterMap = MockRequestParameterMap.create(parameters)

        expect:
        requestParameterMap.getValue("a").string == "1"
    }
}
