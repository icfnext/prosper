package com.icfolson.aem.prosper.mocks.request

import groovy.transform.ToString
import org.apache.sling.api.request.RequestPathInfo
import org.apache.sling.api.resource.Resource
import org.apache.sling.api.resource.ResourceResolver

@ToString(excludes = "resourceResolver")
class MockRequestPathInfo implements RequestPathInfo {

    private final ResourceResolver resourceResolver

    private final List<String> selectors

    final String resourcePath

    final String extension

    final String suffix

    MockRequestPathInfo(ResourceResolver resourceResolver, String resourcePath, List<String> selectors,
        String extension, String suffix) {
        this.resourceResolver = resourceResolver
        this.resourcePath = resourcePath
        this.selectors = selectors
        this.extension = extension
        this.suffix = suffix
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
    Resource getSuffixResource() {
        def suffixResource = null

        if (suffix) {
            suffixResource = resourceResolver.getResource(suffix)
        }

        suffixResource
    }
}
