package com.citytechinc.cq.testing.resource

import com.citytechinc.cq.testing.AbstractRepositorySpec
import spock.lang.Shared

class TestingResourceSpec extends AbstractRepositorySpec {

    @Shared resourceResolver

    def setupSpec() {
        resourceResolver = new TestingResourceResolver(session)
    }

    def "get children"() {

    }
}
