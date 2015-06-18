package com.citytechinc.aem.prosper.adapter

import org.apache.sling.api.adapter.AdapterFactory
import org.apache.sling.api.adapter.AdapterManager
import org.apache.sling.commons.osgi.PropertiesUtil
import org.osgi.framework.BundleContext
import org.osgi.framework.ServiceReference

import static org.apache.sling.api.adapter.AdapterFactory.ADAPTABLE_CLASSES
import static org.apache.sling.api.adapter.AdapterFactory.ADAPTER_CLASSES

/**
 * Adapter manager for Prosper specs.
 */
class ProsperAdapterManager implements AdapterManager {

    private final BundleContext bundleContext

    ProsperAdapterManager(BundleContext bundleContext) {
        this.bundleContext = bundleContext
    }

    void addAdapter(Class adaptableType, Class adapterType, Closure closure) {
        def adapterProperties = new Hashtable<String, Object>(2)

        adapterProperties.put(ADAPTABLE_CLASSES, [adaptableType.name] as String[])
        adapterProperties.put(ADAPTER_CLASSES, [adapterType.name] as String[])

        bundleContext.registerService(AdapterFactory.name, new InternalAdapterFactory(closure), adapterProperties)
    }

    void addAdapterFactory(AdapterFactory adapterFactory) {
        bundleContext.registerService(AdapterFactory.name, adapterFactory, new Hashtable<String, Object>())
    }

    @Override
    public <AdapterType> AdapterType getAdapter(Object adaptable, Class<AdapterType> adapterType) {
        // find all adapter factories
        def adapterFactories = ((bundleContext.getServiceReferences(AdapterFactory.name, null) ?: []) as List)
            .findResults { ServiceReference serviceReference ->
            def adapterFactory

            def adaptables = PropertiesUtil.toStringArray(serviceReference.getProperty(ADAPTABLE_CLASSES))
            def adapters = PropertiesUtil.toStringArray(serviceReference.getProperty(ADAPTER_CLASSES))

            if (adaptables && adapters) {
                def isAdaptable = adaptables.any { adaptableClassName ->
                    def adaptableClass = Class.forName(adaptableClassName)

                    adaptableClass && adaptableClass.isInstance(adaptable)
                }

                if (isAdaptable && adapters.contains(adapterType.name)) {
                    adapterFactory = bundleContext.getService(serviceReference)
                } else {
                    adapterFactory = null
                }
            } else {
                // adapter factory may have been specifically created in a spec without OSGi properties
                adapterFactory = bundleContext.getService(serviceReference)
            }

            adapterFactory
        }

        // try to find result using matched adapter factories
        adapterFactories.findResult {
            adapterFactory -> adapterFactory.getAdapter(adaptable, adapterType)
        } as AdapterType
    }

    private static class InternalAdapterFactory implements AdapterFactory {

        private final Closure closure

        InternalAdapterFactory(Closure closure) {
            this.closure = closure
        }

        @Override
        <AdapterType> AdapterType getAdapter(Object adaptable, Class<AdapterType> type) {
            closure.call(adaptable) as AdapterType
        }
    }
}
