package com.citytechinc.aem.prosper.context

import org.apache.sling.api.SlingHttpServletRequest
import org.apache.sling.api.SlingHttpServletResponse
import org.springframework.mock.web.MockJspWriter
import org.springframework.mock.web.MockPageContext

import javax.servlet.jsp.JspWriter

/**
 * Extension of Spring <code>MockPageContext</code> that captures the tag output in a <code>Writer</code>.
 */
class ProsperPageContext extends MockPageContext {

    /**
     * Writer containing tag output.
     */
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
