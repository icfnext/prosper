package com.citytechinc.aem.prosper.specs.tag

import com.citytechinc.aem.prosper.specs.AemSpec
import org.springframework.mock.web.MockJspWriter

import javax.servlet.jsp.PageContext
import javax.servlet.jsp.tagext.TagSupport

import static org.apache.sling.scripting.jsp.taglib.DefineObjectsTag.DEFAULT_RESOURCE_RESOLVER_NAME

/**
 * Spock specification for testing tag handlers.
 */
abstract class TagSpec extends AemSpec {

    private StringWriter writer

    TagSupport tag

    /**
     * Create a mock page context that writes output to a StringWriter.  The resulting output can be retrieved by
     * calling <code>getResult()</code>.
     */
    def setup() {
        tag = createTag()
        writer = new StringWriter()

        def pageContext = Mock(PageContext)
        def jspWriter = new MockJspWriter(writer)

        pageContext.out >> jspWriter
        pageContext.getAttribute(DEFAULT_RESOURCE_RESOLVER_NAME) >> resourceResolver

        def attributes = addPageContextAttributes()

        attributes.each { name, value ->
            pageContext.getAttribute(name) >> value
        }

        tag.pageContext = pageContext
    }

    /**
     * Instantiate the concrete tag class under test.
     *
     * @return tag instance to be tested
     */
    abstract TagSupport createTag()

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
}