package com.citytechinc.cq.groovy.testing.mocks.resource

import com.day.cq.tagging.TagManager
import com.day.cq.tagging.impl.JcrTagManagerImpl
import com.day.cq.wcm.api.PageManager
import com.day.cq.wcm.core.impl.PageManagerFactoryImpl
import org.apache.sling.api.resource.Resource
import org.apache.sling.api.resource.ResourceResolver

import javax.jcr.Node
import javax.jcr.RepositoryException
import javax.jcr.Session
import javax.servlet.http.HttpServletRequest

@SuppressWarnings("deprecation")
class MockResourceResolver implements ResourceResolver {

    def session

    MockResourceResolver(session) {
        this.session = session
    }

    @Override
    Resource getResource(String path) {
        def resource = null

        try {
            if (session.nodeExists(path)) {
                resource = new MockResource(this, session.getNode(path))
            }
        } catch (RepositoryException e) {
            // ignore
        }

        resource
    }

    @Override
    Resource getResource(Resource base, String path) {
        base ? getResource("${base.path}/$path") : null
    }

    @Override
    ResourceResolver clone(Map<String, Object> authenticationInfo) {
        throw new UnsupportedOperationException()
    }

    @Override
    Iterator<Resource> findResources(String query, String language) {
        throw new UnsupportedOperationException()
    }

    @Override
    String[] getSearchPath() {
        throw new UnsupportedOperationException()
    }

    @Override
    Iterator<Resource> listChildren(Resource parent) {
        getChildren(parent).iterator()
    }

    @Override
    Iterable<Resource> getChildren(Resource parent) {
        parent.adaptTo(Node).nodes.collect { new MockResource(this, it) }
    }

    @Override
    String map(String resourcePath) {
        resourcePath
    }

    @Override
    String map(HttpServletRequest request, String resourcePath) {
        throw new UnsupportedOperationException()
    }

    @Override
    Iterator<Map<String, Object>> queryResources(String query, String language) {
        throw new UnsupportedOperationException()
    }

    @Override
    Resource resolve(HttpServletRequest request, String absPath) {
        throw new UnsupportedOperationException()
    }

    @Override
    Resource resolve(HttpServletRequest request) {
        throw new UnsupportedOperationException()
    }

    @Override
    Resource resolve(String absPath) {
        getResource(absPath)
    }

    @Override
    <AdapterType> AdapterType adaptTo(Class<AdapterType> type) {
        def result

        if (type == PageManager) {
            def factory = new PageManagerFactoryImpl()

            result = factory.getPageManager(this)
        } else if (type == TagManager) {
            result = new JcrTagManagerImpl(this, null, null, "/etc/tags")
        } else if (type == Session) {
            result = session
        } else {
            result = null
        }

        result
    }

    @Override
    boolean isLive() {
        throw new UnsupportedOperationException()
    }

    @Override
    void close() {

    }

    @Override
    String getUserID() {
        throw new UnsupportedOperationException()
    }

    @Override
    Object getAttribute(String name) {
        throw new UnsupportedOperationException()
    }

    @Override
    Iterator<String> getAttributeNames() {
        throw new UnsupportedOperationException()
    }

    @Override
    void delete(Resource resource) {
        throw new UnsupportedOperationException()
    }

    @Override
    Resource create(Resource parent, String name, Map<String, Object> properties) {
        throw new UnsupportedOperationException()
    }

    @Override
    void revert() {
        throw new UnsupportedOperationException()
    }

    @Override
    void commit() {
        throw new UnsupportedOperationException()
    }

    @Override
    boolean hasChanges() {
        throw new UnsupportedOperationException()
    }
}
