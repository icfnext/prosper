package com.citytechinc.aem.prosper.mocks

import org.springframework.mock.web.MockJspWriter

import javax.servlet.jsp.JspWriter
import javax.servlet.jsp.PageContext

class MockPageContext extends PageContext {

    @Delegate
    org.springframework.mock.web.MockPageContext pageContext = new org.springframework.mock.web.MockPageContext()

    StringWriter writer

    MockPageContext(StringWriter writer) {
        this.writer = writer
    }

    @Override
    JspWriter getOut() {
        new MockJspWriter(writer)
    }
}
