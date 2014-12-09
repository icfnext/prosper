package com.citytechinc.aem.prosper.traits

import com.citytechinc.aem.prosper.mocks.MockPageContext
import com.citytechinc.aem.prosper.tag.JspTagProxy

import javax.servlet.jsp.tagext.TagSupport

import static org.apache.sling.scripting.jsp.taglib.DefineObjectsTag.DEFAULT_RESOURCE_RESOLVER_NAME

trait JspTagTrait extends AbstractProsperTrait {

    /**
     * Initialize the tag instance for the given class.
     *
     * @param tagClass tag class to initialize
     * @return proxy tag instance containing mocked page context and writer
     */
    public <T extends TagSupport> JspTagProxy<T> init(Class<T> tagClass) {
        init(tagClass, [:])
    }

    /**
     * Initialize the tag instance for the given class and additional page context attributes.
     *
     * @param tagClass tag class to initialize
     * @param additionalPageContextAttributes
     * @return proxy tag instance containing mocked page context and writer
     */
    public <T extends TagSupport> JspTagProxy<T> init(Class<T> tagClass,
        Map<String, Object> additionalPageContextAttributes) {
        def writer = new StringWriter()
        def pageContext = new MockPageContext(writer)

        pageContext.setAttribute(DEFAULT_RESOURCE_RESOLVER_NAME, resourceResolver)

        additionalPageContextAttributes.each { name, value ->
            pageContext.setAttribute(name, value)
        }

        def tag = tagClass.newInstance()

        tag.pageContext = pageContext

        new JspTagProxy(tag, pageContext, writer)
    }
}
