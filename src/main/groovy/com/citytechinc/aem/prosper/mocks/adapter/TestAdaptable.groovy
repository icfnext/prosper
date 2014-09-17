package com.citytechinc.aem.prosper.mocks.adapter

interface TestAdaptable {

    void addResourceAdapter(Class adapterType, Closure closure)

    void addResourceResolverAdapter(Class adapterType, Closure closure)
}