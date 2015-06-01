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
        def bindings = new BindingsBuilder(resourceResolver, bundleContext).build {
            path = "/content/prosper/jcr:content"
        }

        def resource = bindings.get(SlingBindings.RESOURCE) as Resource

        expect:
        resource.path == "/content/prosper/jcr:content"
    }

    def "get current page"() {
        setup:
        def bindings = new BindingsBuilder(resourceResolver, bundleContext).build {
            path = "/content/prosper/jcr:content"
        }

        def currentPage = bindings.get(WCMBindings.CURRENT_PAGE) as Page

        expect:
        currentPage.path == "/content/prosper"
    }

    def "set wcm mode"() {
        setup:
        def bindings = new BindingsBuilder(resourceResolver, bundleContext).build {
            wcmMode = WCMMode.DISABLED
        }

        def request = bindings.get(SlingBindings.REQUEST) as SlingHttpServletRequest

        expect:
        WCMMode.fromRequest(request) == WCMMode.DISABLED
    }

    def "get service"() {
        setup:
        def bindings = new BindingsBuilder(resourceResolver, bundleContext).build {
            addService String, "hello"
        }

        def sling = bindings.get(SlingBindings.SLING) as SlingScriptHelper

        expect:
        sling.getService(serviceType) == service

        where:
        serviceType | service
        String      | "hello"
        Integer     | null
    }

    def "get services with filter"() {
        setup:
        def bindings = new BindingsBuilder(resourceResolver, bundleContext).build {
            addServices String, ["", ""] as String[], "foo"
            addServices String, [""] as String[], "foo"
            addServices String, [""] as String[], "bar"
            addServices Integer, [1, 2] as Integer[], "foo"
            addServices Long, [] as Long[], "foo"
        }

        def sling = bindings.get(SlingBindings.SLING) as SlingScriptHelper

        expect:
        sling.getServices(serviceType, filter).length == length

        where:
        serviceType | filter | length
        String      | "foo"  | 3
        String      | "bar"  | 1
        String      | null   | 4
        Integer     | "foo"  | 2
        Long        | "foo"  | 0
    }
}
