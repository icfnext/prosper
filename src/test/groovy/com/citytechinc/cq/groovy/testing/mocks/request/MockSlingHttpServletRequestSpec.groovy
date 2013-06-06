package com.citytechinc.cq.groovy.testing.mocks.request

import com.citytechinc.cq.groovy.testing.specs.AbstractSlingRepositorySpec
import com.google.common.collect.LinkedHashMultimap
import spock.lang.Ignore

@Ignore
class MockSlingHttpServletRequestSpec extends AbstractSlingRepositorySpec {

    def setupSpec() {
        def content = session.rootNode.addNode("content")

        session.save()
    }

    def "test getters"() {
        setup:
        def parameters = LinkedHashMultimap.create()
        def request = new MockSlingHttpServletRequest(resourceResolver, "/content", "", "", "", "", parameters)

        expect:
        request.resource.path == "/content"
    }


}
