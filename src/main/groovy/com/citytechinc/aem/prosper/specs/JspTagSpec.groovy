package com.citytechinc.aem.prosper.specs

import groovy.transform.TupleConstructor
import org.springframework.mock.web.MockJspWriter
import org.springframework.mock.web.MockPageContext

import javax.servlet.jsp.JspWriter
import javax.servlet.jsp.PageContext
import javax.servlet.jsp.tagext.TagSupport

import static org.apache.sling.scripting.jsp.taglib.DefineObjectsTag.DEFAULT_RESOURCE_RESOLVER_NAME

/**
 * Spock specification for testing tag handlers.
 */
abstract class JspTagSpec extends ProsperSpec {

    @TupleConstructor
    class JspTag {

        PageContext pageContext

        Writer writer
    }

    /**
     * Initialize the given tag instance and return the writer for reading tag output.
     *
     * @param tag
     * @return JSP tag instance containing mock page context and writer
     */
    JspTag init(TagSupport tag) {
        init(tag, [:])
    }

    /**
     * Initialize the given tag instance and return the writer for reading tag output.
     *
     * @param tag
     * @param additionalPageContextAttributes
     * @return JSP tag instance containing mock page context and writer
     */
    JspTag init(TagSupport tag, Map<String, Object> additionalPageContextAttributes) {
        def writer = new StringWriter()
        def jspWriter = new MockJspWriter(writer)
        def pageContext = new MockPageContext() {

            @Override
            JspWriter getOut() {
                jspWriter
            }
        }

        pageContext.setAttribute(DEFAULT_RESOURCE_RESOLVER_NAME, resourceResolver)

        additionalPageContextAttributes.each { name, value ->
            pageContext.setAttribute(name, value)
        }

        tag.pageContext = pageContext

        new JspTag(pageContext, writer)
    }
}