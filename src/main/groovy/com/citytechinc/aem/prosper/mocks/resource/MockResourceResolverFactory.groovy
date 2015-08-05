package com.citytechinc.aem.prosper.mocks.resource

import groovy.transform.TupleConstructor
import org.apache.sling.api.resource.LoginException
import org.apache.sling.api.resource.ResourceResolver
import org.apache.sling.api.resource.ResourceResolverFactory

@TupleConstructor
class MockResourceResolverFactory implements ResourceResolverFactory {

    ResourceResolver resourceResolver

    @Override
    ResourceResolver getResourceResolver(Map<String, Object> authenticationInfo) throws LoginException {
        resourceResolver
    }

    @Override
    ResourceResolver getAdministrativeResourceResolver(Map<String, Object> authenticationInfo) throws LoginException {
        resourceResolver
    }

    @Override
    ResourceResolver getServiceResourceResolver(Map<String, Object> authenticationInfo) throws LoginException {
        resourceResolver
    }

    @Override
    ResourceResolver getThreadResourceResolver() {
        throw new UnsupportedOperationException()
    }
}
