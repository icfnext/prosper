package com.citytechinc.cqlibrary.testing.mock

import com.day.cq.wcm.api.Page
import com.day.cq.wcm.core.impl.PageImpl
import org.apache.sling.api.resource.Resource
import org.apache.sling.api.resource.ResourceMetadata
import org.apache.sling.api.resource.ResourceResolver
import org.apache.sling.api.resource.ValueMap
import org.apache.sling.jcr.resource.JcrPropertyMap

import javax.jcr.Node

class MockResource implements Resource {

    def node

    MockResource(node) {
        this.node = node
    }

    @Override
    <AdapterType> AdapterType adaptTo(Class<AdapterType> type) {
        def result

        if (type == Node) {
            result = node
        } else if (type == ValueMap) {
			result = new JcrPropertyMap(node)
        } else if (type == Page && 'cq:Page' == getResourceType()) {
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
	    node.depth == 0 ? null : new MockResource(node.parent)
    }

    @Override
    Iterator<Resource> listChildren() {
        node.nodes.collect { new MockResource(it) }.iterator()
    }

	@Override
	Iterable<Resource> getChildren() {
		node.nodes.collect { new MockResource(it) }.iterator()
	}

	@Override
    Resource getChild(String relPath) {
        node.hasNode(relPath) ? new MockResource(node.getNode(relPath)) : null
    }

    @Override
    String getResourceType() {
        node.get('sling:resourceType') ?: node.primaryNodeType.name
    }

    @Override
    String getResourceSuperType() {
        node.get('sling:resourceSuperType')
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
        new MockResourceResolver(node.session)
    }
}
