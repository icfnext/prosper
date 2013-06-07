package com.citytechinc.cq.groovy.testing.specs

class SlingRepositorySpec extends AbstractSlingRepositorySpec {

    @Override
    void addResourceAdapters() {
        addResourceAdapter(String, { "hello" })
    }

    @Override
    void addResourceResolverAdapters() {
        addResourceResolverAdapter(String, { "world" })
    }

    def "added resource adapter"() {
        expect:
        resourceResolver.getResource("/").adaptTo(String) == "hello"
    }

    def "added resource resolver adapter"() {
        expect:
        resourceResolver.adaptTo(String) == "world"
    }
}
