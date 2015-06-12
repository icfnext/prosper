package com.citytechinc.aem.prosper.mocks.adapter

import org.apache.sling.api.adapter.AdapterFactory
import org.apache.sling.commons.osgi.PropertiesUtil
import org.osgi.framework.BundleContext
import org.osgi.framework.ServiceReference

class TestAdapterManager {
    private static final PROP_ADAPTABLES = "adaptables"
    private static final PROP_ADAPTERS = "adapters"
    private final BundleContext bundleContext

    TestAdapterManager(final BundleContext bundleContext) {
        this.bundleContext = bundleContext
    }

    public void addAdapter(Class adaptableType, Class adapterType, Closure closure) {
        final Hashtable<String, Object> adapterProperties = new Hashtable<>(2)
        adapterProperties.put(PROP_ADAPTABLES, [adaptableType.getName()] as String[])
        adapterProperties.put(PROP_ADAPTERS, [adapterType.getName()] as String[])
        bundleContext.registerService(AdapterFactory.class.getName(), new InternalAdapterFactory(closure),
            adapterProperties)
    }

    public void addAdapter(AdapterFactory adapterFactory) {
        bundleContext.registerService(AdapterFactory.class.getName(), adapterFactory, new Hashtable<String, Object>())
    }

    public <AdapterType> AdapterType adapt(Object adaptable, Class<AdapterType> adapterType) {
        //find all adapter factories
        def adapterFactories = (
            (bundleContext.getServiceReferences(AdapterFactory.class.getName(), null) ?: []) as List
        ).findResults { ServiceReference serviceReference ->
            final AdapterFactory adapterFactory

            final String[] adaptables = PropertiesUtil.toStringArray(serviceReference.getProperty(PROP_ADAPTABLES))
            final String[] adapters = PropertiesUtil.toStringArray(serviceReference.getProperty(PROP_ADAPTERS))
            if (adaptables != null && adapters != null) {
                final boolean isAdaptable = adaptables.any { String adaptableClassName ->
                    Class adaptableClass = Class.forName(adaptableClassName)
                    adaptableClass && adaptableClass.isInstance(adaptable)
                }
                if (isAdaptable && adapters.contains(adapterType.getName())) {
                    adapterFactory = bundleContext.getService(serviceReference)
                } else {
                    adapterFactory = null
                }
            } else {
                //adapter factory may have been specifically created in a spec without OSGi properties
                adapterFactory = bundleContext.getService(serviceReference)
            }

            adapterFactory
        }

        //try to find result using matched adapter factories
        def result = (AdapterType) adapterFactories.findResult {
            adapterFactory -> adapterFactory.getAdapter(adaptable, adapterType)
        }

        result
    }

    private class InternalAdapterFactory implements AdapterFactory {
        private final Closure closure

        private InternalAdapterFactory(Closure closure) {
            this.closure = closure
        }

        @Override
        def <AdapterType> AdapterType getAdapter(final Object adaptable, final Class<AdapterType> type) {
            return (AdapterType) closure.call(adaptable)
        }
    }
}
