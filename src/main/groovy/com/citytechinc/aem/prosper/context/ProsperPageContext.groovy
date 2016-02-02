package com.citytechinc.aem.prosper.context

import groovy.transform.TupleConstructor
import org.springframework.mock.web.MockJspWriter
import org.springframework.mock.web.MockPageContext

import javax.servlet.jsp.JspWriter

@TupleConstructor
class ProsperPageContext extends MockPageContext {

    Writer writer

    @Override
    JspWriter getOut() {
        new MockJspWriter(writer)
    }
}
