package com.citytechinc.aem.prosper.mocks.resource

import org.apache.sling.api.resource.ResourceResolver
import org.apache.sling.api.resource.SyntheticResource

class MockNonExistingResource extends SyntheticResource {

    private final def adapters

    private final def adapterFactories

    MockNonExistingResource(ResourceResolver resourceResolver, String path, adapters, adapterFactories) {
        super(resourceResolver, path, "sling:nonexisting")

        this.adapters = adapters
        this.adapterFactories = adapterFactories
    }

    MockNonExistingResource(ResourceResolver resourceResolver, String path,
        String resourceType) {
        super(resourceResolver, path, resourceType)

        adapters = [:]
        adapterFactories = []
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
