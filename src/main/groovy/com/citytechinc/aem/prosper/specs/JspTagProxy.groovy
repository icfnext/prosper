package com.citytechinc.aem.prosper.specs

import javax.servlet.jsp.PageContext

/**
 * Composite class containing the mocked page context and writer for a JSP tag instance.
 */
final class JspTagProxy {

    /**
     * Mock page context for tag under test.
     */
    final PageContext pageContext

    /**
     * Writer for capturing tag output.
     */
    private final Writer writer

    JspTagProxy(PageContext pageContext, Writer writer) {
        this.pageContext = pageContext
        this.writer = writer
    }

    /**
     * Get the output value for the JSP writer.
     *
     * @return output string
     */
    final String getOutput() {
        writer.toString()
    }
}
