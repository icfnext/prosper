package com.citytechinc.aem.prosper.specs

import com.adobe.cq.sightly.WCMUse
import com.citytechinc.aem.prosper.builders.BindingsBuilder

/**
 * Spock specification for testing Sightly component classes that extend <code>WCMUse</code> or implement
 * <code>Use</code>.
 */
abstract class SightlySpec extends ProsperSpec {

    /**
     * Instantiate the component class for the given type, using the provided closure to build the
     * required bindings.  The class will not be initialized.
     *
     * @param type component type
     * @param closure
     * @return initialized component instance
     */
    public <T extends WCMUse> T init(Class<T> type, @DelegatesTo(value = BindingsBuilder) Closure closure) {
        init(type, false, closure)
    }

    /**
     * Instantiate and initialize the component class for the given type, using the provided closure to build the
     * required bindings.
     *
     * @param type component type
     * @param activate if true, component will also be activated before returning
     * @param closure
     * @return initialized component instance
     */
    public <T extends WCMUse> T init(Class<T> type, boolean activate,
        @DelegatesTo(value = BindingsBuilder) Closure closure) {
        def bindings = new BindingsBuilder(resourceResolver).build(closure)

        def instance = type.newInstance()

        instance.init(bindings)

        if (activate) {
            instance.activate()
        }

        instance
    }
}
