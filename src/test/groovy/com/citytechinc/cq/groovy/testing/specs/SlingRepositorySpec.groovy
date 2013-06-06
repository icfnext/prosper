package com.citytechinc.cq.groovy.testing.specs

class SlingRepositorySpec extends AbstractSlingRepositorySpec {

    @Override
    void addAdapters() {
        addAdapter(String, { "hello!" })
    }

    def "added adapter"() {
        expect:
        resourceResolver.adaptTo(String) == "hello!"
    }
}
