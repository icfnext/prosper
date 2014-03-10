package com.citytechinc.aem.prosper.mocks

import com.citytechinc.aem.prosper.mocks.request.MockRequestParameterMap
import com.google.common.collect.SetMultimap
import org.apache.sling.api.SlingHttpServletRequest
import org.apache.sling.api.request.RequestDispatcherOptions
import org.apache.sling.api.request.RequestParameter
import org.apache.sling.api.request.RequestParameterMap
import org.apache.sling.api.request.RequestPathInfo
import org.apache.sling.api.request.RequestProgressTracker
import org.apache.sling.api.resource.Resource
import org.apache.sling.api.resource.ResourceResolver
import org.apache.sling.commons.testing.sling.MockRequestPathInfo

import javax.servlet.RequestDispatcher
import javax.servlet.ServletInputStream
import javax.servlet.http.Cookie
import javax.servlet.http.HttpSession
import java.security.Principal

class MockSlingHttpServletRequest implements SlingHttpServletRequest {

    private final def resourceResolver

    private final def resource

    private final def requestPathInfo

    private final def queryString

    private final def requestParameterMap

    private final def attributes

    private final def method

    MockSlingHttpServletRequest(ResourceResolver resourceResolver, String path, String method, String selectorString,
        String extension, String suffix, String queryString, SetMultimap<String, String> parameters,
        Map<String, Object> attributes) {
        this.method = method
        this.resourceResolver = resourceResolver
        this.queryString = queryString
        this.attributes = attributes

        resource = resourceResolver.resolve(path)

        requestParameterMap = MockRequestParameterMap.create(parameters)
        requestPathInfo = new MockRequestPathInfo(selectorString, extension, suffix, path)
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
        throw new UnsupportedOperationException()
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
    String getAuthType() {
        throw new UnsupportedOperationException()
    }

    @Override
    Cookie[] getCookies() {
        throw new UnsupportedOperationException()
    }

    @Override
    long getDateHeader(String s) {
        throw new UnsupportedOperationException()
    }

    @Override
    String getHeader(String s) {
        throw new UnsupportedOperationException()
    }

    @Override
    Enumeration getHeaders(String s) {
        throw new UnsupportedOperationException()
    }

    @Override
    Enumeration getHeaderNames() {
        throw new UnsupportedOperationException()
    }

    @Override
    int getIntHeader(String s) {
        throw new UnsupportedOperationException()
    }

    @Override
    String getMethod() {
        method
    }

    @Override
    String getPathInfo() {
        throw new UnsupportedOperationException()
    }

    @Override
    String getPathTranslated() {
        throw new UnsupportedOperationException()
    }

    @Override
    String getContextPath() {
        throw new UnsupportedOperationException()
    }

    @Override
    String getQueryString() {
        queryString
    }

    @Override
    String getRemoteUser() {
        throw new UnsupportedOperationException()
    }

    @Override
    boolean isUserInRole(String s) {
        throw new UnsupportedOperationException()
    }

    @Override
    Principal getUserPrincipal() {
        throw new UnsupportedOperationException()
    }

    @Override
    String getRequestedSessionId() {
        throw new UnsupportedOperationException()
    }

    @Override
    String getRequestURI() {
        throw new UnsupportedOperationException()
    }

    @Override
    StringBuffer getRequestURL() {
        throw new UnsupportedOperationException()
    }

    @Override
    String getServletPath() {
        throw new UnsupportedOperationException()
    }

    @Override
    HttpSession getSession(boolean b) {
        throw new UnsupportedOperationException()
    }

    @Override
    HttpSession getSession() {
        throw new UnsupportedOperationException()
    }

    @Override
    boolean isRequestedSessionIdValid() {
        throw new UnsupportedOperationException()
    }

    @Override
    boolean isRequestedSessionIdFromCookie() {
        throw new UnsupportedOperationException()
    }

    @Override
    boolean isRequestedSessionIdFromURL() {
        throw new UnsupportedOperationException()
    }

    @Override
    boolean isRequestedSessionIdFromUrl() {
        throw new UnsupportedOperationException()
    }

    @Override
    Object getAttribute(String s) {
        attributes[s]
    }

    @Override
    Enumeration getAttributeNames() {
        throw new UnsupportedOperationException()
    }

    @Override
    String getCharacterEncoding() {
        throw new UnsupportedOperationException()
    }

    @Override
    void setCharacterEncoding(String s) throws UnsupportedEncodingException {
        throw new UnsupportedOperationException()
    }

    @Override
    int getContentLength() {
        throw new UnsupportedOperationException()
    }

    @Override
    String getContentType() {
        throw new UnsupportedOperationException()
    }

    @Override
    ServletInputStream getInputStream() throws IOException {
        throw new UnsupportedOperationException()
    }

    @Override
    String getParameter(String s) {
        def value = requestParameterMap.getValue(s)

        value ? value.string : null
    }

    @Override
    Enumeration getParameterNames() {
        throw new UnsupportedOperationException()
    }

    @Override
    String[] getParameterValues(String s) {
        def values = requestParameterMap.getValues(s)

        values ? values*.string.toArray(new String[0]) : null
    }

    @Override
    Map getParameterMap() {
        requestParameterMap.collectEntries([:]) { name, values ->
            [name: values*.string.toArray(new String[0])]
        }
    }

    @Override
    String getProtocol() {
        throw new UnsupportedOperationException()
    }

    @Override
    String getScheme() {
        throw new UnsupportedOperationException()
    }

    @Override
    String getServerName() {
        throw new UnsupportedOperationException()
    }

    @Override
    int getServerPort() {
        throw new UnsupportedOperationException()
    }

    @Override
    BufferedReader getReader() throws IOException {
        throw new UnsupportedOperationException()
    }

    @Override
    String getRemoteAddr() {
        throw new UnsupportedOperationException()
    }

    @Override
    String getRemoteHost() {
        throw new UnsupportedOperationException()
    }

    @Override
    void setAttribute(String s, Object o) {
        attributes[s] = o
    }

    @Override
    void removeAttribute(String s) {
        attributes.remove(s)
    }

    @Override
    Locale getLocale() {
        throw new UnsupportedOperationException()
    }

    @Override
    Enumeration getLocales() {
        throw new UnsupportedOperationException()
    }

    @Override
    boolean isSecure() {
        throw new UnsupportedOperationException()
    }

    @Override
    RequestDispatcher getRequestDispatcher(String s) {
        throw new UnsupportedOperationException()
    }

    @Override
    String getRealPath(String s) {
        throw new UnsupportedOperationException()
    }

    @Override
    int getRemotePort() {
        throw new UnsupportedOperationException()
    }

    @Override
    String getLocalName() {
        throw new UnsupportedOperationException()
    }

    @Override
    String getLocalAddr() {
        throw new UnsupportedOperationException()
    }

    @Override
    int getLocalPort() {
        throw new UnsupportedOperationException()
    }
}
