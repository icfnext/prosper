package com.citytechinc.aem.prosper.context

import org.apache.sling.api.adapter.AdapterFactory
import org.apache.sling.models.spi.Injector
import org.osgi.framework.BundleContext

/**
 *
 */
interface SlingContextProvider {

    /**
     * Get the mock OSGi bundle context.
     *
     * @return bundle context
     */
    BundleContext getBundleContext()

    def <T> T registerService(T service)

    def <T> T registerService(Class<T> serviceClass, T service)

    def <T> T registerService(Class<T> serviceClass, T service, Map<String, Object> properties)

    def <T> T registerInjectActivateService(T service)

    def <T> T registerInjectActivateService(T service, Map<String, Object> properties)

    def <ServiceType> ServiceType getService(Class<ServiceType> serviceType)

    def <ServiceType> ServiceType[] getServices(Class<ServiceType> serviceType, String filter)

    /**
     * Register a Sling Injector for use in a test.
     *
     * @param injector injector to register
     * @param serviceRanking OSGi service ranking
     */
    void registerInjector(Injector injector, Integer serviceRanking)

    /**
     * Add <code>@Model</code>-annotated classes for the specified package for use in a test.
     *
     * @param packageName package name to scan for annotated classes
     */
    void addModelsForPackage(String packageName)

    /**
     * Set the Sling run mode(s) for the current test.
     *
     * @param runModes run modes
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
     * Register an adapter factory for the current Prosper context.
     *
     * @param adapterFactory adapter factory instance
     * @param adaptableClasses array of class names that can be adapted from by this factory
     * @param adapterClasses array of class names that can be adapted to by this factory
     */
    void registerAdapterFactory(AdapterFactory adapterFactory, String[] adaptableClasses, String[] adapterClasses)
}