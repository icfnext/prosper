package com.citytechinc.aem.prosper.mocks

import org.apache.sling.api.SlingHttpServletResponse

import javax.servlet.ServletOutputStream
import javax.servlet.http.Cookie

class MockSlingHttpServletResponse implements SlingHttpServletResponse {

    private final Writer writer

    private int status

    private String contentType

    private String encoding

    MockSlingHttpServletResponse(Writer writer, int status, String contentType, String encoding) {
        this.writer = writer
        this.status = status
        this.contentType = contentType
        this.encoding = encoding
    }

    @Override
    def <AdapterType> AdapterType adaptTo(Class<AdapterType> type) {
        throw new UnsupportedOperationException()
    }

    @Override
    void addCookie(Cookie cookie) {
        throw new UnsupportedOperationException()
    }

    @Override
    boolean containsHeader(String s) {
        throw new UnsupportedOperationException()
    }

    @Override
    String encodeURL(String s) {
        throw new UnsupportedOperationException()
    }

    @Override
    String encodeRedirectURL(String s) {
        throw new UnsupportedOperationException()
    }

    @Override
    String encodeUrl(String s) {
        throw new UnsupportedOperationException()
    }

    @Override
    String encodeRedirectUrl(String s) {
        throw new UnsupportedOperationException()
    }

    @Override
    void sendError(int i, String s) throws IOException {
        throw new UnsupportedOperationException()
    }

    @Override
    void sendError(int i) throws IOException {
        throw new UnsupportedOperationException()
    }

    @Override
    void sendRedirect(String s) throws IOException {
        throw new UnsupportedOperationException()
    }

    @Override
    void setDateHeader(String s, long l) {
        throw new UnsupportedOperationException()
    }

    @Override
    void addDateHeader(String s, long l) {
        throw new UnsupportedOperationException()
    }

    @Override
    void setHeader(String s, String s1) {
        throw new UnsupportedOperationException()
    }

    @Override
    void addHeader(String s, String s1) {
        throw new UnsupportedOperationException()
    }

    @Override
    void setIntHeader(String s, int i) {
        throw new UnsupportedOperationException()
    }

    @Override
    void addIntHeader(String s, int i) {
        throw new UnsupportedOperationException()
    }

    @Override
    void setStatus(int i) {
        status = i
    }

    @Override
    void setStatus(int i, String s) {
        status = i
    }

    @Override
    String getCharacterEncoding() {
        encoding
    }

    @Override
    String getContentType() {
        contentType
    }

    @Override
    ServletOutputStream getOutputStream() throws IOException {
        throw new UnsupportedOperationException()
    }

    @Override
    PrintWriter getWriter() throws IOException {
        new PrintWriter(writer)
    }

    @Override
    void setCharacterEncoding(String s) {
        encoding = s
    }

    @Override
    void setContentLength(int i) {
        throw new UnsupportedOperationException()
    }

    @Override
    void setContentType(String s) {
        contentType = s
    }

    @Override
    void setBufferSize(int i) {
        throw new UnsupportedOperationException()
    }

    @Override
    int getBufferSize() {
        throw new UnsupportedOperationException()
    }

    @Override
    void flushBuffer() throws IOException {

    }

    @Override
    void resetBuffer() {

    }

    @Override
    boolean isCommitted() {
        throw new UnsupportedOperationException()
    }

    @Override
    void reset() {
        throw new UnsupportedOperationException()
    }

    @Override
    void setLocale(Locale locale) {
        throw new UnsupportedOperationException()
    }

    @Override
    Locale getLocale() {
        throw new UnsupportedOperationException()
    }
}
