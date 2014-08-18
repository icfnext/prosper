package com.citytechinc.aem.prosper.specs

import org.springframework.mock.web.MockJspWriter
import org.springframework.mock.web.MockPageContext

import javax.servlet.jsp.JspWriter
import javax.servlet.jsp.tagext.TagSupport

import static org.apache.sling.scripting.jsp.taglib.DefineObjectsTag.DEFAULT_RESOURCE_RESOLVER_NAME

/**
 * Spock specification for testing tag handlers.
 */
abstract class JspTagSpec<T extends TagSupport> extends ProsperSpec {

    /**
     * Initialize the given tag instance and return the writer for reading tag output.
     *
     * @param tag
     * @return
     */
    Writer init(TagSupport tag) {
        init(tag, [:])
    }

    /**
     * Initialize the given tag instance and return the writer for reading tag output.
     *
     * @param tag
     * @param additionalPageContextAttributes
     * @return
     */
    Writer init(TagSupport tag, Map<String, Object> additionalPageContextAttributes) {
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

        writer
    }
}