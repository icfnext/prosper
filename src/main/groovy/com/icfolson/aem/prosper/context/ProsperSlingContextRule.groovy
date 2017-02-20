package com.icfolson.aem.prosper.context

import com.icfolson.aem.prosper.adapter.ClosureAdapterFactory
import com.icfolson.aem.prosper.adapter.ProsperAdapterFactory
import io.wcm.testing.mock.aem.MockAemAdapterFactory
import org.apache.sling.api.SlingHttpServletRequest
import org.apache.sling.api.adapter.AdapterFactory
import org.apache.sling.api.resource.PersistenceException
import org.apache.sling.api.resource.Resource
import org.apache.sling.api.resource.ResourceResolver
import org.apache.sling.models.spi.Injector
import org.apache.sling.testing.mock.sling.junit.SlingContext
import org.apache.sling.testing.mock.sling.junit.SlingContextCallback
import org.junit.rules.TestRule
import org.osgi.framework.BundleContext

import static org.apache.sling.api.adapter.AdapterFactory.ADAPTABLE_CLASSES
import static org.apache.sling.api.adapter.AdapterFactory.ADAPTER_CLASSES
import static org.apache.sling.testing.mock.sling.ResourceResolverType.JCR_OAK
import static org.osgi.framework.Constants.SERVICE_RANKING

class ProsperSlingContextRule implements SlingContextProvider, TestRule {

    class ProsperSlingContextCallback implements SlingContextCallback {

        @Override
        void execute(SlingContext context) throws IOException, PersistenceException {
            // register prosper adapter factory
            registerAdapterFactory(new ProsperAdapterFactory(), ProsperAdapterFactory.ADAPTABLE_CLASSES,
                ProsperAdapterFactory.ADAPTER_CLASSES)

            // register mock adapter factory
            registerAdapterFactory(new MockAemAdapterFactory())
        }
    }

    @Delegate
    private SlingContext slingContext = new SlingContext(new ProsperSlingContextCallback(), JCR_OAK)

    @Override
    ResourceResolver getResourceResolver() {
        resourceResolver()
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
