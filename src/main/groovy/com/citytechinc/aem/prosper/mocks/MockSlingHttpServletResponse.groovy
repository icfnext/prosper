package com.citytechinc.aem.prosper.mocks

import org.apache.sling.api.SlingHttpServletResponse
import org.springframework.mock.web.MockHttpServletResponse

class MockSlingHttpServletResponse implements SlingHttpServletResponse {

    @Delegate
    private MockHttpServletResponse mockResponse

    MockSlingHttpServletResponse(MockHttpServletResponse mockResponse) {
        this.mockResponse = mockResponse
    }

    @Override
    def <AdapterType> AdapterType adaptTo(Class<AdapterType> type) {
        throw new UnsupportedOperationException()
    }
}
