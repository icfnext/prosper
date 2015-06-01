package com.citytechinc.aem.prosper.mocks.adapter

import org.apache.sling.api.adapter.AdapterFactory

interface TestAdaptable {
    @Deprecated
    void addResourceAdapter(Class adapterType, Closure closure)

    @Deprecated
    void addResourceResolverAdapter(Class adapterType, Closure closure)

    void addAdapter(Class adaptableType, Class adapterType, Closure closure)

    void addAdapter(AdapterFactory adapterFactory)
}