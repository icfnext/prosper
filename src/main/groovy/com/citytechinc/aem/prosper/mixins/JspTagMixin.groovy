package com.citytechinc.aem.prosper.mixins

import com.citytechinc.aem.prosper.specs.ProsperSpec
import com.citytechinc.aem.prosper.tag.JspTagProxy
import org.springframework.mock.web.MockJspWriter
import org.springframework.mock.web.MockPageContext

import javax.servlet.jsp.JspWriter
import javax.servlet.jsp.tagext.TagSupport

import static org.apache.sling.scripting.jsp.taglib.DefineObjectsTag.DEFAULT_RESOURCE_RESOLVER_NAME

/**
 * Mixin providing methods for initializing JSP tag support classes with a mocked page context.
 */
class JspTagMixin extends ProsperMixin {

    JspTagMixin(ProsperSpec spec) {
        super(spec)
    }

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
        def pageContext = new MockPageContext() {
            @Override
            JspWriter getOut() {
                new MockJspWriter(writer)
            }
        }

        pageContext.setAttribute(DEFAULT_RESOURCE_RESOLVER_NAME, spec.resourceResolver)

        additionalPageContextAttributes.each { name, value ->
            pageContext.setAttribute(name, value)
        }

        def tag = tagClass.newInstance()

        tag.pageContext = pageContext

        new JspTagProxy(tag, pageContext, writer)
    }
}
