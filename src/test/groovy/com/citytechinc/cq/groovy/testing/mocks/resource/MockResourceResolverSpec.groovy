package com.citytechinc.cq.groovy.testing.mocks.resource
import com.citytechinc.cq.groovy.testing.specs.AbstractRepositorySpec
import com.day.cq.tagging.TagManager
import com.day.cq.wcm.api.PageManager
import org.apache.sling.api.resource.NonExistingResource
import spock.lang.Shared

import javax.jcr.Session
import javax.jcr.query.Query

class MockResourceResolverSpec extends AbstractRepositorySpec {

    @Shared resourceResolver

    def setupSpec() {
        resourceResolver = new MockResourceResolver(session)

        def content = session.rootNode.addNode("content")

        content.addNode("one")
        content.addNode("two")

        session.save()
    }

    def "adapt to page manager"() {
        expect:
        resourceResolver.adaptTo(PageManager)
    }

    def "adapt to tag manager"() {
        expect:
        resourceResolver.adaptTo(TagManager)
    }

    def "adapt to session"() {
        expect:
        resourceResolver.adaptTo(Session)
    }

    def "adapt to invalid type returns null"() {
        expect:
        !resourceResolver.adaptTo(String)
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

    def "get resource with base resource and absolute path returns absolute path"() {
        setup:
        def baseResource = resourceResolver.getResource("/content")

        expect:
        resourceResolver.getResource(baseResource, "/content/one").path == "/content/one"
    }

    def "get resource with null base resource returns null"() {
        expect:
        !resourceResolver.getResource(null, "one")
    }

    def "get resource with null base resource and absolute path returns absolute path"() {
        expect:
        resourceResolver.getResource(null, "/content/one").path == "/content/one"
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

    def "resolve non-existing resource"() {
        expect:
        resourceResolver.resolve("/content/three") instanceof NonExistingResource
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

    def "get search path"() {
        setup:
        resourceResolver.setSearchPath("/content/one")

        expect:
        resourceResolver.searchPath == ["/content/one"]
    }

    def "find resources using XPath"() {
        expect:
        resourceResolver.findResources("/jcr:root/content//*[jcr:primaryType='nt:unstructured']", Query.XPATH).size() == 2
    }

    def "is live after close"() {
        setup:
        resourceResolver.close()

        expect:
        !resourceResolver.live
    }

    def "call method after close"() {
        setup:
        resourceResolver.close()

        when:
        resourceResolver.findResources('', Query.XPATH)

        then:
        thrown(IllegalStateException)

        when:
        resourceResolver.getAttribute("foo")

        then:
        thrown(IllegalStateException)

        when:
        resourceResolver.getAttributeNames()

        then:
        thrown(IllegalStateException)

        when:
        resourceResolver.getResource("/content/one")

        then:
        thrown(IllegalStateException)
    }
}
