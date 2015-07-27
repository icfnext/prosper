package com.citytechinc.aem.prosper.context

import com.citytechinc.aem.prosper.specs.ProsperSpec
import org.apache.sling.api.resource.Resource
import org.apache.sling.models.annotations.Model
import org.apache.sling.models.annotations.injectorspecific.Self

class ProsperSlingContextSpec extends ProsperSpec {

    @Model(adaptables = Resource)
    static class ResourceModel {

        @Self
        Resource resource

        String getPath() {
            resource.path
        }
    }

    def setupSpec() {
        pageBuilder.content {
            prosper()
        }

        slingContext.addModelsForPackage("com.citytechinc.aem.prosper.context")
    }

    def "register model and get adapter from factory"() {
        setup:
        def resource = getResource("/content/prosper")
        def model = resource.adaptTo(ResourceModel)

        expect:
        model.path == "/content/prosper"
    }
}
