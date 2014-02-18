package com.citytechinc.cq.groovy.testing.mocks.resource

import com.day.cq.wcm.api.NameConstants
import com.day.cq.wcm.api.Page
import com.day.cq.wcm.core.impl.PageImpl
import org.apache.sling.api.resource.Resource
import org.apache.sling.api.resource.ResourceMetadata
import org.apache.sling.api.resource.ResourceResolver
import org.apache.sling.api.resource.ValueMap
import org.apache.sling.jcr.resource.JcrPropertyMap
import org.apache.sling.jcr.resource.JcrResourceConstants

import javax.jcr.Node

class MockResource implements Resource {

    ResourceResolver resourceResolver

    Node node

    def adapters

    MockResource(resourceResolver, node, adapters) {
        this.resourceResolver = resourceResolver
        this.node = node
        this.adapters = adapters
    }

    @Override
    <AdapterType> AdapterType adaptTo(Class<AdapterType> type) {
        def result

        if (type == Node) {
            result = node
        } else if (type == ValueMap) {
            result = new JcrPropertyMap(node)
        } else if (type == Page && NameConstants.NT_PAGE == getResourceType()) {
            result = new PageImpl(this)
        } else {
            def found = adapters.find { it.key == type }

            result = found ? (AdapterType) found.value.call(this) : null
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
        node.depth == 0 ? null : new MockResource(resourceResolver, node.parent, adapters)
    }

    @Override
    Iterator<Resource> listChildren() {
        getChildren().iterator()
    }

    @Override
    Iterable<Resource> getChildren() {
        node.nodes.collect { new MockResource(resourceResolver, it, adapters) }
    }

    @Override
    Resource getChild(String relPath) {
        node.hasNode(relPath) ? new MockResource(resourceResolver, node.getNode(relPath), adapters) : null
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
        node.hasProperty(JcrResourceConstants.SLING_RESOURCE_SUPER_TYPE_PROPERTY) ? node.getProperty(JcrResourceConstants.SLING_RESOURCE_SUPER_TYPE_PROPERTY).string : null
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
}
