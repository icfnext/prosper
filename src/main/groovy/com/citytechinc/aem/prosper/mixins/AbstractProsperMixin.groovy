package com.citytechinc.aem.prosper.mixins

import org.apache.sling.api.resource.ResourceResolver

abstract class AbstractProsperMixin {

    protected ResourceResolver resourceResolver

    AbstractProsperMixin(ResourceResolver resourceResolver) {
        this.resourceResolver = resourceResolver
    }
}
