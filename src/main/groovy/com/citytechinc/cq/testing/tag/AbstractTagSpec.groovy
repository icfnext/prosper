package com.citytechinc.cq.testing.tag

import com.citytechinc.cq.testing.AbstractRepositorySpec
import org.springframework.mock.web.MockJspWriter
import spock.lang.Shared

import javax.servlet.jsp.PageContext

abstract class AbstractTagSpec extends AbstractRepositorySpec {

    /**
     * Tag instance is shared, but instantiation is delegated to the concrete test class.
     */
    @Shared tag

    @Shared writer

    /**
     * Instantiate the tag class being tested.
     */
    abstract void createTag()

    /**
     * Before each test, create a new writer and tag instance.
     */
    def setup() {
        writer = new StringWriter()

        createTag()
    }

    def mockPageContext() {
        def pageContext = Mock(PageContext)
        def jspWriter = new MockJspWriter(writer)

        pageContext.out >> jspWriter

        pageContext
    }

    def getOutput() {
        tag.doStartTag()
        tag.doEndTag()

        writer.toString()
    }
}