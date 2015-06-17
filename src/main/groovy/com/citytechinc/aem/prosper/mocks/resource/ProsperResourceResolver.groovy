package com.citytechinc.aem.prosper.mocks.resource

import com.citytechinc.aem.prosper.mocks.adapter.ProsperAdaptable
import org.apache.sling.api.resource.ResourceResolver

/**
 * Definition for resource resolver that adds mutable adapter and search path capabilities.
 */
interface ProsperResourceResolver extends ResourceResolver, ProsperAdaptable {

    void setSearchPath(String... searchPath)
}