package com.citytechinc.aem.prosper.builders

import com.citytechinc.aem.prosper.specs.ProsperSpec
import org.apache.sling.api.scripting.SlingBindings
import org.apache.sling.api.scripting.SlingScriptHelper
import spock.lang.Unroll

@Unroll
class BindingsBuilderSpec extends ProsperSpec {

    def "get service"() {
        setup:
        def bindings = new BindingsBuilder(resourceResolver).build {
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
        def bindings = new BindingsBuilder(resourceResolver).build {
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
