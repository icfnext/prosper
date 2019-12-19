package com.icfolson.aem.prosper.context

import org.apache.sling.api.adapter.AdapterFactory
import org.apache.sling.api.resource.ResourceResolver
import org.apache.sling.models.spi.Injector
import org.osgi.framework.BundleContext

/**
 * Selection of methods exposed from the Sling and OSGi contexts.
 */
interface SlingContextProvider {

    /**
     * Get the Sling resource resolver for the current Sling context.
     *
     * @return Sling resource resolver
     */
    ResourceResolver getResourceResolver()

    /**
     * Get the mock OSGi bundle context.
     *
     * @return bundle context
     */
    BundleContext getBundleContext()

    /**
     * Registers a service in the mocked OSGi environment.
     *
     * @param service service instance
     * @return registered service instance
     */
    def <T> T registerService(T service)

    /**
     * Registers a service in the mocked OSGi environment.
     *
     * @param serviceClass service class
     * @param service service instance
     * @return registered service instance
     */
    def <T> T registerService(Class<T> serviceClass, T service)

    /**
     * Registers a service in the mocked OSGi environment.
     *
     * @param serviceClass service class
     * @param service service instance
     * @param properties service properties (optional)
     * @return registered service instance
     */
    def <T> T registerService(Class<T> serviceClass, T service, Map<String, Object> properties)

    /**
     * Injects dependencies, activates and registers a service in the mocked OSGi environment.
     *
     * @param service service instance
     * @return registered service instance
     */
    def <T> T registerInjectActivateService(T service)

    /**
     * Injects dependencies, activates and registers a service in the mocked OSGi environment.
     *
     * @param service service instance
     * @param properties service properties (optional)
     * @return registered service instance
     */
    def <T> T registerInjectActivateService(T service, Map<String, Object> properties)

    /**
     * Lookup a single service.
     *
     * @param serviceType the type (interface) of the service.
     * @return the service instance or null if the service is not available
     */
    def <ServiceType> ServiceType getService(Class<ServiceType> serviceType)

    /**
     * Lookup one or several services.
     *
     * @param serviceType the type (interface) of the service.
     * @param filter an optional filter (LDAP-like, see OSGi spec)
     * @return the service instances or an empty array
     */
    def <ServiceType> ServiceType[] getServices(Class<ServiceType> serviceType, String filter)

    /**
     * Scan classpaths for given package name (and sub packages) and register all classes with <code>@Model</code>
     * annotation.
     *
     * @param packageName package name
     */
    void addModelsForPackage(String packageName)

    /**
     * Scan classpath for given package names (and sub packages) and register all classes with <code>@Model</code>
     * annotation.
     *
     * @param packageNames package names
     */
    void addModelsForPackage(String... packageNames)

    /**
     * Scan classpath for given class names and register all classes with <code>@Model</code> annotation.
     *
     * @param classNames class names
     */
    void addModelsForClasses(String... classNames)

    /**
     * Scan classpath for given classes and register all classes with <code>@Model</code> annotation.
     *
     * @param classes classes
     */
    void addModelsForClasses(Class... classes)

    /**
     * Set current run mode(s).
     *
     * @param runModes run mode(s)
     */
    void runMode(String... runModes)

    /**
     * Convenience method to register an adapter for <code>Resource</code> instances.
     *
     * @param adapterType type returned by the closure function
     * @param closure closure accepting a single <code>Resource</code> instance as an argument
     */
    void registerResourceAdapter(Class adapterType, Closure closure)

    /**
     * Convenience method to register an adapter for <code>ResourceResolver</code> instances.
     *
     * @param adapterType type returned by the closure function
     * @param closure closure accepting a single <code>ResourceResolver</code> instance as an argument
     */
    void registerResourceResolverAdapter(Class adapterType, Closure closure)

    /**
     * Convenience method to register an adapter for <code>SlingHttpServletRequest</code> instances.
     *
     * @param adapterType type returned by the closure function
     * @param closure closure accepting a single <code>SlingHttpServletRequest</code> instance as an argument
     */
    void registerRequestAdapter(Class adapterType, Closure closure)

    /**
     * Register an adapter for the current Prosper context.
     *
     * @param adaptableType type to adapt from
     * @param adapterType type returned by the closure function
     * @param closure closure accepting an instance of the adaptable type as an argument and returning an instance of
     * the adapter type
     */
    void registerAdapter(Class adaptableType, Class adapterType, Closure closure)

    /**
     * Register an adapter factory for the current Prosper context.  Use this method only if the adapter factory has
     * an OSGi metadata XML file in the classpath at /OSGI-INF containing the adaptable and adapter classes.
     * Otherwise, use the other permutation of this method to explicitly specify the required metadata values.
     *
     * @param adapterFactory adapter factory instance
     */
    void registerAdapterFactory(AdapterFactory adapterFactory)

    /**
     * Register an adapter factory for the current Prosper context.
     *
     * @param adapterFactory adapter factory instance
     * @param adaptableClasses array of class names that can be adapted from by this factory
     * @param adapterClasses array of class names that can be adapted to by this factory
     */
    void registerAdapterFactory(AdapterFactory adapterFactory, String[] adaptableClasses, String[] adapterClasses)

    /**
     * Register a Sling Injector for use in a test.
     *
     * @param injector injector to register
     * @param serviceRanking OSGi service ranking
     */
    void registerInjector(Injector injector, Integer serviceRanking)

    /**
     * Register a Sling Injector with the default service ranking for use in a test.
     *
     * @param injector injector to register
     */
    void registerInjector(Injector injector)
}