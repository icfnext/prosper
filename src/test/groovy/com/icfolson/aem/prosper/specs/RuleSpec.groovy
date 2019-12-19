package com.icfolson.aem.prosper.specs

import com.day.cq.tagging.TagManager
import com.day.cq.wcm.api.PageManager
import io.wcm.testing.mock.aem.MockAemAdapterFactory
import org.apache.sling.api.adapter.AdapterFactory
import org.apache.sling.testing.mock.sling.junit.SlingContext
import org.junit.ClassRule
import spock.lang.Shared
import spock.lang.Specification

import static org.apache.sling.testing.mock.sling.ResourceResolverType.JCR_OAK

class RuleSpec extends Specification {

    @ClassRule
    @Shared
    SlingContext slingContext = new SlingContext(JCR_OAK)

    def setupSpec() {
        // register mock adapter factory
        registerAdapterFactory(new MockAemAdapterFactory())
    }

    def "adapt to page manager"() {
        expect:
        slingContext.resourceResolver().adaptTo(PageManager)
    }

    def "adapt to tag manager"() {
        expect:
        slingContext.resourceResolver().adaptTo(TagManager)
    }

    void registerAdapterFactory(AdapterFactory adapterFactory) {
        slingContext.registerService(AdapterFactory, adapterFactory)
    }
}
