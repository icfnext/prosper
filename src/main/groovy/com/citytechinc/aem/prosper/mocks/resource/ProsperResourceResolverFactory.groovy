package com.citytechinc.aem.prosper.mocks.resource

import com.citytechinc.aem.prosper.adapter.ProsperAdapterManager
import groovy.transform.TupleConstructor
import org.apache.sling.api.resource.ResourceResolver
import org.apache.sling.api.resource.ResourceResolverFactory

import javax.jcr.Session

@TupleConstructor
class ProsperResourceResolverFactory implements ResourceResolverFactory {

    Session session

    ProsperAdapterManager adapterManager

    @Override
    ResourceResolver getResourceResolver(Map<String, Object> authenticationInfo) {
        new MockResourceResolver(session, adapterManager)
    }

    @Override
    ResourceResolver getAdministrativeResourceResolver(Map<String, Object> authenticationInfo) {
        new MockResourceResolver(session, adapterManager)
    }

    @Override
    ResourceResolver getServiceResourceResolver(Map<String, Object> authenticationInfo) {
        new MockResourceResolver(session, adapterManager)
    }

    @Override
    ResourceResolver getThreadResourceResolver() {
        new MockResourceResolver(session, adapterManager)
    }
}
