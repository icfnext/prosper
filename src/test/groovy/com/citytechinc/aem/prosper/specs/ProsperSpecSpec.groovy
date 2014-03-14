package com.citytechinc.aem.prosper.specs

import com.day.cq.tagging.TagManager
import com.day.cq.wcm.api.PageManager
import org.apache.sling.api.adapter.AdapterFactory
import org.apache.sling.api.resource.Resource
import org.apache.sling.api.resource.ResourceResolver

import javax.jcr.Session

class ProsperSpecSpec extends ProsperSpec {

    @Override
    Collection<AdapterFactory> addAdapterFactories() {
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

        [adapterFactory]
    }

    @Override
    Map<Class, Closure> addResourceAdapters() {
        [(String.class): { "hello" }]
    }

    @Override
    Map<Class, Closure> addResourceResolverAdapters() {
        [(String.class): { "world" }]
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

    def "add resource adapter for test"() {
        setup:
        addResourceAdapter(Map, { [:] })

        expect:
        resourceResolver.getResource("/").adaptTo(Map) == [:]
    }

    def "add resource resolver adapter for test"() {
        setup:
        addResourceResolverAdapter(Map, { [:] })

        expect:
        resourceResolver.adaptTo(Map) == [:]
    }
}
