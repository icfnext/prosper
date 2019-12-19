package com.icfolson.aem.prosper.annotations

import com.icfolson.aem.prosper.models.AdditionalProsperSlingModel
import com.icfolson.aem.prosper.models.ProsperSlingModel
import com.icfolson.aem.prosper.specs.ProsperSpec

@ModelSpec(additionalPackages = "com.icfolson.aem.prosper.models")
class ModelSpecAnnotationSpec extends ProsperSpec {

    def "model in current package is registered when spec is annotated"() {
        expect:
        getResource("/").adaptTo(ProsperSlingModel)
    }

    def "model in other package is registered when additional packages are specified"() {
        expect:
        getResource("/").adaptTo(AdditionalProsperSlingModel)
    }
}
