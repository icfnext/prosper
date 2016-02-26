package com.citytechinc.aem.prosper.adapter

import com.citytechinc.aem.prosper.adapters.OSGiRegisteredAdapterFactory
import com.citytechinc.aem.prosper.context.ProsperSlingContext
import com.citytechinc.aem.prosper.specs.ProsperSpec
import com.day.cq.wcm.api.PageManager
import org.apache.sling.api.adapter.AdapterFactory
import spock.lang.IgnoreRest

class ProsperAdapterManagerSpec extends ProsperSpec {

    def "empty bundleContext returns null for adapted objects"() {
        given: "a new sling context"
        def slingContext = new ProsperSlingContext()

        and: "an adapter manager built off the empty bundle context"
        def adapterManager = new ProsperAdapterManager(slingContext)

        when: "an object is adapted using the adapter manager"
        def adapted = adapterManager.getAdapter(resourceResolver, PageManager)

        then: "null is returned"
        adapted == null
    }

    def "test adapter manager respects OSGi service properties"() {
        given: "an OSGi registered adapter factory is added"
        adapterManager.addAdapterFactory(new OSGiRegisteredAdapterFactory())

        when: "a request is adapted"
        def requestResult = requestBuilder.build().adaptTo(Long)

        then: "a valid result is returned"
        requestResult == 1984l

        when: "a resource resolve is adapted"
        def resourceResolverResult = resourceResolver.adaptTo(Long)

        then: "a result is not returned"
        resourceResolverResult == null
    }

    @IgnoreRest
    def "test adapter factory without OSGi service properties is always called"() {
        given: "an adapter factory without OSGi properties"
        adapterManager.addAdapterFactory(new AdapterFactory() {
            @Override
            <AdapterType> AdapterType getAdapter(Object o, Class<AdapterType> aClass) {
                (AdapterType) 157
            }
        })

        when: "a request is adapted"
        def requestResult = requestBuilder.build().adaptTo(Integer)

        then: "a valid result is returned"
        requestResult == 157

        when: "a resource resolve is adapted"
        def resourceResolverResult = resourceResolver.adaptTo(Integer)

        then: "a valid result is returned"
        resourceResolverResult == 157
    }
}
