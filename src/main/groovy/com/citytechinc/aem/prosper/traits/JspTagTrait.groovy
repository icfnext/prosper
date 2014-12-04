package com.citytechinc.aem.prosper.traits

import com.citytechinc.aem.prosper.mocks.MockPageContext
import com.citytechinc.aem.prosper.tag.JspTagProxy
import org.springframework.mock.web.MockJspWriter

import javax.servlet.jsp.tagext.TagSupport

import static org.apache.sling.scripting.jsp.taglib.DefineObjectsTag.DEFAULT_RESOURCE_RESOLVER_NAME

trait JspTagTrait extends AbstractProsperTrait {

    /**
     * Initialize the tag instance for the given class.
     *
     * @param tagClass
     * @return proxy tag instance containing mocked page context and writer
     */
    public <T extends TagSupport> JspTagProxy<T> init(Class<T> tagClass) {
        init(tagClass, [:])
    }

    /**
     * Initialize the tag instance for the given class and additional page context attributes.
     *
     * @param tagClass
     * @param additionalPageContextAttributes
     * @return proxy tag instance containing mocked page context and writer
     */
    public <T extends TagSupport> JspTagProxy<T> init(Class<T> tagClass,
        Map<String, Object> additionalPageContextAttributes) {
        def writer = new StringWriter()
        def jspWriter = new MockJspWriter(writer)
        def pageContext = new MockPageContext(jspWriter)

        pageContext.setAttribute(DEFAULT_RESOURCE_RESOLVER_NAME, resourceResolver)

        additionalPageContextAttributes.each { name, value ->
            pageContext.setAttribute(name, value)
        }

        def tag = tagClass.newInstance()

        tag.pageContext = pageContext

        new JspTagProxy(tag, pageContext, writer)
    }
}
