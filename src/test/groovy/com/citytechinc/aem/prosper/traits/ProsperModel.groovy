package com.citytechinc.aem.prosper.traits

import org.apache.sling.api.resource.Resource
import org.apache.sling.models.annotations.Model
import org.apache.sling.models.annotations.injectorspecific.Self

import javax.inject.Inject

import static org.apache.sling.models.annotations.DefaultInjectionStrategy.OPTIONAL

@Model(adaptables = [Resource], defaultInjectionStrategy = OPTIONAL)
class ProsperModel {

    @Self
    Resource resource

    @Inject
    String injectedValue

    String getName() {
        resource.name
    }
}
