package com.citytechinc.aem.spock.specs.tag

import com.citytechinc.aem.spock.specs.AemSpec
import org.springframework.mock.web.MockJspWriter

import javax.servlet.jsp.PageContext
import javax.servlet.jsp.tagext.TagSupport

import static org.apache.sling.scripting.jsp.taglib.DefineObjectsTag.DEFAULT_RESOURCE_RESOLVER_NAME

/**
 * Spock specification for testing tag handlers.
 */
abstract class AbstractTagSpec extends AemSpec {

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

        tag.pageContext = pageContext
    }

    /**
     * Instantiate the concrete tag class under test.
     *
     * @return tag instance to be tested
     */
    abstract TagSupport createTag()

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