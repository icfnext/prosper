package com.citytechinc.aem.prosper.mocks.resource

import com.citytechinc.aem.prosper.mocks.adapter.TestAdaptable
import org.apache.sling.api.resource.ResourceResolver

/**
 * Definition for resource resolver that adds mutable adapter and search path capabilities.
 */
interface TestResourceResolver extends ResourceResolver, TestAdaptable {

    void setSearchPath(String... searchPath)
}