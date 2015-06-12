package com.citytechinc.aem.prosper.mocks.adapter

import com.citytechinc.aem.prosper.adapters.OSGiRegisteredAdapterFactory
import com.citytechinc.aem.prosper.specs.ProsperSpec
import com.day.cq.wcm.api.PageManager
import org.apache.sling.api.adapter.AdapterFactory
import org.apache.sling.testing.mock.osgi.MockOsgi
import org.osgi.framework.BundleContext

class TestAdapterManagerSpec extends ProsperSpec {
    def "empty bundleContext returns null for adapted objects"() {
        given: "an empty bundle context"
        final BundleContext emptyBundleContext = MockOsgi.newBundleContext()

        and: "an adapter manager built off the empty bundle context"
        final TestAdapterManager adapterManager = new TestAdapterManager(emptyBundleContext)

        when: "an object is adapted using the adapter manager"
        def adapted = adapterManager.adapt(resourceResolver, PageManager)

        then: "null is returned"
        adapted == null
    }

    def "test adapter manager respects OSGi service properties"() {
        given: "an OSGi registered adapter factory is added"
        addAdapter(new OSGiRegisteredAdapterFactory())

        when: "a request is adapted"
        final Long requestResult = requestBuilder.build().adaptTo(Long)

        then: "a valid result is returned"
        requestResult == 1984l

        when: "a resource resolve is adapted"
        final Long resourceResolverResult = resourceResolver.adaptTo(Long)

        then: "a result is not returned"
        resourceResolverResult == null
    }

    def "test adapter factory without OSGi service properties is always called"() {
        given: "an adapter factory without OSGi properties"
        addAdapter(new AdapterFactory() {
            @Override
            def <AdapterType> AdapterType getAdapter(final Object o, final Class<AdapterType> aClass) {
                return (AdapterType) 157
            }
        })

        when: "a request is adapted"
        final Integer requestResult = requestBuilder.build().adaptTo(Integer)

        then: "a valid result is returned"
        requestResult == 157

        when: "a resource resolve is adapted"
        final Integer resourceResolverResult = resourceResolver.adaptTo(Integer)

        then: "a valid result is returned"
        resourceResolverResult == 157
    }
}
