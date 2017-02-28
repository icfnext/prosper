package com.icfolson.aem.prosper.specs

import spock.lang.Unroll

@Unroll
class ExtendingNodeTypesSpec extends NodeTypesSpec {

    def "check node type for node with custom type"() {
        expect:
        getNode(path).isNodeType(nodeType)

        where:
        path           | nodeType
        "/etc/prosper" | "prosper:TestType"
        "/etc/spock"   | "spock:TestType"
    }
}
