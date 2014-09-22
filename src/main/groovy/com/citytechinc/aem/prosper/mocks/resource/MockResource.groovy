package com.citytechinc.aem.prosper.mocks.resource

import groovy.transform.ToString
import org.apache.sling.api.adapter.AdapterFactory
import org.apache.sling.api.resource.Resource

@ToString(includes = "resource")
class MockResource implements Resource {

    @Delegate
    private final Resource resource

    private final Map<Class, Closure> adapters

    private final List<AdapterFactory> adapterFactories

    MockResource(Resource resource, Map<Class, Closure> adapters, List<AdapterFactory> adapterFactories) {
        this.resource = resource
        this.adapters = adapters
        this.adapterFactories = adapterFactories
    }

    @Override
    <AdapterType> AdapterType adaptTo(Class<AdapterType> type) {
        def result = (AdapterType) adapterFactories.findResult {
            adapterFactory -> adapterFactory.getAdapter(this, type)
        }

        if (!result) {
            def adapter = adapters.find { it.key == type }

            if (adapter) {
                result = (AdapterType) adapter.value.call(this)
            } else {
                result = resource.adaptTo(type)
            }
        }

        result
    }
}
