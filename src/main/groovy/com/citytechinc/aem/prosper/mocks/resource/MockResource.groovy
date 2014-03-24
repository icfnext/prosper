package com.citytechinc.aem.prosper.mocks.resource

import com.google.common.base.Objects
import org.apache.sling.api.resource.Resource
import org.apache.sling.api.resource.ResourceMetadata
import org.apache.sling.api.resource.ResourceResolver
import org.apache.sling.jcr.resource.JcrResourceConstants

import javax.jcr.Node

class MockResource implements Resource {

    private final ResourceResolver resourceResolver

    private final Node node

    private final def adapters

    private final def adapterFactories

    MockResource(resourceResolver, node, adapters, adapterFactories) {
        this.resourceResolver = resourceResolver
        this.node = node
        this.adapters = adapters
        this.adapterFactories = adapterFactories
    }

    @Override
    <AdapterType> AdapterType adaptTo(Class<AdapterType> type) {
        def result = (AdapterType) adapterFactories.findResult {
            adapterFactory -> adapterFactory.getAdapter(this, type)
        }

        if (!result) {
            def adapter = adapters.find { it.key == type }

            if (adapter) {
                result = (AdapterType) adapter.value.call(this)
            }
        }

        result
    }

    @Override
    String getPath() {
        node.path
    }

    @Override
    String getName() {
        node.name
    }

    @Override
    Resource getParent() {
        node.depth == 0 ? null : new MockResource(resourceResolver, node.parent, adapters, adapterFactories)
    }

    @Override
    Iterator<Resource> listChildren() {
        getChildren().iterator()
    }

    @Override
    Iterable<Resource> getChildren() {
        node.nodes.collect { new MockResource(resourceResolver, it, adapters, adapterFactories) }
    }

    @Override
    Resource getChild(String relPath) {
        node.hasNode(relPath) ? new MockResource(resourceResolver, node.getNode(relPath), adapters,
            adapterFactories) : null
    }

    @Override
    String getResourceType() {
        def resourceType

        if (node.hasProperty(JcrResourceConstants.SLING_RESOURCE_TYPE_PROPERTY)) {
            resourceType = node.getProperty(JcrResourceConstants.SLING_RESOURCE_TYPE_PROPERTY).string
        } else {
            resourceType = node.primaryNodeType.name
        }

        resourceType
    }

    @Override
    String getResourceSuperType() {
        def resourceSuperType = null

        if (node.hasProperty(JcrResourceConstants.SLING_RESOURCE_SUPER_TYPE_PROPERTY)) {
            resourceSuperType = node.getProperty(JcrResourceConstants.SLING_RESOURCE_SUPER_TYPE_PROPERTY).string
        }

        resourceSuperType
    }

    @Override
    boolean hasChildren() {
        node.hasNodes()
    }

    @Override
    boolean isResourceType(String resourceType) {
        getResourceType() == resourceType
    }

    @Override
    ResourceMetadata getResourceMetadata() {
        throw new UnsupportedOperationException()
    }

    @Override
    ResourceResolver getResourceResolver() {
        resourceResolver
    }

    @Override
    String toString() {
        Objects.toStringHelper(this).add("path", path).add("resourceType", resourceType).add("resourceSuperType",
            resourceSuperType).toString()
    }
}
