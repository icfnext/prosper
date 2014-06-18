package com.citytechinc.aem.prosper.specs

import com.adobe.cq.sightly.WCMUse
import com.day.cq.wcm.api.WCMMode
import com.day.cq.wcm.api.designer.Style

class SightlySpecSpec extends SightlySpec {

    class TestSightlyComponent extends WCMUse {

        def activated = false

        @Override
        void activate() throws Exception {
            activated = true
        }

        String getStylePath() {
            getCurrentStyle().getPath()
        }
    }

    def setupSpec() {
        pageBuilder.content {
            home("Home") {
                "jcr:content"() {
                    test()
                }
            }
        }
    }

    def "init component"() {
        setup:
        def component = init(TestSightlyComponent) {
            path = "/content/home/jcr:content/test"
            wcmMode = WCMMode.DISABLED
        }

        expect:
        component.resource.path == "/content/home/jcr:content/test"
        component.currentPage.path == "/content/home"
        component.wcmMode.disabled
    }

    def "init component and activate"() {
        setup:
        def component = init(TestSightlyComponent, true) {
            path = "/content/home/jcr:content/test"
        }

        expect:
        component.activated
    }

    def "init component with mock object"() {
        setup:
        def style = Mock(Style)

        def component = init(TestSightlyComponent) {
            path = "/content/home/jcr:content/test"
            wcmMode = WCMMode.DISABLED
            currentStyle = style
        }

        when:
        component.stylePath

        then:
        1 * style.getPath()
    }
}
