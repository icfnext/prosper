package com.citytechinc.aem.prosper.mocks

import javax.servlet.jsp.JspWriter
import javax.servlet.jsp.PageContext

class MockPageContext extends PageContext {

    @Delegate
    org.springframework.mock.web.MockPageContext pageContext = new org.springframework.mock.web.MockPageContext()

    JspWriter jspWriter

    MockPageContext(JspWriter jspWriter) {
        this.jspWriter = jspWriter
    }

    @Override
    JspWriter getOut() {
        jspWriter
    }
}
