package com.citytechinc.aem.spock.specs

import com.day.cq.tagging.TagManager
import com.day.cq.wcm.api.PageManager

import javax.jcr.Session

class SlingRepositorySpec extends AbstractSlingRepositorySpec {

    @Override
    void addResourceAdapters() {
        addResourceAdapter(String, { "hello" })
    }

    @Override
    void addResourceResolverAdapters() {
        addResourceResolverAdapter(String, { "world" })
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
        !resourceResolver.adaptTo(Integer)
    }

    def "additional resource adapter"() {
        expect:
        resourceResolver.getResource("/").adaptTo(String) == "hello"
    }

    def "additional resource resolver adapter"() {
        expect:
        resourceResolver.adaptTo(String) == "world"
    }
}
