package com.citytechinc.aem.prosper.support

import com.adobe.cq.sightly.WCMUse
import com.citytechinc.aem.prosper.specs.ProsperSpec
import com.day.cq.wcm.api.PageManager
import com.day.cq.wcm.api.WCMMode
import com.day.cq.wcm.api.designer.Style
import io.sightly.java.api.Use
import org.apache.sling.api.resource.Resource
import spock.lang.Shared

import javax.script.Bindings

import static org.apache.sling.api.scripting.SlingBindings.RESOURCE

class SightlySupportSpec extends ProsperSpec {

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

    class TestWcmUseComponent extends WCMUse {

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
    SightlySupport sightlySupport

    def setupSpec() {
        pageBuilder.content {
            home("Home") {
                "jcr:content"() {
                    test()
                }
            }
        }

        sightlySupport = new SightlySupport(resourceResolver)
    }

    def "init component"() {
        setup:
        def component = sightlySupport.init(TestUseComponent) {
            path = "/content/home/jcr:content/test"
        }

        expect:
        component.pageTitle == "Home"
    }

    def "activate component"() {
        setup:
        def component = sightlySupport.activate(TestWcmUseComponent) {
            path = "/content/home/jcr:content/test"
            wcmMode = WCMMode.DISABLED
        }

        expect:
        component.activated
        component.resource.path == "/content/home/jcr:content/test"
        component.currentPage.path == "/content/home"
        component.wcmMode.disabled
    }

    def "init component with mock object"() {
        setup:
        def style = Mock(Style)

        def component = sightlySupport.activate(TestWcmUseComponent) {
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