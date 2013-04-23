package com.citytechinc.cq.testing.resource

import com.day.cq.wcm.api.Page
import com.day.cq.wcm.core.impl.PageImpl
import org.apache.sling.api.resource.Resource
import org.apache.sling.api.resource.ResourceMetadata
import org.apache.sling.api.resource.ResourceResolver
import org.apache.sling.api.resource.ValueMap
import org.apache.sling.jcr.resource.JcrPropertyMap

import javax.jcr.Node

class TestingResource implements Resource {

    def resourceResolver

    def node

    TestingResource(resourceResolver, node) {
        this.resourceResolver = resourceResolver
        this.node = node
    }

    @Override
    <AdapterType> AdapterType adaptTo(Class<AdapterType> type) {
        def result

        if (type == Node) {
            result = node
        } else if (type == ValueMap) {
            result = new JcrPropertyMap(node)
        } else if (type == Page && "cq:Page" == getResourceType()) {
            result = new PageImpl(this)
        } else {
            result = null
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
        node.depth == 0 ? null : new TestingResource(resourceResolver, node.parent)
    }

    @Override
    Iterator<Resource> listChildren() {
        node.nodes.collect { new TestingResource(resourceResolver, it) }.iterator()
    }

    @Override
    Iterable<Resource> getChildren() {
        node.nodes.collect { new TestingResource(resourceResolver, it) }
    }

    @Override
    Resource getChild(String relPath) {
        node.hasNode(relPath) ? new TestingResource(resourceResolver, node.getNode(relPath)) : null
    }

    @Override
    String getResourceType() {
        def resourceType

        if (node.hasProperty("sling:resourceType")) {
            resourceType = node.getProperty("sling:resourceType").string
        } else {
            resourceType = node.primaryNodeType.name
        }

        resourceType
    }

    @Override
    String getResourceSuperType() {
        node.hasProperty("sling:resourceSuperType") ? node.getProperty("sling:resourceSuperType").string : null
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
