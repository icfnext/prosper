package com.citytechinc.aem.prosper.specs

import com.adobe.cq.sightly.WCMUse
import com.citytechinc.aem.prosper.builders.BindingsBuilder
import io.sightly.java.api.Use

import javax.script.Bindings

/**
 * Spock specification for testing Sightly component classes that extend <code>WCMUse</code> or implement
 * <code>Use</code>.
 */
abstract class SightlySpec extends ProsperSpec {

    /**
     * Instantiate and initialize the component class for the given type, using the provided closure to build the
     * required bindings.
     *
     * @param type component type
     * @param closure
     * @param activate if true, component will also be activated before returning
     * @return initialized component instance
     */
    public <T extends WCMUse> T init(Class<T> type,
        @DelegatesTo(value = BindingsBuilder, strategy = Closure.OWNER_FIRST) Closure closure, boolean activate) {
        def bindings = new BindingsBuilder(resourceResolver).build(closure)

        def instance = type.newInstance()

        instance.init(bindings)

        if (activate) {
            instance.activate()
        }

        instance
    }

    /**
     * Instantiate and initialize the component class for the given type, using the provided closure to build the
     * required bindings.
     *
     * @param type component type
     * @param closure
     * @return initialized component instance
     */
    public <T extends Use> T init(Class<T> type,
        @DelegatesTo(value = BindingsBuilder, strategy = Closure.OWNER_FIRST) Closure closure) {
        init(type, closure, null)
    }

    /**
     * Instantiate and initialize the component class for the given type, using the provided closure to build the
     * required bindings.
     *
     * @param type component type
     * @param closure
     * @param additionalBindings additional script bindings to combine with bindings built from closure
     * @return initialized component instance
     */
    public <T extends Use> T init(Class<T> type,
        @DelegatesTo(value = BindingsBuilder, strategy = Closure.OWNER_FIRST) Closure closure,
        Bindings additionalBindings) {
        def bindings = new BindingsBuilder(resourceResolver).build(closure)

        if (additionalBindings) {
            bindings.putAll(additionalBindings)
        }

        def instance = type.newInstance()

        instance.init(bindings)

        instance
    }
}
