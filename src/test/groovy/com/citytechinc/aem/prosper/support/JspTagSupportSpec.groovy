package com.citytechinc.aem.prosper.support

import com.citytechinc.aem.prosper.specs.ProsperSpec
import spock.lang.Shared

import javax.servlet.jsp.JspException
import javax.servlet.jsp.tagext.TagSupport

class JspTagSupportSpec extends ProsperSpec {

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

    @Shared
    JspTagSupport jspTagSupport

    def setupSpec() {
        jspTagSupport = new JspTagSupport(resourceResolver)
    }

    def "init tag and get result"() {
        setup:
        def proxy = jspTagSupport.init(TestTag)

        when:
        proxy.tag.doStartTag()

        then:
        proxy.output == "hello"
    }

    def "init tag with additional page context attributes and get result"() {
        setup:
        def proxy = jspTagSupport.init(TestTag, ["testName": "testValue"])

        when:
        proxy.tag.doEndTag()

        then:
        proxy.output == "testValue"
    }
}
