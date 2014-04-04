package com.citytechinc.aem.prosper.mocks.resource

import org.apache.sling.api.resource.Resource

class MockResource implements Resource {

    @Delegate
    private Resource resource

    private final def adapters

    private final def adapterFactories

    MockResource(Resource resource, adapters, adapterFactories) {
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
