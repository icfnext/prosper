package com.icfolson.aem.prosper.adapter

import org.apache.sling.api.SlingHttpServletRequest
import org.apache.sling.api.adapter.AdapterFactory
import org.apache.sling.api.resource.Resource
import org.apache.sling.api.resource.ResourceResolver

class ProsperAdapterFactory implements AdapterFactory {

    public static final String[] ADAPTER_CLASSES = [Resource.name, ResourceResolver.name]

    public static final String[] ADAPTABLE_CLASSES = [SlingHttpServletRequest.name]

    @Override
    <AdapterType> AdapterType getAdapter(Object adaptable, Class<AdapterType> type) {
        def result = null

        if (adaptable instanceof SlingHttpServletRequest) {
            def request = adaptable as SlingHttpServletRequest

            if (type == Resource) {
                result = request.resource
            } else if (type == ResourceResolver) {
                result = request.resourceResolver
            }
        }

        result as AdapterType
    }
}
