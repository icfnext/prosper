package com.citytechinc.aem.prosper.traits

import com.citytechinc.aem.prosper.builders.RequestBuilder
import com.citytechinc.aem.prosper.builders.ResponseBuilder
import com.citytechinc.aem.prosper.context.ProsperPageContext
import com.citytechinc.aem.prosper.context.ProsperSlingContext
import com.citytechinc.aem.prosper.tag.JspTagProxy
import com.day.cq.wcm.api.PageManager
import org.apache.sling.api.SlingHttpServletRequest
import org.apache.sling.api.SlingHttpServletResponse
import org.apache.sling.api.resource.Resource
import org.apache.sling.api.resource.ResourceResolver
import org.apache.sling.api.resource.ValueMap
import org.apache.sling.testing.mock.sling.MockSling

import javax.jcr.Node
import javax.servlet.jsp.PageContext
import javax.servlet.jsp.tagext.TagSupport

import static com.day.cq.wcm.tags.DefineObjectsTag.DEFAULT_CURRENT_PAGE_NAME
import static com.day.cq.wcm.tags.DefineObjectsTag.DEFAULT_PAGE_MANAGER_NAME
import static com.day.cq.wcm.tags.DefineObjectsTag.DEFAULT_PAGE_PROPERTIES_NAME
import static com.day.cq.wcm.tags.DefineObjectsTag.DEFAULT_PROPERTIES_NAME
import static org.apache.sling.scripting.jsp.taglib.DefineObjectsTag.DEFAULT_NODE_NAME
import static org.apache.sling.scripting.jsp.taglib.DefineObjectsTag.DEFAULT_REQUEST_NAME
import static org.apache.sling.scripting.jsp.taglib.DefineObjectsTag.DEFAULT_RESOURCE_NAME
import static org.apache.sling.scripting.jsp.taglib.DefineObjectsTag.DEFAULT_RESOURCE_RESOLVER_NAME
import static org.apache.sling.scripting.jsp.taglib.DefineObjectsTag.DEFAULT_RESPONSE_NAME
import static org.apache.sling.scripting.jsp.taglib.DefineObjectsTag.DEFAULT_SLING_NAME

/**
 * Trait providing methods for initializing JSP tag support classes with a mocked page context.
 */
trait JspTagTrait {

    abstract ResourceResolver getResourceResolver()

    abstract ProsperSlingContext getSlingContext()

    abstract RequestBuilder getRequestBuilder()

    abstract ResponseBuilder getResponseBuilder()

    /**
     * Initialize the tag instance for the given class.
     *
     * @param tagClass tag class to initialize
     * @param resourcePath path to set <code>Resource</code> instance and related objects in page context
     * @return proxy tag instance containing mocked page context and writer
     */
    JspTagProxy init(Class<TagSupport> tagClass, String resourcePath) {
        init(tagClass, resourcePath, [:])
    }

    /**
     * Initialize the tag instance for the given class and additional page context attributes.
     *
     * @param tagClass tag class to initialize
     * @param resourcePath path to set <code>Resource</code> instance and related objects in page context
     * @param additionalPageContextAttributes additional attributes to set in the mocked page context
     * @return proxy tag instance containing mocked page context and writer
     */
    JspTagProxy init(Class<TagSupport> tagClass, String resourcePath,
        Map<String, Object> additionalPageContextAttributes) {
        def resource = resourceResolver.resolve(resourcePath)
        def request = requestBuilder.setPath(resource.path).build()
        def response = responseBuilder.build()

        def writer = new StringWriter()
        def pageContext = new ProsperPageContext(request, response, writer)

        setDefaultPageContextAttributes(pageContext, request, response, resource)

        additionalPageContextAttributes.each { name, value ->
            pageContext.setAttribute(name, value)
        }

        def tag = tagClass.newInstance()

        tag.pageContext = pageContext

        new JspTagProxy(tag, pageContext, writer)
    }

    private void setDefaultPageContextAttributes(PageContext pageContext, SlingHttpServletRequest request,
        SlingHttpServletResponse response, Resource resource) {
        def pageManager = resourceResolver.adaptTo(PageManager)
        def currentPage = pageManager.getContainingPage(resource)

        pageContext.setAttribute(DEFAULT_RESOURCE_RESOLVER_NAME, resourceResolver)
        pageContext.setAttribute(DEFAULT_RESOURCE_NAME, resource)
        pageContext.setAttribute(DEFAULT_PROPERTIES_NAME, resource.valueMap)
        pageContext.setAttribute(DEFAULT_NODE_NAME, resource.adaptTo(Node))
        pageContext.setAttribute(DEFAULT_REQUEST_NAME, request)
        pageContext.setAttribute(DEFAULT_RESPONSE_NAME, response)
        pageContext.setAttribute(DEFAULT_SLING_NAME, MockSling.newSlingScriptHelper(request, response,
            slingContext.bundleContext()))
        pageContext.setAttribute(DEFAULT_PAGE_MANAGER_NAME, pageManager)
        pageContext.setAttribute(DEFAULT_CURRENT_PAGE_NAME, currentPage)
        pageContext.setAttribute(DEFAULT_PAGE_PROPERTIES_NAME, currentPage ? currentPage.properties : ValueMap.EMPTY)
    }
}