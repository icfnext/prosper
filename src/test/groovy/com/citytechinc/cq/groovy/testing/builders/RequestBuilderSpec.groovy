package com.citytechinc.cq.groovy.testing.builders

import com.citytechinc.cq.groovy.testing.specs.AbstractSlingRepositorySpec

class RequestBuilderSpec extends AbstractSlingRepositorySpec {

    def setupSpec() {
        session.rootNode.addNode("content")
        session.save()
    }

    def "build basic request"() {
        setup:
        def builder = new RequestBuilder(resourceResolver, "/content")

        def request = builder.build()

        expect:
        request.resource.path == "/content"
        !request.requestParameterMap
        request.requestPathInfo.extension == ""
        request.requestPathInfo.suffix == ""
        request.requestPathInfo.selectorString == ""
        request.requestPathInfo.resourcePath == "/content"
        request.queryString == ""
    }
}
