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

    abstract Map<Class, Closure> addAdapters()
}
