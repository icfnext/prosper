package com.icfolson.aem.prosper.mocks

import org.apache.sling.api.resource.ResourceResolver
import org.apache.sling.testing.mock.sling.servlet.MockRequestPathInfo
import org.osgi.framework.BundleContext

class ProsperMockSlingHttpServletRequest extends org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest {

    ProsperMockSlingHttpServletRequest(ResourceResolver resourceResolver, BundleContext bundleContext) {
        super(resourceResolver, bundleContext)

        resource = resourceResolver.resolve("/")
    }

    /**
     * Set the request path.
     *
     * @param path JCR path
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

    void setParameters(Map<String, Object> parameters) {
        setParameterMap(parameters.collectEntries { name, value ->
            [name, value instanceof Collection ? value as String[] : value]
        })
    }
}
