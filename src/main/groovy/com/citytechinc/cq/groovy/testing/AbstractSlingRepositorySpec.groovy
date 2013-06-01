package com.citytechinc.cq.groovy.testing

import com.citytechinc.cq.groovy.testing.resource.TestingResourceResolver
import spock.lang.Shared

/**
 * Spock specification for JCR testing that includes a Sling resource resolver.
 */
abstract class AbstractSlingRepositorySpec extends AbstractRepositorySpec {

    @Shared resourceResolver

    def setupSpec() {
        def adapters = addAdapters()

        resourceResolver = new TestingResourceResolver(session) {
            @Override
            <AdapterType> AdapterType adaptTo(Class<AdapterType> clazz) {
                def found = adapters.find { it.key == clazz }

                found ? (AdapterType) found.value.call(this) : super.adaptTo(clazz)
            }
        }
    }

    /**
     * Implementing specs can override this method to add adapters to the Sling <code>ResourceResolver</code> at runtime.
     *
     * @return map of classes to their adapter functions
     */
    Map<Class, Closure> addAdapters() {

    }
}
