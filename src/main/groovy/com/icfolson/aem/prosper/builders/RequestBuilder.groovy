package com.icfolson.aem.prosper.builders

import com.icfolson.aem.prosper.mocks.MockSlingHttpServletRequest
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

    private final List<String> selectors = []

    private String path = "/"

    private String suffix = ""

    private String extension = ""

    /**
     * Create a request builder for a test spec.
     *
     * @param resourceResolver Sling resource resolver
     */
    RequestBuilder(ResourceResolver resourceResolver) {
        this.resourceResolver = resourceResolver
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
    MockSlingHttpServletRequest build(@DelegatesTo(RequestBuilder) Closure closure) {
        if (closure) {
            closure.delegate = this
            closure.resolveStrategy = Closure.DELEGATE_ONLY
            closure()
        }

        new MockSlingHttpServletRequest(mockRequest, resourceResolver, path, selectors, extension, suffix)
    }

    /**
     * Set the request path.
     *
     * @param path JCR path
     */
    RequestBuilder setPath(String path) {
        this.path = path
        this
    }

    /**
     * Set the request suffix.
     *
     * @param suffix suffix
     */
    RequestBuilder setSuffix(String suffix) {
        this.suffix = suffix
        this
    }

    /**
     * Set the request extension
     *
     * @param extension extension
     */
    RequestBuilder setExtension(String extension) {
        this.extension = extension
        this
    }

    /**
     * Add selectors to the request.
     *
     * @param selectors list of selectors
     */
    RequestBuilder setSelectors(List<String> selectors) {
        this.selectors.addAll(selectors)
        this
    }

    /**
     * Add request parameters from a map.  Map values should either be strings or lists of strings (for multivalued
     * parameters).
     *
     * @param parameters map of parameter names and values
     */
    RequestBuilder setParameters(Map<String, Object> parameters) {
        parameters.each { name, value ->
            if (value instanceof Collection) {
                mockRequest.setParameter(name, value as String[])
            } else {
                mockRequest.setParameter(name, value as String)
            }
        }

        this
    }

    // delegate methods

    RequestBuilder setCharacterEncoding(String characterEncoding) {
        mockRequest.setCharacterEncoding(characterEncoding)
        this
    }

    RequestBuilder setContent(byte[] content) {
        mockRequest.setContent(content)
        this
    }

    RequestBuilder setContentType(String contentType) {
        mockRequest.setContentType(contentType)
        this
    }

    RequestBuilder setParameter(String name, String value) {
        mockRequest.setParameter(name, value)
        this
    }

    RequestBuilder setParameter(String name, String[] values) {
        mockRequest.setParameter(name, values)
        this
    }

    RequestBuilder addParameter(String name, String value) {
        mockRequest.addParameter(name, value)
        this
    }

    RequestBuilder addParameter(String name, String[] values) {
        mockRequest.addParameter(name, values)
        this
    }

    RequestBuilder addParameters(Map params) {
        mockRequest.addParameters(params)
        this
    }

    RequestBuilder removeParameter(String name) {
        mockRequest.removeParameter(name)
        this
    }

    RequestBuilder removeAllParameters() {
        mockRequest.removeAllParameters()
        this
    }

    RequestBuilder setProtocol(String protocol) {
        mockRequest.setProtocol(protocol)
        this
    }

    RequestBuilder setScheme(String scheme) {
        mockRequest.setScheme(scheme)
        this
    }

    RequestBuilder setServerName(String serverName) {
        mockRequest.setServerName(serverName)
        this
    }

    RequestBuilder setServerPort(int serverPort) {
        mockRequest.setServerPort(serverPort)
        this
    }

    RequestBuilder setRemoteAddr(String remoteAddr) {
        mockRequest.setRemoteAddr(remoteAddr)
        this
    }

    RequestBuilder setRemoteHost(String remoteHost) {
        mockRequest.setRemoteHost(remoteHost)
        this
    }

    RequestBuilder setAttribute(String name, Object value) {
        mockRequest.setAttribute(name, value)
        this
    }

    RequestBuilder removeAttribute(String name) {
        mockRequest.removeAttribute(name)
        this
    }

    RequestBuilder clearAttributes() {
        mockRequest.clearAttributes()
        this
    }

    RequestBuilder addPreferredLocale(Locale locale) {
        mockRequest.addPreferredLocale(locale)
        this
    }

    RequestBuilder setPreferredLocales(List<Locale> locales) {
        mockRequest.setPreferredLocales(locales)
        this
    }

    RequestBuilder setSecure(boolean secure) {
        mockRequest.setSecure(secure)
        this
    }

    RequestBuilder setRemotePort(int remotePort) {
        mockRequest.setRemotePort(remotePort)
        this
    }

    RequestBuilder setLocalName(String localName) {
        mockRequest.setLocalName(localName)
        this
    }

    RequestBuilder setLocalAddr(String localAddr) {
        mockRequest.setLocalAddr(localAddr)
        this
    }

    RequestBuilder setLocalPort(int localPort) {
        mockRequest.setLocalPort(localPort)
        this
    }

    RequestBuilder setAuthType(String authType) {
        mockRequest.setAuthType(authType)
        this
    }

    RequestBuilder setCookies(Cookie... cookies) {
        mockRequest.setCookies(cookies)
        this
    }

    RequestBuilder addHeader(String name, Object value) {
        mockRequest.addHeader(name, value)
        this
    }

    RequestBuilder setMethod(String method) {
        mockRequest.setMethod(method)
        this
    }

    RequestBuilder setPathInfo(String pathInfo) {
        mockRequest.setPathInfo(pathInfo)
        this
    }

    RequestBuilder setContextPath(String contextPath) {
        mockRequest.setContextPath(contextPath)
        this
    }

    RequestBuilder setQueryString(String queryString) {
        mockRequest.setQueryString(queryString)
        this
    }

    RequestBuilder setRemoteUser(String remoteUser) {
        mockRequest.setRemoteAddr(remoteUser)
        this
    }

    RequestBuilder addUserRole(String role) {
        mockRequest.addUserRole(role)
        this
    }

    RequestBuilder setUserPrincipal(Principal userPrincipal) {
        mockRequest.setUserPrincipal(userPrincipal)
        this
    }

    RequestBuilder setRequestedSessionId(String requestedSessionId) {
        mockRequest.setRequestedSessionId(requestedSessionId)
        this
    }

    RequestBuilder setRequestURI(String requestURI) {
        mockRequest.setRequestURI(requestURI)
        this
    }

    RequestBuilder setServletPath(String servletPath) {
        mockRequest.setServletPath(servletPath)
        this
    }

    RequestBuilder setSession(HttpSession session) {
        mockRequest.setSession(session)
        this
    }

    RequestBuilder setRequestedSessionIdValid(boolean requestedSessionIdValid) {
        mockRequest.setRequestedSessionIdValid(requestedSessionIdValid)
        this
    }

    RequestBuilder setRequestedSessionIdFromCookie(boolean requestedSessionIdFromCookie) {
        mockRequest.setRequestedSessionIdFromCookie(requestedSessionIdFromCookie)
        this
    }

    RequestBuilder setRequestedSessionIdFromURL(boolean requestedSessionIdFromURL) {
        mockRequest.setRequestedSessionIdFromURL(requestedSessionIdFromURL)
        this
    }
}
