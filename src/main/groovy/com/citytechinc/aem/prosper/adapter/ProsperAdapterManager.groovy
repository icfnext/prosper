package com.citytechinc.aem.prosper.adapter

import com.citytechinc.aem.prosper.context.ProsperSlingContext
import groovy.transform.TupleConstructor
import org.apache.sling.api.adapter.AdapterFactory
import org.apache.sling.api.adapter.AdapterManager
import org.apache.sling.commons.osgi.PropertiesUtil
import org.osgi.framework.ServiceReference

import static org.apache.sling.api.adapter.AdapterFactory.ADAPTABLE_CLASSES
import static org.apache.sling.api.adapter.AdapterFactory.ADAPTER_CLASSES

/**
 * Adapter manager for Prosper specs.
 */
@TupleConstructor
class ProsperAdapterManager implements AdapterManager {

    ProsperSlingContext slingContext

    void addAdapter(Class adaptableType, Class adapterType, Closure closure) {
        def adapterProperties = [:]

        adapterProperties.put(ADAPTABLE_CLASSES, [adaptableType.name] as String[])
        adapterProperties.put(ADAPTER_CLASSES, [adapterType.name] as String[])

        slingContext.registerService(AdapterFactory, new InternalAdapterFactory(closure), adapterProperties)
    }

    void addAdapterFactory(AdapterFactory adapterFactory) {
        slingContext.registerService(AdapterFactory, adapterFactory)
    }

    @Override
    public <AdapterType> AdapterType getAdapter(Object adaptable, Class<AdapterType> adapterType) {
        // find all adapter factories
        def serviceReferences = slingContext.bundleContext.getServiceReferences(AdapterFactory.name, null)

        def adapterFactories = ((serviceReferences ?: []) as List).findResults { ServiceReference serviceReference ->
            def adapterFactory

            def adaptables = PropertiesUtil.toStringArray(serviceReference.getProperty(ADAPTABLE_CLASSES))
            def adapters = PropertiesUtil.toStringArray(serviceReference.getProperty(ADAPTER_CLASSES))

            if (adaptables && adapters) {
                def isAdaptable = adaptables.any { adaptableClassName ->
                    def adaptableClass = Class.forName(adaptableClassName)

                    adaptableClass && adaptableClass.isInstance(adaptable)
                }

                if (isAdaptable && adapters.contains(adapterType.name)) {
                    adapterFactory = slingContext.bundleContext.getService(serviceReference)
                } else {
                    adapterFactory = null
                }
            } else {
                // adapter factory may have been specifically created in a spec without OSGi properties
                adapterFactory = slingContext.bundleContext.getService(serviceReference)
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
