package com.citytechinc.cq.testing.resource
import com.citytechinc.cq.testing.AbstractRepositorySpec
import spock.lang.Shared

class TestingResourceResolverSpec extends AbstractRepositorySpec {

    @Shared resourceResolver

    def setupSpec() {
        resourceResolver = new TestingResourceResolver(session)

        session.rootNode.addNode("content").addNode("child")
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
        resourceResolver.getResource(baseResource, "child").path == "/content/child"
    }

    def "get resource with null base resource returns null"() {
        expect:
        !resourceResolver.getResource(null, "child")
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
}
