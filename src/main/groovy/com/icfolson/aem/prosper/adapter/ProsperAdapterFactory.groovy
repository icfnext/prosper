package com.icfolson.aem.prosper.adapter

import groovy.transform.TupleConstructor
import org.apache.sling.api.SlingHttpServletRequest
import org.apache.sling.api.adapter.AdapterFactory
import org.apache.sling.api.resource.Resource
import org.apache.sling.api.resource.ResourceResolver

@TupleConstructor
class ProsperAdapterFactory implements AdapterFactory {

    public static final String[] ADAPTER_CLASSES = [
        "org.apache.sling.api.resource.Resource",
        "org.apache.sling.api.resource.ResourceResolver"
    ]

    public static final String[] ADAPTABLE_CLASSES = [
        "org.apache.sling.api.SlingHttpServletRequest"
    ]

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
