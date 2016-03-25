package com.citytechinc.aem.prosper.context

import org.apache.sling.api.adapter.AdapterFactory
import org.apache.sling.models.spi.Injector
import org.osgi.framework.BundleContext

interface SlingContextProvider {

    BundleContext getBundleContext()

    def <T> T registerService(T service)

    def <T> T registerService(Class<T> serviceClass, T service)

    def <T> T registerService(Class<T> serviceClass, T service, Map<String, Object> properties)

    def <T> T registerInjectActivateService(T service)

    def <T> T registerInjectActivateService(T service, Map<String, Object> properties)

    def <ServiceType> ServiceType getService(Class<ServiceType> serviceType)

    def <ServiceType> ServiceType[] getServices(Class<ServiceType> serviceType, String filter)

    void registerInjector(Injector injector, Integer serviceRanking)

    void addModelsForPackage(String packageName)

    void runMode(String... runModes)

    void registerResourceAdapter(Class adapterType, Closure closure)

    void registerResourceResolverAdapter(Class adapterType, Closure closure)

    void registerRequestAdapter(Class adapterType, Closure closure)

    void registerAdapter(Class adaptableType, Class adapterType, Closure closure)

    void registerAdapterFactory(AdapterFactory adapterFactory, String[] adaptableClasses, String[] adapterClasses)
}