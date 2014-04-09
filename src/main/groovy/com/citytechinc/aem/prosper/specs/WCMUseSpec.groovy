package com.citytechinc.aem.prosper.specs

import com.adobe.cq.sightly.WCMUse
import com.citytechinc.aem.prosper.builders.BindingsBuilder

import javax.script.SimpleBindings

class WCMUseSpec extends ProsperSpec {

    public <T extends WCMUse> void init(T instance,
        @DelegatesTo(value = BindingsBuilder, strategy = Closure.OWNER_FIRST) Closure closure) {
        def bindings

        if (closure) {
            bindings = new BindingsBuilder(resourceResolver).build(closure)
        } else {
            bindings = new SimpleBindings()
        }

        instance.init(bindings)
    }

    public <T extends WCMUse> T init(Class<T> type,
        @DelegatesTo(value = BindingsBuilder, strategy = Closure.OWNER_FIRST) Closure closure) {
        def bindings

        if (closure) {
            bindings = new BindingsBuilder(resourceResolver).build(closure)
        } else {
            bindings = new SimpleBindings()
        }

        def instance = type.newInstance()

        instance.init(bindings)

        instance
    }
}
