package com.citytechinc.aem.prosper.mocks.request

import org.apache.sling.api.request.RequestParameter

class MockRequestParameter implements RequestParameter {

    private final def value

    MockRequestParameter(String value) {
        this.value = value
    }

    @Override
    boolean isFormField() {
        throw new UnsupportedOperationException()
    }

    @Override
    String getContentType() {
        throw new UnsupportedOperationException()
    }

    @Override
    long getSize() {
        throw new UnsupportedOperationException()
    }

    @Override
    byte[] get() {
        throw new UnsupportedOperationException()
    }

    @Override
    InputStream getInputStream() throws IOException {
        throw new UnsupportedOperationException()
    }

    @Override
    String getFileName() {
        throw new UnsupportedOperationException()
    }

    @Override
    String getString() {
        value
    }

    @Override
    String getString(String encoding) throws UnsupportedEncodingException {
        value
    }
}
