package com.citytechinc.cq.testing.tag

import com.citytechinc.cq.testing.AbstractRepositorySpec
import org.springframework.mock.web.MockJspWriter

import javax.servlet.jsp.PageContext

/**
 * Spock specification for testing tag handlers.
 */
abstract class AbstractTagSpec extends AbstractRepositorySpec {

    def tag

    def writer

    def setup() {
        tag = createTag()
        writer = new StringWriter()
    }

    /**
     * Instantiate the concrete tag class under test.
     *
     * @return tag instance to be tested
     */
    abstract def createTag()

    /**
     * Create a mock page context that writes output to a StringWriter.  The resulting output can be retrieved by
     * calling <code>getResult()</code>.
     *
     * @return mocked page context
     */
    def mockPageContext() {
        def pageContext = Mock(PageContext)
        def jspWriter = new MockJspWriter(writer)

        pageContext.out >> jspWriter

        tag.pageContext = pageContext

        pageContext
    }

    def getResult() {
        writer.toString()
    }
}