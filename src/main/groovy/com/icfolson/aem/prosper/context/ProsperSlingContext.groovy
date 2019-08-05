package com.icfolson.aem.prosper.context

import com.icfolson.aem.prosper.adapter.ClosureAdapterFactory
import io.wcm.testing.mock.aem.junit.AemContext
import io.wcm.testing.mock.aem.junit.AemContextBuilder
import org.apache.sling.api.SlingHttpServletRequest
import org.apache.sling.api.adapter.AdapterFactory
import org.apache.sling.api.resource.Resource
import org.apache.sling.api.resource.ResourceResolver
import org.apache.sling.models.spi.Injector
import org.junit.rules.TestRule
import org.osgi.framework.BundleContext

import static org.apache.sling.api.adapter.AdapterFactory.ADAPTABLE_CLASSES
import static org.apache.sling.api.adapter.AdapterFactory.ADAPTER_CLASSES
import static org.apache.sling.testing.mock.sling.ResourceResolverType.JCR_OAK
import static org.osgi.framework.Constants.SERVICE_RANKING

/**
 * Prosper implementation of the Sling/OSGi context rule for usage in specs.
 */
class ProsperSlingContext implements SlingContextProvider, TestRule {

    @Delegate
    private AemContext aemContext = new AemContextBuilder(JCR_OAK)
        .beforeSetUp(new AdapterFactoryRegistrationCallback())
        .build()

    @Override
    ResourceResolver getResourceResolver() {
        aemContext.resourceResolver()
    }

    @Override
    BundleContext getBundleContext() {
        aemContext.bundleContext()
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
