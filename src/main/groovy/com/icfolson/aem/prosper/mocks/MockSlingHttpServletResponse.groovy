package com.icfolson.aem.prosper.mocks

import com.google.common.base.Objects
import org.apache.sling.api.SlingHttpServletResponse
import org.apache.sling.api.adapter.SlingAdaptable
import org.springframework.mock.web.MockHttpServletResponse

/**
 * Mock Sling response that delegates to a Spring <code>MockHttpServletResponse</code>.  This class should not be used
 * directly; rather, use a <code>ResponseBuilder</code> instance from test specs to instantiate mock requests.
 */
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
