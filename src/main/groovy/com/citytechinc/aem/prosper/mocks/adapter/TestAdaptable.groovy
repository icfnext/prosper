package com.citytechinc.aem.prosper.mocks.adapter

public interface TestAdaptable {

    void addResourceAdapter(Class adapterType, Closure closure)

    void addResourceResolverAdapter(Class adapterType, Closure closure)
}