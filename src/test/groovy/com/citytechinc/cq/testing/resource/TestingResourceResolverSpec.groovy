package com.citytechinc.cq.testing.resource

import com.citytechinc.cq.testing.AbstractRepositorySpec
import spock.lang.Shared

class TestingResourceResolverSpec extends AbstractRepositorySpec {

    @Shared resourceResolver

    def setupSpec() {
        resourceResolver = new TestingResourceResolver(session)

        def content = session.rootNode.addNode("content")

        content.addNode("one")
        content.addNode("two")

        session.save()
    }

    def "get resource"() {
        expect:
        resourceResolver.getResource("/content").path == "/content"
    }

    def "get non-existent resource returns null"() {
        expect:
        !resourceResolver.getResource("/etc")
    }

    def "get resource for malformed path returns null"() {
        expect:
        !resourceResolver.getResource("//")
    }

    def "get resource with base resource"() {
        setup:
        def baseResource = resourceResolver.getResource("/content")

        expect:
        resourceResolver.getResource(baseResource, "one").path == "/content/one"
    }

    def "get resource with null base resource returns null"() {
        expect:
        !resourceResolver.getResource(null, "one")
    }

    def "get resource with base resource and malformed path returns null"() {
        setup:
        def baseResource = resourceResolver.getResource("/content")

        expect:
        !resourceResolver.getResource(baseResource, "//")
    }

    def "resolve resource"() {
        expect:
        resourceResolver.resolve("/content").path == "/content"
    }

    def "list children"() {
        setup:
        def resource = resourceResolver.getResource("/content")

        expect:
        resourceResolver.listChildren(resource).size() == 2
    }

    def "get children"() {
        setup:
        def resource = resourceResolver.getResource("/content")

        expect:
        resourceResolver.getChildren(resource).size() == 2
    }
}
