package com.citytechinc.aem.prosper.mocks.request

import groovy.transform.ToString
import org.apache.sling.api.request.RequestParameter
import org.apache.sling.api.request.RequestParameterMap

import javax.servlet.http.HttpServletRequest

@ToString(includes = "map")
class MockRequestParameterMap implements RequestParameterMap {

    static RequestParameterMap create(HttpServletRequest request) {
        def map = (request.parameterMap as Map<String, String[]>).collectEntries { name, values ->
            [(name): values.collect { new MockRequestParameter(name, it) }.toArray(new RequestParameter[values.size()])]
        }

        new MockRequestParameterMap(map)
    }

    @Delegate
    private final Map<String, RequestParameter[]> map

    MockRequestParameterMap(map) {
        this.map = map
    }

    @Override
    RequestParameter[] getValues(String name) {
        map[name] ?: null
    }

    @Override
    RequestParameter getValue(String name) {
        def values = map[name]

        values ? values[0] : null
    }
}
