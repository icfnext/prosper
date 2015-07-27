package com.citytechinc.aem.prosper.context

import org.apache.sling.models.impl.FirstImplementationPicker
import org.apache.sling.models.impl.ModelAdapterFactory
import org.apache.sling.models.impl.injectors.BindingsInjector
import org.apache.sling.models.impl.injectors.ChildResourceInjector
import org.apache.sling.models.impl.injectors.OSGiServiceInjector
import org.apache.sling.models.impl.injectors.RequestAttributeInjector
import org.apache.sling.models.impl.injectors.ResourcePathInjector
import org.apache.sling.models.impl.injectors.SelfInjector
import org.apache.sling.models.impl.injectors.SlingObjectInjector
import org.apache.sling.models.impl.injectors.ValueMapInjector
import org.apache.sling.models.spi.ImplementationPicker
import org.apache.sling.testing.mock.osgi.MockEventAdmin
import org.apache.sling.testing.mock.sling.context.SlingContextImpl
import org.osgi.framework.BundleContext
import org.osgi.service.component.ComponentContext

/**
 * Prosper implementation of the Sling/OSGi context for usage in specs.
 */
class ProsperSlingContext {

    private final SlingContextImpl slingContext = new SlingContextImpl()

    ProsperSlingContext() {
        registerInjectActivateService(new MockEventAdmin())
        registerInjectActivateService(new ModelAdapterFactory())
        registerInjectActivateService(new BindingsInjector())
        registerInjectActivateService(new ChildResourceInjector())
        registerInjectActivateService(new OSGiServiceInjector())
        registerInjectActivateService(new RequestAttributeInjector())
        registerInjectActivateService(new ResourcePathInjector())
        registerInjectActivateService(new SelfInjector())
        registerInjectActivateService(new SlingObjectInjector())
        registerInjectActivateService(new ValueMapInjector())
        registerService(ImplementationPicker, new FirstImplementationPicker())
    }

    ComponentContext getComponentContext() {
        slingContext.componentContext()
    }

    BundleContext getBundleContext() {
        slingContext.bundleContext()
    }

    def <T> T registerService(T service) {
        slingContext.registerService(service)
    }

    def <T> T registerService(Class<T> serviceClass, T service) {
        slingContext.registerService(serviceClass, service)
    }

    def <T> T registerService(Class<T> serviceClass, T service, Map<String, Object> properties) {
        slingContext.registerService(serviceClass, service, properties)
    }

    def <T> T registerInjectActivateService(T service) {
        slingContext.registerInjectActivateService(service)
    }

    def <T> T registerInjectActivateService(T service, Map<String, Object> properties) {
        slingContext.registerInjectActivateService(service, properties)
    }

    def <ServiceType> ServiceType getService(Class<ServiceType> serviceType) {
        slingContext.getService(serviceType)
    }

    def <ServiceType> ServiceType[] getServices(Class<ServiceType> serviceType, String filter) {
        slingContext.getServices(serviceType, filter)
    }

    void addModelsForPackage(String packageName) {
        slingContext.addModelsForPackage(packageName)
    }
}
