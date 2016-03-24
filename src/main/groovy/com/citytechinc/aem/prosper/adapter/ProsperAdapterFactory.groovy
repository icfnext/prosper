package com.citytechinc.aem.prosper.adapter

import com.citytechinc.aem.prosper.context.ProsperSlingContext
import com.day.cq.tagging.TagManager
import com.day.cq.tagging.impl.JcrTagManagerImpl
import com.day.cq.wcm.api.NameConstants
import com.day.cq.wcm.api.Page
import com.day.cq.wcm.api.PageManager
import com.day.cq.wcm.api.PageManagerFactory
import com.day.cq.wcm.core.impl.PageImpl
import groovy.transform.TupleConstructor
import org.apache.sling.api.SlingHttpServletRequest
import org.apache.sling.api.adapter.AdapterFactory
import org.apache.sling.api.resource.Resource
import org.apache.sling.api.resource.ResourceResolver

@TupleConstructor
class ProsperAdapterFactory implements AdapterFactory {

    public static final String[] ADAPTER_CLASSES = [
        "com.day.cq.wcm.api.Page",
        "com.day.cq.wcm.api.PageManager",
        "com.day.cq.tagging.TagManager",
        "org.apache.sling.api.resource.Resource",
        "org.apache.sling.api.resource.ResourceResolver"
    ]

    public static final String[] ADAPTABLE_CLASSES = [
        "org.apache.sling.api.resource.Resource",
        "org.apache.sling.api.resource.ResourceResolver",
        "org.apache.sling.api.SlingHttpServletRequest"
    ]

    ProsperSlingContext slingContext

    @Override
    def <AdapterType> AdapterType getAdapter(Object adaptable, Class<AdapterType> type) {
        def result

        if (adaptable instanceof ResourceResolver) {
            result = getResourceResolverAdapter(adaptable as ResourceResolver, type)
        } else if (adaptable instanceof Resource) {
            result = getResourceAdapter(adaptable as Resource, type)
        } else if (adaptable instanceof SlingHttpServletRequest) {
            result = getRequestAdapter(adaptable as SlingHttpServletRequest, type)
        } else {
            result = null
        }

        result
    }

    private <AdapterType> AdapterType getResourceResolverAdapter(ResourceResolver resourceResolver,
        Class<AdapterType> type) {
        def result

        if (type == PageManager) {
            def factory = slingContext.getService(PageManagerFactory)

            result = factory.getPageManager(resourceResolver)
        } else if (type == TagManager) {
            result = new JcrTagManagerImpl(resourceResolver, null, null, "/etc/tags")
        } else {
            result = null
        }

        result as AdapterType
    }

    private <AdapterType> AdapterType getResourceAdapter(Resource resource, Class<AdapterType> type) {
        def result

        if (type == Page) {
            result = NameConstants.NT_PAGE == resource.resourceType ? new PageImpl(resource) : null
        } else {
            result = null
        }

        result as AdapterType
    }

    private <AdapterType> AdapterType getRequestAdapter(SlingHttpServletRequest request, Class<AdapterType> type) {
        def result

        if (type == Resource) {
            result = request.resource
        } else if (type == ResourceResolver) {
            result = request.resourceResolver
        } else {
            result = null
        }

        result as AdapterType
    }
}
