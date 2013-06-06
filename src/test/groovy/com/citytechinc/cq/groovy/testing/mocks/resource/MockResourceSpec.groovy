package com.citytechinc.cq.groovy.testing.mocks.resource

import com.citytechinc.cq.groovy.testing.specs.AbstractRepositorySpec
import spock.lang.Shared

class MockResourceSpec extends AbstractRepositorySpec {

    @Shared resourceResolver

    def setupSpec() {
        resourceResolver = new MockResourceResolver(session)

        def home = session.rootNode.addNode("home", "cq:Page")
        def content = home.addNode("jcr:content")

        content.setProperty("jcr:title", "Home")
        content.setProperty("sling:resourceType", "type")
        content.setProperty("sling:resourceSuperType", "supertype")

        session.save()
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
