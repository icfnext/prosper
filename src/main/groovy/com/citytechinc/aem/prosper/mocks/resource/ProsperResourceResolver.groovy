package com.citytechinc.aem.prosper.mocks.resource

import org.apache.sling.api.resource.ResourceResolver

/**
 * Definition for resource resolver that adds mutable search path capabilities.
 */
interface ProsperResourceResolver extends ResourceResolver {

    void setSearchPath(String... searchPath)
}