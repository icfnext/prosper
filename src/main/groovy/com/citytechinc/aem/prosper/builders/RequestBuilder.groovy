package com.citytechinc.aem.prosper.builders

import com.citytechinc.aem.prosper.mocks.MockSlingHttpServletRequest
import com.citytechinc.aem.prosper.specs.ProsperSpec
import org.apache.sling.api.adapter.AdapterManager
import org.apache.sling.api.resource.ResourceResolver
import org.springframework.mock.web.MockHttpServletRequest

import javax.servlet.http.Cookie
import javax.servlet.http.HttpSession
import java.security.Principal

/**
 * Builder to assist in creating <code>SlingHttpServletRequest</code> objects.
 */
class RequestBuilder {

    private final MockHttpServletRequest mockRequest = new MockHttpServletRequest()

    private final ResourceResolver resourceResolver

    private final AdapterManager adapterManager

    private final List<String> selectors = []

    private String path = "/"

    private String suffix = ""

    private String extension = ""

    /**
     * Create a request builder for a test spec.
     *
     * @param resourceResolver Sling resource resolver
     * @param adapterManager adapter manager for the current specification
     */
    RequestBuilder(ProsperSpec spec) {
        resourceResolver = spec.resourceResolver
        adapterManager = spec.adapterManager
    }

    /**
     * Set the request path.
     *
     * @param path JCR path
     */
    void setPath(String path) {
        this.path = path
    }

    /**
     * Set the request suffix.
     *
     * @param suffix suffix
     */
    void setSuffix(String suffix) {
        this.suffix = suffix
    }

    /**
     * Set the request extension
     *
     * @param extension extension
     */
    void setExtension(String extension) {
        this.extension = extension
    }

    /**
     * Add selectors to the request.
     *
     * @param selectors list of selectors
     */
    void setSelectors(List<String> selectors) {
        this.selectors.addAll(selectors)
    }

    /**
     * Add request parameters from a map.  Map values should either be strings or lists of strings (for multivalued
     * parameters).
     *
     * @param parameters map of parameter names and values
     */
    void setParameters(Map<String, Object> parameters) {
        parameters.each { name, value ->
            if (value instanceof Collection) {
                mockRequest.setParameter(name, value as String[])
            } else {
                mockRequest.setParameter(name, value as String)
            }
        }
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
     * Build a Sling request using a closure to set request properties.  The closure delegates to this builder and an
     * instance of <a href="http://docs.spring.io/spring/docs/3.2.8
     * .RELEASE/javadoc-api/org/springframework/mock/web/MockHttpServletRequest.html">MockHttpServletRequest</a>,
     * so methods for these instances may be called directly in the closure (see example below).  This pattern is
     * similar to the Groovy <a href="http://groovy.codehaus.org/groovy-jdk/java/lang/Object.html#with(groovy.lang
     * .Closure)"><code>with</code></a> method.
     *
     * <pre>
     *  new RequestBuilder(resourceResolver).build {*      serverName = "localhost"
     *      path = "/content"
     *      method = "GET"
     *      parameters = ["a": ["1", "2"], "b": ["1"]]
     *      extension = "html"
     *}</pre>
     *
     * @param closure closure that delegates to this builder and <a href="http://docs.spring.io/spring/docs/3.2.8
     * .RELEASE/javadoc-api/org/springframework/mock/web/MockHttpServletRequest.html">MockHttpServletRequest</a>
     * @return request
     */
    MockSlingHttpServletRequest build(Closure closure) {
        if (closure) {
            closure.delegate = this
            closure.resolveStrategy = Closure.DELEGATE_ONLY
            closure()
        }

        new MockSlingHttpServletRequest(mockRequest, resourceResolver, path, selectors, extension, suffix,
            adapterManager)
    }

    // delegate methods

    void setCharacterEncoding(String characterEncoding) {
        mockRequest.setCharacterEncoding(characterEncoding)
    }

    void setContent(byte[] content) {
        mockRequest.setContent(content)
    }

    void setContentType(String contentType) {
        mockRequest.setContentType(contentType)
    }

