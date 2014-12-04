package com.citytechinc.aem.prosper.traits

import org.apache.sling.api.resource.ResourceResolver

trait AbstractProsperTrait {

    abstract ResourceResolver getResourceResolver()
}
