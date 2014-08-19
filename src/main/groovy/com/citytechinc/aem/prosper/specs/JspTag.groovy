package com.citytechinc.aem.prosper.specs

import groovy.transform.TupleConstructor

import javax.servlet.jsp.PageContext

/**
 * Composite class containing the mocked page context and writer for a JSP tag instance.
 */
@TupleConstructor
class JspTag {

    /**
     * Mock page context for tag under test.
     */
    PageContext pageContext

    /**
     * Writer for capturing tag output.
     */
    Writer writer

    /**
     * Get the output value for the JSP writer.
     *
     * @return output string
     */
    String getOutput() {
        writer.toString()
    }
}
