package com.citytechinc.cq.groovy.testing.specs

import com.citytechinc.cq.groovy.testing.builders.RequestBuilder
import com.citytechinc.cq.groovy.testing.mocks.resource.MockResourceResolver
import spock.lang.Shared

/**
 * Spock specification for JCR testing that includes a Sling resource resolver.
 */
abstract class AbstractSlingRepositorySpec extends AbstractRepositorySpec {

    @Shared resourceResolver

    @Shared adapters = [:]

    def setupSpec() {
        addAdapters()

        def sharedAdapters = adapters

        resourceResolver = new MockResourceResolver(session) {
            @Override
            <AdapterType> AdapterType adaptTo(Class<AdapterType> clazz) {
                def found = sharedAdapters.find { it.key == clazz }

                found ? (AdapterType) found.value.call(this) : super.adaptTo(clazz)
            }
        }
    }

    /**
     * Implementing specs should override this method to add adapters to the Sling <code>ResourceResolver</code> at runtime.
     */
    void addAdapters() {

    }

    /**
     * Add an adapter type with an instantiation function.
     *
     * @param type adapter type
     * @param c closure to instantiate the provided adapter type; closure may contain a <code>ResourceResolver</code> argument
     */
    void addAdapter(Class type, Closure c) {
        adapters[type] = c
    }

    /**
     * Get a request builders for the given path.
     *
     * @param path JCR path for request
     * @return request builders instance for the given resource path
     */
    RequestBuilder getRequestBuilder(String path) {
        new RequestBuilder(resourceResolver, path)
    }
}
