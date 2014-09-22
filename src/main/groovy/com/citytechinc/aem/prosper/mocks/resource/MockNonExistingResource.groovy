package com.citytechinc.aem.prosper.mocks.resource

import org.apache.sling.api.adapter.AdapterFactory
import org.apache.sling.api.resource.ResourceResolver
import org.apache.sling.api.resource.SyntheticResource

class MockNonExistingResource extends SyntheticResource {

    private final Map<Class, Closure> adapters

    private final List<AdapterFactory> adapterFactories

    MockNonExistingResource(ResourceResolver resourceResolver, String path, Map<Class, Closure> adapters,
        List<AdapterFactory> adapterFactories) {
        super(resourceResolver, path, "sling:nonexisting")

        this.adapters = adapters
        this.adapterFactories = adapterFactories
    }

    @Override
    def <AdapterType> AdapterType adaptTo(Class<AdapterType> type) {
        def result = (AdapterType) adapterFactories.findResult { adapterFactory ->
            adapterFactory.getAdapter(this, type)
        }

        if (!result) {
            def adapter = adapters.find { it.key == type }

            if (adapter) {
                result = (AdapterType) adapter.value.call(this)
            } else {
                result = super.adaptTo(type)
            }
        }

        result
    }
}
