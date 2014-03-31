package com.citytechinc.aem.prosper.mocks.resource

import com.citytechinc.aem.prosper.specs.ProsperSpec
import com.google.common.collect.Iterables
import spock.lang.Ignore

class MockResourceSpec extends ProsperSpec {

    def setupSpec() {
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
        Iterables.size(resource.children) == 1
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

    @Ignore
    def "is resource type"() {
        setup:
        def resource = resourceResolver.getResource("/home/jcr:content")

        expect:
        resource.isResourceType("type")
        !resource.isResourceType("none")
    }
}
