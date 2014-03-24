package com.citytechinc.aem.prosper.mocks.request

import org.apache.sling.api.request.RequestParameter
import org.apache.sling.api.request.RequestParameterMap
import org.springframework.mock.web.MockHttpServletRequest

class MockRequestParameterMap implements RequestParameterMap {

    static RequestParameterMap create(MockHttpServletRequest mockRequest) {
        def map = [:]

        mockRequest.parameterMap.each { name, values ->
            map[name] = values.collect { new MockRequestParameter(it) }.toArray(new RequestParameter[values.size()])
        }

        new MockRequestParameterMap(map)
    }

    @Delegate
    private final Map<String, RequestParameter[]> map = [:]

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
