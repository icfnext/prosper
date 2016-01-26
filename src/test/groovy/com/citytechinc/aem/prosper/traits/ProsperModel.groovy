package com.citytechinc.aem.prosper.traits

import org.apache.sling.api.resource.Resource
import org.apache.sling.models.annotations.Model
import org.apache.sling.models.annotations.injectorspecific.Self

@Model(adaptables = [Resource])
class ProsperModel {

    @Self
    Resource resource

    String getName() {
        resource.name
    }
}
