package com.citytechinc.aem.prosper.context

import com.citytechinc.aem.prosper.adapter.ClosureAdapterFactory
import com.citytechinc.aem.prosper.adapter.ProsperAdapterFactory
import com.day.cq.replication.Replicator
import com.day.cq.wcm.core.impl.PageManagerFactoryImpl
import org.apache.sling.api.SlingHttpServletRequest
import org.apache.sling.api.adapter.AdapterFactory
import org.apache.sling.api.resource.Resource
import org.apache.sling.api.resource.ResourceResolver
import org.apache.sling.models.spi.Injector
import org.apache.sling.testing.mock.osgi.MockEventAdmin
import org.apache.sling.testing.mock.sling.MockSling
import org.apache.sling.testing.mock.sling.context.SlingContextImpl
import org.osgi.framework.BundleContext

import static org.apache.sling.api.adapter.AdapterFactory.ADAPTABLE_CLASSES
import static org.apache.sling.api.adapter.AdapterFactory.ADAPTER_CLASSES
import static org.apache.sling.testing.mock.sling.ResourceResolverType.JCR_OAK
import static org.osgi.framework.Constants.SERVICE_RANKING

/**
 * Prosper implementation of the Sling/OSGi context for usage in specs.
 */
class ProsperSlingContext extends SlingContextImpl {

    final ResourceResolver resourceResolver

    /**
     * Register default services and the Prosper adapter factory.
     */
    ProsperSlingContext() {
        MockSling.setAdapterManagerBundleContext(bundleContext())

        // register default services
        registerInjectActivateService(new MockEventAdmin())
        registerDefaultServices()

        // initialize resource resolver
        resourceResolver = MockSling.newResourceResolver(JCR_OAK, bundleContext())

        // additional prosper services
        registerService(Replicator, [replicate: {}] as Replicator)
        registerInjectActivateService(new PageManagerFactoryImpl())

        // register prosper adapter factory
        registerAdapterFactory(new ProsperAdapterFactory(this), ProsperAdapterFactory.ADAPTABLE_CLASSES,
            ProsperAdapterFactory.ADAPTER_CLASSES)
    }

    /**
     * Get the mock OSGi bundle context.
     *
     * @return bundle context
     */
    BundleContext getBundleContext() {
        bundleContext()
    }

    /**
     * Convenience method to register an adapter for <code>Resource</code> instances.
     *
     * @param adapterType type returned by the closure function
     * @param closure closure accepting a single <code>Resource</code> instance as an argument
     */
    void registerResourceAdapter(Class adapterType, Closure closure) {
        registerAdapter(Resource, adapterType, closure)
    }

    /**
     * Convenience method to register an adapter for <code>ResourceResolver</code> instances.
     *
     * @param adapterType type returned by the closure function
     * @param closure closure accepting a single <code>ResourceResolver</code> instance as an argument
     */
    void registerResourceResolverAdapter(Class adapterType, Closure closure) {
        registerAdapter(ResourceResolver, adapterType, closure)
    }

    /**
     * Convenience method to register an adapter for <code>SlingHttpServletRequest</code> instances.
     *
     * @param adapterType type returned by the closure function
     * @param closure closure accepting a single <code>SlingHttpServletRequest</code> instance as an argument
     */
    void registerRequestAdapter(Class adapterType, Closure closure) {
        registerAdapter(SlingHttpServletRequest, adapterType, closure)
    }

    /**
     * Register an adapter for the current Prosper context.
     *
     * @param adaptableType type to adapt from
     * @param adapterType type returned by the closure function
     * @param closure closure accepting an instance of the adaptable type as an argument and returning an instance of
     * the adapter type
     */
    void registerAdapter(Class adaptableType, Class adapterType, Closure closure) {
        registerAdapterFactory(new ClosureAdapterFactory(closure), [adaptableType.name] as String[],
            [adapterType.name] as String[])
    }

    /**
     * Register an adapter factory for the current Prosper context.
     *
     * @param adapterFactory adapter factory instance
     * @param adaptableClasses array of class names that can be adapted from by this factory
     * @param adapterClasses array of class names that can be adapted to by this factory
     */
    void registerAdapterFactory(AdapterFactory adapterFactory, String[] adaptableClasses, String[] adapterClasses) {
        registerService(AdapterFactory, adapterFactory, [
            (ADAPTABLE_CLASSES): adaptableClasses,
            (ADAPTER_CLASSES): adapterClasses
        ])
    }

    /**
     * Register a Sling Injector for use in a test.
     *
     * @param injector injector to register
     * @param serviceRanking OSGi service ranking
     */
    void registerInjector(Injector injector, Integer serviceRanking) {
        registerInjectActivateService(injector, [(SERVICE_RANKING): serviceRanking])
    }
}
