package com.icfolson.aem.prosper.specs

import com.icfolson.aem.prosper.annotations.NodeTypes
import spock.lang.Unroll

@NodeTypes([
    "SLING-INF/nodetypes/spock.cnd",
    "/SLING-INF/nodetypes/prosper.cnd"
])
@Unroll
class NodeTypesSpec extends ProsperSpec {

    def setupSpec() {
        nodeBuilder.etc {
            prosper("prosper:TestType")
            spock("spock:TestType")
        }
    }

    def "nodes have custom node types"() {
        expect:
        getNode(path).isNodeType(nodeType)

        where:
        path           | nodeType
        "/etc/prosper" | "prosper:TestType"
        "/etc/spock"   | "spock:TestType"
    }
}
