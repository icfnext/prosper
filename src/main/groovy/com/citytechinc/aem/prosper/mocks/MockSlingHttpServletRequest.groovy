package com.citytechinc.aem.prosper.mocks

import com.citytechinc.aem.prosper.mocks.request.MockRequestParameterMap
import org.apache.sling.api.SlingHttpServletRequest
import org.apache.sling.api.request.RequestDispatcherOptions
import org.apache.sling.api.request.RequestParameter
import org.apache.sling.api.request.RequestParameterMap
import org.apache.sling.api.request.RequestPathInfo
import org.apache.sling.api.request.RequestProgressTracker
import org.apache.sling.api.resource.Resource
import org.apache.sling.api.resource.ResourceResolver
import org.springframework.mock.web.MockHttpServletRequest

import javax.servlet.RequestDispatcher
import javax.servlet.http.Cookie

class MockSlingHttpServletRequest implements SlingHttpServletRequest {

    class MockRequestPathInfo implements RequestPathInfo {

        String path

        String extension

        String suffix

        String selectors

        @Override
        String getResourcePath() {
            path
        }

        @Override
        String getExtension() {
            extension
        }

        @Override
        String getSelectorString() {
            selectors
        }

        @Override
        String[] getSelectors() {
            selectors ? selectors.split("\\.") : new String[0]
        }

        @Override
        String getSuffix() {
            suffix
        }
    }

    @Delegate
    private MockHttpServletRequest mockRequest

    private final def resourceResolver

    private final def resource

    private final def requestPathInfo

    private final def requestParameterMap

    MockSlingHttpServletRequest(MockHttpServletRequest mockRequest, ResourceResolver resourceResolver, String path,
        String selectorString, String extension, String suffix) {
        this.mockRequest = mockRequest
        this.resourceResolver = resourceResolver

        resource = resourceResolver.resolve(path)

        requestParameterMap = MockRequestParameterMap.create(mockRequest)
        requestPathInfo = new MockRequestPathInfo(path: path, extension: extension, suffix: suffix,
            selectors: selectorString)
    }

    @Override
    Resource getResource() {
        resource
    }

    @Override
    ResourceResolver getResourceResolver() {
        resourceResolver
    }

    @Override
    RequestPathInfo getRequestPathInfo() {
        requestPathInfo
    }

    @Override
    RequestParameter getRequestParameter(String name) {
        requestParameterMap.getValue(name)
    }

    @Override
    RequestParameter[] getRequestParameters(String name) {
        requestParameterMap.getValues(name)
    }

    @Override
    RequestParameterMap getRequestParameterMap() {
        requestParameterMap
    }

    @Override
    RequestDispatcher getRequestDispatcher(String path, RequestDispatcherOptions options) {
        throw new UnsupportedOperationException()
    }

    @Override
    RequestDispatcher getRequestDispatcher(Resource resource, RequestDispatcherOptions options) {
        throw new UnsupportedOperationException()
    }

    @Override
    RequestDispatcher getRequestDispatcher(Resource resource) {
        throw new UnsupportedOperationException()
    }

    @Override
    Cookie getCookie(String name) {
        getCookies().find { it.name == name }
    }

    @Override
    String getResponseContentType() {
        throw new UnsupportedOperationException()
    }

    @Override
    Enumeration<String> getResponseContentTypes() {
        throw new UnsupportedOperationException()
    }

    @Override
    ResourceBundle getResourceBundle(Locale locale) {
        throw new UnsupportedOperationException()
    }

    @Override
    ResourceBundle getResourceBundle(String baseName, Locale locale) {
        throw new UnsupportedOperationException()
    }

    @Override
    RequestProgressTracker getRequestProgressTracker() {
        throw new UnsupportedOperationException()
    }

    @Override
    def <AdapterType> AdapterType adaptTo(Class<AdapterType> type) {
        throw new UnsupportedOperationException()
    }

    @Override
    String getQueryString() {
        // check for overridden query string
        def queryString = mockRequest.queryString

        if (!queryString) {
            def builder = new StringBuilder()
            def map = mockRequest.getParameterMap()

            if (map) {
                map.each { name, values ->
                    values.each { value ->
                        builder.append(name)
                        builder.append('=')
                        builder.append(value)
                        builder.append('&')
                    }
                }

                builder.deleteCharAt(builder.length() - 1)
            }

            queryString = builder.toString()
        }

        queryString
    }
}
