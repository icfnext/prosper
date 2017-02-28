package com.icfolson.aem.prosper.specs

import com.day.cq.tagging.TagManager
import com.day.cq.wcm.api.PageManager
import com.icfolson.aem.prosper.adapter.ProsperAdapterFactory
import io.wcm.testing.mock.aem.MockAemAdapterFactory
import org.apache.sling.api.adapter.AdapterFactory
import org.apache.sling.testing.mock.sling.junit.SlingContext
import org.junit.ClassRule
import spock.lang.Shared
import spock.lang.Specification

import static org.apache.sling.api.adapter.AdapterFactory.ADAPTABLE_CLASSES
import static org.apache.sling.api.adapter.AdapterFactory.ADAPTER_CLASSES
import static org.apache.sling.testing.mock.sling.ResourceResolverType.JCR_OAK

class RuleSpec extends Specification {

    @ClassRule
    @Shared
    SlingContext slingContext = new SlingContext(JCR_OAK)

    def setupSpec() {
        // register prosper adapter factory
        registerAdapterFactory(new ProsperAdapterFactory(), ProsperAdapterFactory.ADAPTABLE_CLASSES,
            ProsperAdapterFactory.ADAPTER_CLASSES)

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

    void registerAdapterFactory(AdapterFactory adapterFactory, String[] adaptableClasses, String[] adapterClasses) {
        slingContext.registerService(AdapterFactory, adapterFactory, [
            (ADAPTABLE_CLASSES): adaptableClasses,
            (ADAPTER_CLASSES): adapterClasses
        ])
    }
}
