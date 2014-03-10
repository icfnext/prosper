package com.citytechinc.aem.prosper.mocks.request

import com.google.common.collect.SetMultimap
import org.apache.sling.api.request.RequestParameter
import org.apache.sling.api.request.RequestParameterMap

class MockRequestParameterMap implements RequestParameterMap {

    static RequestParameterMap create(SetMultimap<String, String> parameters) {
        def map = [:]

        parameters.keySet().each { name ->
            def values = parameters.get(name)

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
