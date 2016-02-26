package com.citytechinc.aem.prosper.context

import org.apache.sling.commons.mime.MimeTypeService
import org.apache.sling.jcr.resource.internal.helper.jcr.PathMapper
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
import org.apache.sling.settings.SlingSettingsService
import org.apache.sling.testing.mock.osgi.MockEventAdmin
import org.apache.sling.testing.mock.sling.context.SlingContextImpl
import org.apache.sling.testing.mock.sling.services.MockMimeTypeService
import org.apache.sling.testing.mock.sling.services.MockSlingSettingService
import org.osgi.framework.BundleContext
import org.osgi.service.component.ComponentContext

/**
 * Prosper implementation of the Sling/OSGi context for usage in specs.
 */
class ProsperSlingContext {

    private final SlingContextImpl slingContext = new SlingContextImpl()

    /**
     * Register default services and model injectors.
     */
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
        registerService(SlingSettingsService, new MockSlingSettingService(["publish"] as Set))
        registerService(MimeTypeService, new MockMimeTypeService())
        registerService(new PathMapper())
    }

    /**
     * Registers a service in the mocked OSGi environment.
     *
     * @param service Service instance
     * @return Registered service instance
     */
    public <T> T registerService(T service) {
        slingContext.registerService(service)
    }

    /**
     * Registers a service in the mocked OSGi environment.
     *
     * @param serviceClass Service class
     * @param service Service instance
     * @return Registered service instance
     */
    public <T> T registerService(Class<T> serviceClass, T service) {
        slingContext.registerService(serviceClass, service)
    }

    /**
     * Registers a service in the mocked OSGi environment.
     *
     * @param serviceClass Service class
     * @param service Service instance
     * @param properties Service properties (optional)
     * @return Registered service instance
     */
    public <T> T registerService(Class<T> serviceClass, T service, Map<String, Object> properties) {
        slingContext.registerService(serviceClass, service, properties)
    }

    /**
     * Injects dependencies, activates and registers a service in the mocked OSGi environment.
     *
     * @param service Service instance
     * @return Registered service instance
     */
    public <T> T registerInjectActivateService(T service) {
        slingContext.registerInjectActivateService(service)
    }

    /**
     * Injects dependencies, activates and registers a service in the mocked OSGi environment.
     *
     * @param service Service instance
     * @param properties Service properties (optional)
     * @return Registered service instance
     */
    public <T> T registerInjectActivateService(T service, Map<String, Object> properties) {
        slingContext.registerInjectActivateService(service, properties)
    }

    /**
     * Lookup a single service.
     *
     * @param serviceType The type (interface) of the service.
     * @return The service instance, or null if the service is not available.
     */
    public <ServiceType> ServiceType getService(Class<ServiceType> serviceType) {
        slingContext.getService(serviceType)
    }

    /**
     * Lookup one or several services.
     *
     * @param serviceType The type (interface) of the service.
     * @param filter An optional filter (LDAP-like, see OSGi spec)
     * @return The services instances or an empty array.
     * @throws RuntimeException If the <code>filter</code> string is not a valid OSGi service filter string.
     */
    public <ServiceType> ServiceType[] getServices(Class<ServiceType> serviceType, String filter) {
        slingContext.getServices(serviceType, filter)
    }

    /**
     * Set current run mode(s).
     *
     * @param runModes Run mode(s).
     */
    void runMode(String... runModes) {
        slingContext.runMode(runModes)
    }

    /**
     * Scan classpaths for given package name (and sub packages) to scan for and register all classes with @Model
     * annotation.
     *
     * @param packageName Java package name
     */
    void addModelsForPackage(String packageName) {
        slingContext.addModelsForPackage(packageName)
    }

    /**
     * Get the mocked OSGi component context.
     *
     * @return mock OSGi component context
     */
    ComponentContext getComponentContext() {
        slingContext.componentContext()
    }

    /**
     * Get the mocked OSGi bundle context.
     *
     * @return mock OSGi bundle context
     */
    BundleContext getBundleContext() {
        slingContext.bundleContext()
    }
}
