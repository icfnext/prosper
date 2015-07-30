package com.citytechinc.aem.prosper.adapter

import com.day.cq.replication.Replicator
import com.day.cq.tagging.TagManager
import com.day.cq.tagging.impl.JcrTagManagerImpl
import com.day.cq.wcm.api.NameConstants
import com.day.cq.wcm.api.Page
import com.day.cq.wcm.api.PageManager
import com.day.cq.wcm.core.impl.PageImpl
import com.day.cq.wcm.core.impl.PageManagerFactoryImpl
import groovy.transform.TupleConstructor
import org.apache.sling.api.SlingHttpServletRequest
import org.apache.sling.api.adapter.AdapterFactory
import org.apache.sling.api.resource.Resource
import org.apache.sling.api.resource.ResourceResolver
import org.apache.sling.jcr.api.SlingRepository
import org.osgi.service.event.EventAdmin

import javax.jcr.Session

@TupleConstructor
class ProsperAdapterFactory implements AdapterFactory {

    SlingRepository repository

    Session session

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
            def factory = new PageManagerFactoryImpl()

            def fields = [
                replicator: [replicate: {}] as Replicator,
                eventAdmin: [postEvent: {}, sendEvent: {}] as EventAdmin,
                repository: repository
            ]

            fields.each { name, instance ->
                factory.class.getDeclaredField(name).with {
                    accessible = true
                    set(factory, instance)
                }
            }

            result = factory.getPageManager(resourceResolver)
        } else if (type == TagManager) {
            result = new JcrTagManagerImpl(resourceResolver, null, null, "/etc/tags")
        } else if (type == Session) {
            result = session
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
