package com.citytechinc.aem.prosper.traits

import com.citytechinc.aem.prosper.specs.ProsperSpec

class JspTagTraitSpec extends ProsperSpec implements JspTagTrait {

    def "init tag and get result"() {
        setup:
        def proxy = init(TestTag)

        when:
        proxy.tag.doStartTag()

        then:
        proxy.output == "hello"
    }

    def "init tag with additional page context attributes and get result"() {
        setup:
        def proxy = init(TestTag, ["testName": "testValue"])

        when:
        proxy.tag.doEndTag()

        then:
        proxy.output == "testValue"
    }
}
