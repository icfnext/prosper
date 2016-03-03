package com.citytechinc.aem.prosper.mocks.resource

import org.apache.sling.api.adapter.AdapterManager
import org.apache.sling.api.resource.Resource
import org.apache.sling.api.resource.ResourceResolver
import org.apache.sling.api.resource.ResourceUtil
import org.apache.sling.jcr.resource.JcrResourceUtil
import org.apache.sling.jcr.resource.internal.helper.jcr.JcrResourceProvider
import org.apache.sling.jcr.resource.internal.helper.jcr.PathMapper

import javax.jcr.Node
import javax.jcr.RepositoryException
import javax.jcr.Session
import javax.servlet.http.HttpServletRequest

class MockResourceResolver implements ProsperResourceResolver, GroovyInterceptable {

    private final JcrResourceProvider resourceProvider

    private final Session session

    private final AdapterManager adapterManager

    private String[] searchPath

    private boolean closed

    MockResourceResolver(Session session, AdapterManager adapterManager) {
        resourceProvider = new JcrResourceProvider(session, null, null, new PathMapper())

        this.session = session
        this.adapterManager = adapterManager
    }

    @Override
    void setSearchPath(String... searchPath) {
        this.searchPath = searchPath
    }

    @Override
    def invokeMethod(String name, args) {
        if (["isLive", "close"].contains(name) || !closed) {
            return this.&"$name"(args)
        }

        throw new IllegalStateException("The resource resolver is closed.")
    }

    @Override
    Resource getResource(String path) {
        def resource = null

        try {
            if (session.itemExists(path)) {
                resource = getResourceInternal(path)
            }
        } catch (RepositoryException e) {
            // ignore
        }

        resource
    }

    @Override
    Resource getResource(Resource base, String path) {
        def resource

        if (path.startsWith("/")) {
            resource = getResource(path)
        } else {
            resource = base ? getResource("${base.path}/$path") : null
        }

        resource
    }

    @Override
    ResourceResolver clone(Map<String, Object> authenticationInfo) {
        throw new UnsupportedOperationException()
    }

    @Override
    Iterator<Resource> findResources(String query, String language) {
        def resourceResults = JcrResourceUtil.query(session, query, language).nodes.collect() { Node node ->
            getResource(node.path)
        }

        resourceResults.iterator()
    }

    @Override
    String[] getSearchPath() {
        searchPath ?: [] as String[]
    }

    @Override
    Iterator<Resource> listChildren(Resource parent) {
        getChildren(parent).iterator()
    }

    @Override
    Iterable<Resource> getChildren(Resource parent) {
        parent.adaptTo(Node).nodes.collect { Node node ->
            getResourceInternal(node.path)
        } as Iterable<Resource>
    }

    @Override
    String map(String resourcePath) {
        resourcePath
    }

    @Override
    String map(HttpServletRequest request, String resourcePath) {
        resourcePath
    }

    @Override
    Iterator<Map<String, Object>> queryResources(String query, String language) {
        throw new UnsupportedOperationException()
    }

    @Override
    boolean hasChildren(Resource resource) {
        resource.hasChildren()
    }

    @Override
    Resource resolve(HttpServletRequest request, String absPath) {
        resolve(absPath)
    }

    @Override
    Resource resolve(HttpServletRequest request) {
        throw new UnsupportedOperationException()
    }

    @Override
    Resource resolve(String absPath) {
        getResource(absPath) ?: new MockNonExistingResource(this, absPath, adapterManager)
    }

    @Override
    <AdapterType> AdapterType adaptTo(Class<AdapterType> type) {
        adapterManager.getAdapter(this, type)
    }

    @Override
    boolean isLive() {
        !closed
    }

    @Override
    void close() {
        closed = true
    }

    @Override
    String getUserID() {
        throw new UnsupportedOperationException()
    }

    @Override
    Object getAttribute(String name) {
        resourceProvider.getAttribute(this, name)
    }

    @Override
    Iterator<String> getAttributeNames() {
        resourceProvider.getAttributeNames(this).iterator()
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
        resourceProvider.revert(this)
    }

    @Override
    void commit() {
        resourceProvider.commit(this)
    }

    @Override
    boolean hasChanges() {
        resourceProvider.hasChanges(this)
    }

    @Override
    String getParentResourceType(Resource resource) {
        resource.resourceSuperType ?: getParentResourceType(resource.resourceType)
    }

    @Override
    String getParentResourceType(String resourceType) {
        final Resource resourceTypeResource = getResource(ResourceUtil.resourceTypeToPath(resourceType))
        resourceTypeResource != null ? resourceTypeResource.resourceSuperType : null
    }

    @Override
    boolean isResourceType(Resource resource, String resourceType) {
        def isResourceType = resourceType == resource.resourceType

        def resourceSuperType = resource.resourceSuperType ?: getParentResourceType(resource.resourceType)
        while (!isResourceType && resourceSuperType) {
            isResourceType = resourceType == resourceSuperType
            resourceSuperType = getParentResourceType(resourceSuperType)
        }

        isResourceType
    }

    @Override
    void refresh() {
        resourceProvider.refresh()
    }

    private Resource getResourceInternal(String path) {
        def jcrResource = resourceProvider.getResource(this, path)

        new MockResource(jcrResource, adapterManager)
    }
}
