package com.citytechinc.aem.prosper.context

import org.apache.sling.api.SlingHttpServletRequest
import org.apache.sling.api.SlingHttpServletResponse
import org.springframework.mock.web.MockJspWriter
import org.springframework.mock.web.MockPageContext

import javax.servlet.jsp.JspWriter

class ProsperPageContext extends MockPageContext {

    Writer writer

    ProsperPageContext(SlingHttpServletRequest request, SlingHttpServletResponse response, Writer writer) {
        super(null, request, response)

        this.writer = writer
    }

    @Override
    JspWriter getOut() {
        new MockJspWriter(writer)
    }
}
