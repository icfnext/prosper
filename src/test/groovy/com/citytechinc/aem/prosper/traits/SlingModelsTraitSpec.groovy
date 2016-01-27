package com.citytechinc.aem.prosper.traits

import com.citytechinc.aem.prosper.specs.ProsperSpec

class SlingModelsTraitSpec extends ProsperSpec implements SlingModelsTrait {

    def setupSpec() {
        nodeBuilder.content {
            test()
        }
    }

    def "add models for package"() {
        setup:
        addModelsForPackage(this.class.package.name)

        expect:
        getResource("/content/test").adaptTo(ProsperModel).name == "test"
    }

    def "register injector"() {
        setup:
        addModelsForPackage(this.class.package.name)
        registerInjector(new TestInjector(), 10000)

        expect:
        getResource("/content/test").adaptTo(ProsperModel).injectedValue == TestInjector.class.name
    }
}