    void setParameter(String name, String value) {
        mockRequest.setParameter(name, value)
    }

    void setParameter(String name, String[] values) {
        mockRequest.setParameter(name, values)
    }

    void addParameter(String name, String value) {
        mockRequest.addParameter(name, value)
    }

    void addParameter(String name, String[] values) {
        mockRequest.addParameter(name, values)
    }

    void addParameters(Map params) {
        mockRequest.addParameters(params)
    }

    void removeParameter(String name) {
        mockRequest.removeParameter(name)
    }

    void removeAllParameters() {
        mockRequest.removeAllParameters()
    }

    void setProtocol(String protocol) {
        mockRequest.setProtocol(protocol)
    }

    void setScheme(String scheme) {
        mockRequest.setScheme(scheme)
    }

    void setServerName(String serverName) {
        mockRequest.setServerName(serverName)
    }

    void setServerPort(int serverPort) {
        mockRequest.setServerPort(serverPort)
    }

    void setRemoteAddr(String remoteAddr) {
        mockRequest.setRemoteAddr(remoteAddr)
    }

    void setRemoteHost(String remoteHost) {
        mockRequest.setRemoteHost(remoteHost)
    }

    void setAttribute(String name, Object value) {
        mockRequest.setAttribute(name, value)
    }

    void removeAttribute(String name) {
        mockRequest.removeAttribute(name)
    }

    void clearAttributes() {
        mockRequest.clearAttributes()
    }

    void addPreferredLocale(Locale locale) {
        mockRequest.addPreferredLocale(locale)
    }

    void setPreferredLocales(List<Locale> locales) {
        mockRequest.setPreferredLocales(locales)
    }

    void setSecure(boolean secure) {
        mockRequest.setSecure(secure)
    }

    void setRemotePort(int remotePort) {
        mockRequest.setRemotePort(remotePort)
    }

    void setLocalName(String localName) {
        mockRequest.setLocalName(localName)
    }

    void setLocalAddr(String localAddr) {
        mockRequest.setLocalAddr(localAddr)
    }

    void setLocalPort(int localPort) {
        mockRequest.setLocalPort(localPort)
    }

    void setAuthType(String authType) {
        mockRequest.setAuthType(authType)
    }

    void setCookies(Cookie... cookies) {
        mockRequest.setCookies(cookies)
    }

    void addHeader(String name, Object value) {
        mockRequest.addHeader(name, value)
    }

    void setMethod(String method) {
        mockRequest.setMethod(method)
    }

    void setPathInfo(String pathInfo) {
        mockRequest.setPathInfo(pathInfo)
    }

    void setContextPath(String contextPath) {
        mockRequest.setContextPath(contextPath)
    }

    void setQueryString(String queryString) {
        mockRequest.setQueryString(queryString)
    }

    void setRemoteUser(String remoteUser) {
        mockRequest.setRemoteAddr(remoteUser)
    }

    void addUserRole(String role) {
        mockRequest.addUserRole(role)
    }

    void setUserPrincipal(Principal userPrincipal) {
        mockRequest.setUserPrincipal(userPrincipal)
    }

    void setRequestedSessionId(String requestedSessionId) {
        mockRequest.setRequestedSessionId(requestedSessionId)
    }

    void setRequestURI(String requestURI) {
        mockRequest.setRequestURI(requestURI)
    }

    void setServletPath(String servletPath) {
        mockRequest.setServletPath(servletPath)
    }

    void setSession(HttpSession session) {
        mockRequest.setSession(session)
    }

    void setRequestedSessionIdValid(boolean requestedSessionIdValid) {
        mockRequest.setRequestedSessionIdValid(requestedSessionIdValid)
    }

    void setRequestedSessionIdFromCookie(boolean requestedSessionIdFromCookie) {
        mockRequest.setRequestedSessionIdFromCookie(requestedSessionIdFromCookie)
    }

    void setRequestedSessionIdFromURL(boolean requestedSessionIdFromURL) {
        mockRequest.setRequestedSessionIdFromURL(requestedSessionIdFromURL)
    }
}
