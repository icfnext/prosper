package com.citytechinc.aem.prosper.mixins

import com.adobe.cq.sightly.WCMUsePojo
import com.citytechinc.aem.prosper.builders.BindingsBuilder
import com.citytechinc.aem.prosper.specs.ProsperSpec
import org.apache.sling.scripting.sightly.pojo.Use

/**
 * Mixin providing methods for initializing Sightly component classes with mocked bindings.
 */
class SightlyMixin extends ProsperMixin {

    SightlyMixin(ProsperSpec spec) {
        super(spec)
    }

    /**
     * Instantiate and initialize the component class for the given type, using the provided closure to build the
     * required bindings.
     *
     * @param type component type
     * @param closure
     * @return initialized component instance
     */
    public <T extends Use> T init(Class<T> type, @DelegatesTo(BindingsBuilder) Closure closure) {
        def bindings = spec.bindingsBuilder.build(closure)

        def instance = type.newInstance()

        instance.init(bindings)

        instance
    }

    /**
     * Instantiate, initialize, and activate the component class for the given type,
     * using the provided closure to build the required bindings.
     *
     * @param type component type
     * @param closure
     * @return activated component instance
     */
    public <T extends WCMUsePojo> T activate(Class<T> type, @DelegatesTo(BindingsBuilder) Closure closure) {
        def instance = init(type, closure)

        instance.activate()

        instance
    }
}