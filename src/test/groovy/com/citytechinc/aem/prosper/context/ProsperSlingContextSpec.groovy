package com.citytechinc.aem.prosper.context

import com.citytechinc.aem.prosper.adapters.OSGiRegisteredAdapterFactory
import com.citytechinc.aem.prosper.specs.ProsperSpec
import org.apache.sling.api.SlingHttpServletRequest
import org.apache.sling.api.adapter.AdapterFactory
import org.apache.sling.api.resource.Resource
import org.apache.sling.api.resource.ResourceResolver
import org.apache.sling.models.annotations.Model
import org.apache.sling.models.annotations.injectorspecific.Self
import spock.lang.Ignore

class ProsperSlingContextSpec extends ProsperSpec {

    @Model(adaptables = [Resource, SlingHttpServletRequest])
    static class ResourceModel {

        @Self
        Resource resource

        String getPath() {
            resource.path
        }
    }

    def setupSpec() {
        slingContext.addModelsForPackage("com.citytechinc.aem.prosper.context")
    }

    def "adapt resource to model"() {
        setup:
        def resource = getResource("/content/prosper")
        def model = resource.adaptTo(ResourceModel)

        expect:
        model.path == "/content/prosper"
    }

    def "adapt request to model"() {
        setup:
        def request = requestBuilder.build {
            path = "/content/prosper"
        }

        def model = request.adaptTo(ResourceModel)

        expect:
        model.path == "/content/prosper"
    }

    @Ignore
    def "test adapter manager respects OSGi service properties"() {
        given: "an OSGi registered adapter factory is added"
        slingContext.registerAdapterFactory(new OSGiRegisteredAdapterFactory(), null, null)

        when: "a request is adapted"
        def requestResult = requestBuilder.build().adaptTo(Long)

        then: "a valid result is returned"
        requestResult == 1984l

        when: "a resource resolve is adapted"
        def resourceResolverResult = resourceResolver.adaptTo(Long)

        then: "a result is not returned"
        resourceResolverResult == null
    }

    @Ignore
    def "test adapter factory without OSGi service properties is always called"() {
        given: "an adapter factory without OSGi properties"
        def adapterFactory = new AdapterFactory() {
            @Override
            <AdapterType> AdapterType getAdapter(Object o, Class<AdapterType> aClass) {
                (AdapterType) 157
            }
        }

        slingContext.registerAdapterFactory(adapterFactory, [ResourceResolver.name] as String[],
            [Integer.name] as String[])

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
