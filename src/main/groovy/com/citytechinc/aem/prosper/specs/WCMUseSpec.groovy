package com.citytechinc.aem.prosper.specs

import com.adobe.cq.sightly.WCMUse
import com.citytechinc.aem.prosper.builders.BindingsBuilder

/**
 * Spock specification for testing component classes that extend <code>WCMUse</code>.
 */
abstract class WCMUseSpec extends ProsperSpec {

    public <T extends WCMUse> T init(Class<T> type,
        @DelegatesTo(value = BindingsBuilder, strategy = Closure.OWNER_FIRST) Closure closure) {
        def bindings = new BindingsBuilder(resourceResolver).build(closure)

        def instance = type.newInstance()

        instance.init(bindings)

        instance
    }
}
