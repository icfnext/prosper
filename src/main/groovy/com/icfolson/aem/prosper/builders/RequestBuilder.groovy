package com.icfolson.aem.prosper.builders

import com.adobe.cq.sightly.WCMBindings
import com.day.cq.commons.inherit.HierarchyNodeInheritanceValueMap
import com.day.cq.wcm.api.PageManager
import com.day.cq.wcm.api.WCMMode
import com.icfolson.aem.prosper.mocks.ProsperMockSlingHttpServletRequest
import org.apache.sling.api.SlingHttpServletRequest
import org.apache.sling.api.resource.ResourceResolver
import org.apache.sling.api.resource.ValueMap
import org.apache.sling.api.scripting.SlingBindings
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest
import org.osgi.framework.BundleContext

/**
 * Builder to assist in creating <code>SlingHttpServletRequest</code> objects.
 */
class RequestBuilder {

    private final ResourceResolver resourceResolver

    private final BundleContext bundleContext

    /**
     * Create a request builder for a test spec.
     *
     * @param resourceResolver Sling resource resolver
     */
    RequestBuilder(ResourceResolver resourceResolver, BundleContext bundleContext) {
        this.resourceResolver = resourceResolver
        this.bundleContext = bundleContext
    }

    /**
     * Build a Sling request with default values.
     *
     * @return request
     */
    MockSlingHttpServletRequest build() {
        build(null)
    }

    /**
     * Build a Sling request using a closure to set request properties.  The closure delegates to an instance of
     * <a href="http://static.javadoc.io/org.apache.sling/org.apache.sling.testing.sling-mock/2.2.20/org/apache/sling/testing/mock/sling/servlet/MockSlingHttpServletRequest
     * .html">MockSlingHttpServletRequest</a>,
     * so methods for this instances may be called directly in the closure (see example below).
     *
     * <pre>
     *  new RequestBuilder(resourceResolver).build {*      serverName = "localhost"
     *      path = "/content"
     *      method = "GET"
     *      parameterMap = ["a": ["1", "2"], "b": ["1"]]
     *      extension = "html"
     *}</pre>
     *
     * @param closure closure that delegates to <a href="http://static.javadoc.io/org.apache.sling/org.apache.sling.testing.sling-mock/2.2
     * .20/org/apache/sling/testing/mock/sling/servlet/MockSlingHttpServletRequest.html">MockSlingHttpServletRequest</a>
     * @return mock request
     */
    MockSlingHttpServletRequest build(@DelegatesTo(ProsperMockSlingHttpServletRequest) Closure closure) {
        def request = new ProsperMockSlingHttpServletRequest(resourceResolver, bundleContext)

        if (closure) {
            closure.delegate = request
            closure.resolveStrategy = Closure.DELEGATE_ONLY
            closure()
        }

        if (request.resource) {
            request.setAttribute(SlingBindings.name, getSlingBindings(request))
        }

        request
    }

    private SlingBindings getSlingBindings(SlingHttpServletRequest request) {
        def bindings = new SlingBindings()

        bindings.put(WCMBindings.WCM_MODE, WCMMode.fromRequest(request))
        bindings.put(WCMBindings.PROPERTIES, request.resource.valueMap)

        def pageManager = resourceResolver.adaptTo(PageManager)
        def currentPage = pageManager.getContainingPage(request.resource)

        bindings.put(WCMBindings.PAGE_MANAGER, pageManager)
        bindings.put(WCMBindings.CURRENT_PAGE, currentPage)

        if (currentPage) {
            bindings.put(WCMBindings.PAGE_PROPERTIES, currentPage.properties)
            bindings.put(WCMBindings.INHERITED_PAGE_PROPERTIES, new HierarchyNodeInheritanceValueMap(currentPage.contentResource))
        } else {
            bindings.put(WCMBindings.PAGE_PROPERTIES, ValueMap.EMPTY)
            bindings.put(WCMBindings.INHERITED_PAGE_PROPERTIES, ValueMap.EMPTY)
        }

        bindings
    }
}
