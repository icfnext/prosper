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
class ProsperSlingContext extends SlingContextImpl implements SlingContextProvider {

    final ResourceResolver resourceResolver

    /**
     * Register default services and the Prosper adapter factory.
     */
    ProsperSlingContext() {
        MockSling.setAdapterManagerBundleContext(bundleContext)

        // register default services
        registerInjectActivateService(new MockEventAdmin())
        registerDefaultServices()

        // initialize resource resolver
        resourceResolver = MockSling.newResourceResolver(JCR_OAK, bundleContext)

        // additional prosper services
        registerService(Replicator, [replicate: {}] as Replicator)
        registerInjectActivateService(new PageManagerFactoryImpl())

        // register prosper adapter factory
        registerAdapterFactory(new ProsperAdapterFactory(this), ProsperAdapterFactory.ADAPTABLE_CLASSES,
            ProsperAdapterFactory.ADAPTER_CLASSES)
    }

    @Override
    BundleContext getBundleContext() {
        bundleContext()
    }

    @Override
    void registerResourceAdapter(Class adapterType, Closure closure) {
        registerAdapter(Resource, adapterType, closure)
    }

    @Override
    void registerResourceResolverAdapter(Class adapterType, Closure closure) {
        registerAdapter(ResourceResolver, adapterType, closure)
    }

    @Override
    void registerRequestAdapter(Class adapterType, Closure closure) {
        registerAdapter(SlingHttpServletRequest, adapterType, closure)
    }

    @Override
    void registerAdapter(Class adaptableType, Class adapterType, Closure closure) {
        registerAdapterFactory(new ClosureAdapterFactory(closure), [adaptableType.name] as String[],
            [adapterType.name] as String[])
    }

    @Override
    void registerAdapterFactory(AdapterFactory adapterFactory) {
        registerService(AdapterFactory, adapterFactory)
    }

    @Override
    void registerAdapterFactory(AdapterFactory adapterFactory, String[] adaptableClasses, String[] adapterClasses) {
        registerService(AdapterFactory, adapterFactory, [
            (ADAPTABLE_CLASSES): adaptableClasses,
            (ADAPTER_CLASSES): adapterClasses
        ])
    }

    @Override
    void registerInjector(Injector injector, Integer serviceRanking) {
        registerInjectActivateService(injector, [(SERVICE_RANKING): serviceRanking])
    }
}
