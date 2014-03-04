package com.citytechinc.aem.spock.mocks.resource

import com.citytechinc.aem.spock.specs.AbstractSlingRepositorySpec

class MockResourceSpec extends AbstractSlingRepositorySpec {

    def setupSpec() {
        resourceResolver = new MockResourceResolver(session)

        pageBuilder.home {
            "jcr:content"("Home", "sling:resourceType": "type", "sling:resourceSuperType": "supertype")
        }
    }

    def "get path"() {
        expect:
        resourceResolver.getResource("/home").path == "/home"
    }

    def "get name"() {
        expect:
        resourceResolver.getResource("/home").name == "home"
    }

    def "get parent"() {
        setup:
        def resource = resourceResolver.getResource("/home")

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
        def resource = resourceResolver.getResource("/home")

        expect:
        resource.listChildren().size() == 1
    }

    def "get children"() {
        setup:
        def resource = resourceResolver.getResource("/home")

        expect:
        resource.children.size() == 1
    }

    def "get child"() {
        setup:
        def resource = resourceResolver.getResource("/home")

        expect:
        resource.getChild("jcr:content").path == "/home/jcr:content"
    }

    def "get child for non-existent node returns null"() {
        setup:
        def resource = resourceResolver.getResource("/home")

        expect:
        !resource.getChild("ghost")
    }

    def "get resource type"() {
        setup:
        def resource = resourceResolver.getResource("/home/jcr:content")

        expect:
        resource.resourceType == "type"
    }

    def "get resource type when property does not exist"() {
        setup:
        def resource = resourceResolver.getResource("/home")

        expect:
        resource.resourceType == "cq:Page"
    }

    def "get resource super type"() {
        setup:
        def resource = resourceResolver.getResource("/home/jcr:content")

        expect:
        resource.resourceSuperType == "supertype"
    }

    def "get resource super type when property does not exist"() {
        setup:
        def resource = resourceResolver.getResource("/home")

        expect:
        !resource.resourceSuperType
    }

    def "is resource type"() {
        setup:
        def resource = resourceResolver.getResource("/home/jcr:content")

        expect:
        resource.isResourceType("type")
        !resource.isResourceType("none")
    }
}
