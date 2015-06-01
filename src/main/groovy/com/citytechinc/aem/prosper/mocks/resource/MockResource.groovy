package com.citytechinc.aem.prosper.mocks.resource

import com.citytechinc.aem.prosper.mocks.adapter.TestAdapterManager
import groovy.transform.ToString
import org.apache.sling.api.resource.Resource

@ToString(includes = "resource")
class MockResource implements Resource {

    @Delegate
    private final Resource resource

    private final TestAdapterManager adapterManager

    MockResource(Resource resource, TestAdapterManager adapterManager) {
        this.resource = resource
        this.adapterManager = adapterManager
    }

    @Override
    <AdapterType> AdapterType adaptTo(Class<AdapterType> type) {
        def result = adapterManager.adapt(this, type)

        //specifically check for null so we don't incorrectly check empty collections
        if (result == null) {
            result = resource.adaptTo(type)
        }

        result
    }
}
