package com.citytechinc.cq.groovy.testing

import com.citytechinc.cq.groovy.testing.resource.TestingResourceResolver
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

        resourceResolver = new TestingResourceResolver(session) {
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
    abstract void addAdapters()

    void addAdapter(Class type, Closure c) {
        adapters[type] = c
    }
}
