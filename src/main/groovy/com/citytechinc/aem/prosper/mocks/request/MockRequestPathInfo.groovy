package com.citytechinc.aem.prosper.mocks.request

import org.apache.sling.api.request.RequestPathInfo

class MockRequestPathInfo implements RequestPathInfo {

    String path

    String extension

    String suffix

    List<String> selectors

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
        selectors ? selectors.join(".") : null
    }

    @Override
    String[] getSelectors() {
        selectors as String[]
    }

    @Override
    String getSuffix() {
        suffix
    }
}
