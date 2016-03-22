package com.citytechinc.aem.prosper.mocks

import com.google.common.base.Objects
import org.apache.sling.api.SlingHttpServletResponse
import org.apache.sling.api.adapter.SlingAdaptable
import org.springframework.mock.web.MockHttpServletResponse

class MockSlingHttpServletResponse extends SlingAdaptable implements SlingHttpServletResponse {

    @Delegate
    private final MockHttpServletResponse mockResponse

    MockSlingHttpServletResponse(MockHttpServletResponse mockResponse) {
        this.mockResponse = mockResponse
    }

    @Override
    String toString() {
        Objects.toStringHelper(this).add("contentType", mockResponse.contentType).add("characterEncoding",
            mockResponse.characterEncoding).add("contentAsString", mockResponse.contentAsString).toString()
    }
}
