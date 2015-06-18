package com.citytechinc.aem.prosper.mocks.resource

import org.apache.sling.api.adapter.AdapterManager
import org.apache.sling.api.resource.ResourceResolver
import org.apache.sling.api.resource.SyntheticResource

class MockNonExistingResource extends SyntheticResource {

    private final AdapterManager adapterManager

    MockNonExistingResource(ResourceResolver resourceResolver, String path, AdapterManager adapterManager) {
        super(resourceResolver, path, "sling:nonexisting")

        this.adapterManager = adapterManager
    }

    @Override
    def <AdapterType> AdapterType adaptTo(Class<AdapterType> type) {
        def result = adapterManager.getAdapter(this, type)

        // specifically check for null so we don't incorrectly check empty collections
        if (result == null) {
            result = super.adaptTo(type)
        }

        result
    }
}
