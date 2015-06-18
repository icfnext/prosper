package com.citytechinc.aem.prosper.builders

import com.adobe.cq.sightly.WCMBindings
import com.citytechinc.aem.prosper.specs.ProsperSpec
import com.day.cq.wcm.api.Page
import com.day.cq.wcm.api.WCMMode
import org.apache.sling.api.SlingHttpServletRequest
import org.apache.sling.api.resource.Resource
import org.apache.sling.api.scripting.SlingBindings
import org.apache.sling.api.scripting.SlingScriptHelper
import spock.lang.Unroll

@Unroll
class BindingsBuilderSpec extends ProsperSpec {

    def "get resource"() {
        setup:
        def bindings = bindingsBuilder.build {
            path = "/content/prosper/jcr:content"
        }

        def resource = bindings.get(SlingBindings.RESOURCE) as Resource

        expect:
        resource.path == "/content/prosper/jcr:content"
    }

    def "get current page"() {
        setup:
        def bindings = bindingsBuilder.build {
            path = "/content/prosper/jcr:content"
        }

        def currentPage = bindings.get(WCMBindings.CURRENT_PAGE) as Page

        expect:
        currentPage.path == "/content/prosper"
    }

    def "set wcm mode"() {
        setup:
        def bindings = bindingsBuilder.build {
            wcmMode = WCMMode.DISABLED
        }

        def request = bindings.get(SlingBindings.REQUEST) as SlingHttpServletRequest

        expect:
        WCMMode.fromRequest(request) == WCMMode.DISABLED
    }

    def "get service"() {
        setup:
        def bindings = bindingsBuilder.build {
            registerService String, "hello"
        }

        def sling = bindings.get(SlingBindings.SLING) as SlingScriptHelper

        expect:
        sling.getService(serviceType) == service

        where:
        serviceType | service
        String      | "hello"
        Integer     | null
    }
}
