package com.citytechinc.aem.prosper.mixins

import com.adobe.cq.sightly.WCMUsePojo
import com.citytechinc.aem.prosper.specs.ProsperSpec
import com.day.cq.wcm.api.PageManager
import com.day.cq.wcm.api.WCMMode
import com.day.cq.wcm.api.designer.Style
import org.apache.sling.api.resource.Resource
import org.apache.sling.scripting.sightly.pojo.Use
import spock.lang.Shared

import javax.script.Bindings

import static org.apache.sling.api.scripting.SlingBindings.RESOURCE

class SightlyMixinSpec extends ProsperSpec {

    class TestUseComponent implements Use {

        private Resource resource

        @Override
        void init(Bindings bindings) {
            resource = bindings.get(RESOURCE) as Resource
        }

        def getPageTitle() {
            def currentPage = resource.resourceResolver.adaptTo(PageManager).getContainingPage(resource)

            currentPage.title
        }
    }

    class TestWcmUseComponent extends WCMUsePojo {

        def activated = false

        @Override
        void activate() throws Exception {
            activated = true
        }

        String getStylePath() {
            getCurrentStyle().getPath()
        }
    }

    @Shared
    SightlyMixin sightly

    def "init component"() {
        setup:
        def component = sightly.init(TestUseComponent) {
            path = "/content/prosper/jcr:content/test"
        }

        expect:
        component.pageTitle == "Prosper"
    }

    def "activate component"() {
        setup:
        def component = sightly.activate(TestWcmUseComponent) {
            path = "/content/prosper/jcr:content/test"
            wcmMode = WCMMode.DISABLED
        }

        expect:
        component.activated
        component.resource.path == "/content/prosper/jcr:content/test"
        component.currentPage.path == "/content/prosper"
        component.wcmMode.disabled
    }

    def "init component with mock object"() {
        setup:
        def style = Mock(Style)

        def component = sightly.activate(TestWcmUseComponent) {
            path = "/content/prosper/jcr:content/test"
            wcmMode = WCMMode.DISABLED
            currentStyle = style
        }

        when:
        component.stylePath

        then:
        1 * style.getPath()
    }
}
