package com.citytechinc.aem.prosper.context

import com.citytechinc.aem.prosper.adapter.ClosureAdapterFactory
import com.citytechinc.aem.prosper.adapter.ProsperAdapterFactory
import org.apache.sling.api.adapter.AdapterFactory
import org.apache.sling.testing.mock.osgi.MockEventAdmin
import org.apache.sling.testing.mock.sling.MockSling
import org.apache.sling.testing.mock.sling.context.SlingContextImpl

import static org.apache.sling.api.adapter.AdapterFactory.ADAPTABLE_CLASSES
import static org.apache.sling.api.adapter.AdapterFactory.ADAPTER_CLASSES

/**
 * Prosper implementation of the Sling/OSGi context for usage in specs.
 */
class ProsperSlingContext extends SlingContextImpl {

    /**
     * Register default services and the Prosper adapter factory.
     */
    ProsperSlingContext() {
        MockSling.setAdapterManagerBundleContext(bundleContext())

        // register default services
        registerInjectActivateService(new MockEventAdmin())
        registerDefaultServices()

        // register prosper adapter factory
        registerAdapterFactory(new ProsperAdapterFactory(this), ProsperAdapterFactory.ADAPTABLE_CLASSES,
            ProsperAdapterFactory.ADAPTER_CLASSES)
    }

    void refresh() {
        MockSling.setAdapterManagerBundleContext(bundleContext())
    }

    /**
     * Register an adapter for the current Prosper context.
     *
     * @param adaptableType
     * @param adapterType target adapter type
     * @param closure
     */
    void registerAdapter(Class adaptableType, Class adapterType, Closure closure) {
        registerAdapterFactory(new ClosureAdapterFactory(closure), [adaptableType.name] as String[],
            [adapterType.name] as String[])
    }

    /**
     * Register an adapter factory for the current Prosper context.
     *
     * @param adapterFactory adapter factory instance
     * @param adaptableClasses
     * @param adapterClasses
     */
    void registerAdapterFactory(AdapterFactory adapterFactory, String[] adaptableClasses, String[] adapterClasses) {
        registerService(AdapterFactory, adapterFactory, [
            (ADAPTABLE_CLASSES): adaptableClasses,
            (ADAPTER_CLASSES): adapterClasses
        ])

        // MockSling.setAdapterManagerBundleContext(bundleContext())
    }
}
