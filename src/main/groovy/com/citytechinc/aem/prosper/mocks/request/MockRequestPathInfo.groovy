package com.citytechinc.aem.prosper.mocks.request

import com.google.common.base.Objects
import org.apache.sling.api.request.RequestPathInfo
import org.apache.sling.api.resource.Resource
import org.apache.sling.api.resource.ResourceResolver

class MockRequestPathInfo implements RequestPathInfo {

    private final ResourceResolver resourceResolver

    private final String path

    private final List<String> selectors

    private final String extension

    private final String suffix

    MockRequestPathInfo(resourceResolver, path, selectors, extension, suffix) {
        this.resourceResolver = resourceResolver
        this.path = path
        this.selectors = selectors
        this.extension = extension
        this.suffix = suffix
    }

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

    @Override
    Resource getSuffixResource() {
        def suffixResource = null

        if (suffix) {
            suffixResource = resourceResolver.getResource(suffix)
        }

        suffixResource
    }

    @Override
    String toString() {
        Objects.toStringHelper(this).add("resourcePath", path).add("selectors", selectors).add("extension",
            extension).add("suffix", suffix).toString()
    }
}
