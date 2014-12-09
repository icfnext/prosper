package com.citytechinc.aem.prosper.mixins

import com.adobe.cq.sightly.WCMUse
import com.citytechinc.aem.prosper.builders.BindingsBuilder
import io.sightly.java.api.Use
import org.apache.sling.api.resource.ResourceResolver

class SightlyMixin extends AbstractProsperMixin {

    SightlyMixin(ResourceResolver resourceResolver) {
        super(resourceResolver)
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
        def bindings = new BindingsBuilder(resourceResolver).build(closure)

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
    public <T extends WCMUse> T activate(Class<T> type, @DelegatesTo(BindingsBuilder) Closure closure) {
        def instance = init(type, closure)

        instance.activate()

        instance
    }
}