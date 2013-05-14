package com.citytechinc.cq.testing.tag

import com.citytechinc.cq.testing.AbstractRepositorySpec
import org.springframework.mock.web.MockJspWriter

import javax.servlet.jsp.PageContext
import javax.servlet.jsp.tagext.TagSupport

abstract class AbstractTagSpec extends AbstractRepositorySpec {

    def mockPageContext(TagSupport tag, StringWriter writer) {
        def pageContext = Mock(PageContext)
        def jspWriter = new MockJspWriter(writer)

        pageContext.out >> jspWriter

        tag.pageContext = pageContext

        pageContext
    }
}