package com.citytechinc.aem.prosper.support

import com.citytechinc.aem.prosper.tag.JspTag
import org.apache.sling.api.resource.ResourceResolver
import org.springframework.mock.web.MockJspWriter
import org.springframework.mock.web.MockPageContext

import javax.servlet.jsp.JspWriter
import javax.servlet.jsp.tagext.TagSupport

import static org.apache.sling.scripting.jsp.taglib.DefineObjectsTag.DEFAULT_RESOURCE_RESOLVER_NAME

class JspTagSupport {

    private final ResourceResolver resourceResolver

    JspTagSupport(ResourceResolver resourceResolver) {
        this.resourceResolver = resourceResolver
    }

    /**
     * Initialize the tag instance for the given class.
     *
     * @param tagClass
     * @return proxy tag instance containing mocked page context and writer
     */
    public <T extends TagSupport> JspTag getJspTag(Class<T> tagClass) {
        getJspTag(tagClass, [:])
    }

    /**
     * Initialize the tag instance for the given class and additional page context attributes.
     *
     * @param tagClass
     * @param additionalPageContextAttributes
     * @return proxy tag instance containing mocked page context and writer
     */
    public <T extends TagSupport> JspTag getJspTag(Class<T> tagClass,
        Map<String, Object> additionalPageContextAttributes) {
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

        def tag = tagClass.newInstance()

        tag.pageContext = pageContext

        new JspTag(tag, pageContext, writer)
    }
}
