package com.citytechinc.cq.testing.mock

import com.citytechinc.cq.testing.AbstractRepositorySpec
import spock.lang.Shared

class MockResourceSpec extends AbstractRepositorySpec {

    @Shared resourceResolver

    def setupSpec() {
        resourceResolver = new MockResourceResolver(session)
    }

    def "get children"() {

    }
}
