package com.icfolson.aem.prosper.specs

import com.icfolson.aem.prosper.annotations.NodeTypes

@NodeTypes([
    "SLING-INF/nodetypes/spock.cnd",
    "/SLING-INF/nodetypes/prosper.cnd"
])
class NodeTypesSpec extends ProsperSpec {

    def setupSpec() {
        nodeBuilder.etc {
            prosper("prosper:TestType")
            spock("spock:TestType")
        }
    }
}
