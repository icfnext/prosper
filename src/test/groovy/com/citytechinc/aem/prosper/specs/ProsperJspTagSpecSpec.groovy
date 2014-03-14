package com.citytechinc.aem.prosper.specs

import javax.servlet.jsp.JspException
import javax.servlet.jsp.tagext.TagSupport

class ProsperJspTagSpecSpec extends ProsperJspTagSpec {

    @Override
    TagSupport createTag() {
        def tag = new TagSupport() {
            @Override
            int doEndTag() throws JspException {
                pageContext.out.write("hello")

                EVAL_PAGE
            }
        }

        tag
    }

    @Override
    Map<String, Object> addPageContextAttributes() {
        ["testName": "testValue"]
    }

    def "get result"() {
        when:
        tag.doEndTag()

        then:
        result == "hello"
    }

    def "add page context attributes"() {
        expect:
        tag.pageContext.getAttribute("testName") == "testValue"
    }
}