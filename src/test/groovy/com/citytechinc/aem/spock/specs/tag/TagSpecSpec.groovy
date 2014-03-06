package com.citytechinc.aem.spock.specs.tag

import javax.servlet.jsp.JspException
import javax.servlet.jsp.tagext.TagSupport

class TagSpecSpec extends TagSpec {

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

    def "verify output"() {
        when:
        tag.doEndTag()

        then:
        result == "hello"
    }
}