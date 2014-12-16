package com.citytechinc.aem.prosper.mixins

import org.apache.sling.api.resource.ResourceResolver

/**
 * Base class for Prosper mixins.  Custom mixins may extend this class to provide additional functionality to specs.
 * Mixin instances defined as <code>@Shared</code> fields in specs will be instantiated automatically for use in spec
 * methods.
 */
abstract class ProsperMixin {

    protected ResourceResolver resourceResolver

    ProsperMixin(ResourceResolver resourceResolver) {
        this.resourceResolver = resourceResolver
    }
}
