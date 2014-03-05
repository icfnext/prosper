package com.citytechinc.aem.spock.specs

import com.day.cq.tagging.TagManager
import com.day.cq.wcm.api.PageManager
import org.apache.sling.api.adapter.AdapterFactory
import org.apache.sling.api.resource.Resource
import org.apache.sling.api.resource.ResourceResolver

import javax.jcr.Session

class SlingRepositorySpec extends AbstractSlingRepositorySpec {

    def setupSpec() {
        def adapterFactory = new AdapterFactory() {
            @Override
            def <AdapterType> AdapterType getAdapter(Object adaptable, Class<AdapterType> type) {
                def result

                if (adaptable instanceof Resource) {
                    result = type == Integer ? 1982 : null
                } else if (adaptable instanceof ResourceResolver) {
                    result = type == Integer ? 2014 : null
                } else {
                    result = null
                }

                (AdapterType) result
            }
        }

        registerAdapterFactory(adapterFactory)
        addResourceAdapter(String, { "hello" })
        addResourceResolverAdapter(String, { "world" })
    }

    def "registered adapter factory"() {
        expect:
        resourceResolver.adaptTo(Integer) == 2014
        resourceResolver.getResource("/").adaptTo(Integer) == 1982
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
        !resourceResolver.adaptTo(Boolean)
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
