package com.citytechinc.aem.prosper.specs

import javax.servlet.jsp.JspException
import javax.servlet.jsp.tagext.TagSupport

class JspTagSpecSpec extends JspTagSpec {

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
        def tag = new TestTag()
        def proxy = init(tag)

        when:
        tag.doStartTag()

        then:
        proxy.output == "hello"
    }

    def "init tag with additional page context attributes and get result"() {
        setup:
        def tag = new TestTag()
        def proxy = init(tag, ["testName": "testValue"])

        when:
        tag.doEndTag()

        then:
        proxy.output == "testValue"
    }
}