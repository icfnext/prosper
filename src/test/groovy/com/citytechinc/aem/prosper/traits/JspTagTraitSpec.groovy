package com.citytechinc.aem.prosper.traits

import com.citytechinc.aem.prosper.specs.ProsperSpec

import javax.servlet.jsp.JspException
import javax.servlet.jsp.tagext.TagSupport

class JspTagTraitSpec extends ProsperSpec implements JspTagTrait {

    static class TestTag extends TagSupport {

        @Override
        int doStartTag() throws JspException {
            pageContext.out.write("hello")

            EVAL_PAGE
        }

        @Override
        int doEndTag() throws JspException {
            pageContext.out.write(pageContext.getAttribute("testName") as String)

            EVAL_PAGE
        }
    }

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
