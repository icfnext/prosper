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
     * Writer for capturing tag output.
     */
    StringWriter writer

    /**
     * The JSP tag instance under test.
     */
    T tag

    /**
     * Instantiate the concrete tag class under test.
     *
     * @return tag instance to be tested
     */
    abstract T createTag()

    /**
     * Add additional attributes to the JSP page context for testing.  Implementing specs should override this method
     * as necessary.
     *
     * @return map of attributes names and values
     */
    Map<String, Object> addPageContextAttributes() {
        Collections.emptyMap()
    }

    /**
     * Get the result of the tag execution (i.e. the contents of the <code>StringWriter</code> containing the tag
     * output).  This is typically called after executing <code>doEndTag()</code> or other tag methods that write to the
     * page context output stream.
     *
     * @return string output
     */
    String getResult() {
        writer.toString()
    }

    /**
     * Create a mock page context that writes output to a StringWriter.  The resulting output can be retrieved by
     * calling <code>getResult()</code>.
     */
    def setup() {
        tag = createTag()
        writer = new StringWriter()

        def jspWriter = new MockJspWriter(writer)
        def pageContext = new MockPageContext() {

            @Override
            JspWriter getOut() {
                jspWriter
            }
        }

        pageContext.setAttribute DEFAULT_RESOURCE_RESOLVER_NAME, resourceResolver

        def attributes = addPageContextAttributes()

        attributes.each { name, value ->
            pageContext.setAttribute name, value
        }

        tag.pageContext = pageContext
    }
}