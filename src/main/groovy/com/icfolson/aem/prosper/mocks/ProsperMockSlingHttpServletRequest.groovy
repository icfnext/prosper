package com.icfolson.aem.prosper.mocks

import org.apache.sling.api.resource.ResourceResolver
import org.apache.sling.testing.mock.sling.servlet.MockRequestPathInfo
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest
import org.osgi.framework.BundleContext

/**
 * Extension of Sling mock with additional convenience methods for setting request path info.
 */
class ProsperMockSlingHttpServletRequest extends MockSlingHttpServletRequest {

    ProsperMockSlingHttpServletRequest(ResourceResolver resourceResolver, BundleContext bundleContext) {
        super(resourceResolver, bundleContext)

        resource = resourceResolver.resolve("/")
    }

    /**
     * Set the resource path.
     *
     * @param path JCR resource path
     */
    void setPath(String path) {
        resource = resourceResolver.resolve(path)
        (requestPathInfo as MockRequestPathInfo).resourcePath = path
    }

    /**
     * Set the request suffix.
     *
     * @param suffix suffix
     */
    void setSuffix(String suffix) {
        (requestPathInfo as MockRequestPathInfo).suffix = suffix
    }

    /**
     * Set the request extension
     *
     * @param extension extension
     */
    void setExtension(String extension) {
        (requestPathInfo as MockRequestPathInfo).extension = extension
    }

    /**
     * Add selectors to the request.
     *
     * @param selectors list of selectors
     */
    void setSelectors(List<String> selectors) {
        (requestPathInfo as MockRequestPathInfo).selectorString = selectors.join(".")
    }

    /**
     * Set the request parameter map.  Values can either be single-valued Strings, arrays, or collections.
     *
     * @param parameters parameter map
     */
    @Override
    void setParameterMap(Map<String, Object> parameters) {
        def parameterMap = parameters.collectEntries { name, value ->
            [name, value instanceof Collection ? value as String[] : value]
        }

        super.setParameterMap(parameterMap)
    }
}
