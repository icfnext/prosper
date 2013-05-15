package com.citytechinc.cq.testing

import com.citytechinc.cq.testing.resource.TestingResourceResolver
import spock.lang.Shared

/**
 * Spock specification for JCR testing that includes a Sling resource resolver.
 */
abstract class AbstractSlingRepositorySpec extends AbstractRepositorySpec {

    @Shared resourceResolver

    def setupSpec() {
        resourceResolver = new TestingResourceResolver(session)
    }
}
