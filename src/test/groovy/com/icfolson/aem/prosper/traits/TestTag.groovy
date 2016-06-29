package com.icfolson.aem.prosper.traits

import javax.servlet.jsp.JspException
import javax.servlet.jsp.tagext.TagSupport

class TestTag extends TagSupport {

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
