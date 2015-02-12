package com.citytechinc.aem.prosper.mocks.resource

import com.citytechinc.aem.prosper.specs.ProsperSpec
import com.google.common.collect.Iterables

class MockResourceSpec extends ProsperSpec {

    def "get path"() {
        expect:
        resourceResolver.getResource("/content/prosper").path == "/content/prosper"
    }

    def "get name"() {
        expect:
        resourceResolver.getResource("/content/prosper").name == "prosper"
    }

    def "get parent"() {
        setup:
        def resource = resourceResolver.getResource("/content")

        expect:
        resource.parent.path == session.rootNode.path
    }

    def "get parent for root node returns null"() {
        setup:
        def resource = resourceResolver.getResource("/")

        expect:
        !resource.parent
    }

    def "list children"() {
        setup:
        def resource = resourceResolver.getResource("/content")

        expect:
        resource.listChildren().size() == 2
    }

    def "get children"() {
        setup:
        def resource = resourceResolver.getResource("/content")

        expect:
        Iterables.size(resource.children) == 2
    }

    def "get child"() {
        setup:
        def resource = resourceResolver.getResource("/content/prosper")

        expect:
        resource.getChild("jcr:content").path == "/content/prosper/jcr:content"
    }

    def "get child for non-existent node returns null"() {
        setup:
        def resource = resourceResolver.getResource("/content/prosper")

        expect:
        !resource.getChild("ghost")
    }

    def "get resource type"() {
        setup:
        def resource = resourceResolver.getResource("/content/prosper/jcr:content")

        expect:
        resource.resourceType == "prosper/components/page/prosper"
    }

    def "get resource type when property does not exist"() {
        setup:
        def resource = resourceResolver.getResource("/content/dam")

        expect:
        resource.resourceType == "sling:OrderedFolder"
    }

    def "get resource super type"() {
        setup:
        def resource = resourceResolver.getResource("/content/prosper/jcr:content")

        expect:
        resource.resourceSuperType == "foundation/components/page"
    }

    def "get resource super type when property does not exist"() {
        setup:
        def resource = resourceResolver.getResource("/content")

        expect:
        !resource.resourceSuperType
    }

    def "is resource type"() {
        setup:
        def resource = resourceResolver.getResource("/content/prosper/jcr:content")

        expect:
        resource.isResourceType("prosper/components/page/prosper")
        !resource.isResourceType("spock")
    }
}
