package com.citytechinc.aem.prosper.traits

import com.citytechinc.aem.prosper.specs.ProsperSpec

class ProsperAssertsSpec extends ProsperSpec {

    def "assert node exists"() {
        expect:
        assertNodeExists("/etc")
    }
}
