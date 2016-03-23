package com.citytechinc.aem.prosper.adapter

import groovy.transform.TupleConstructor
import org.apache.sling.api.adapter.AdapterFactory

@TupleConstructor
class AdapterFactoryHolder {

    AdapterFactory adapterFactory

    String[] adaptableClasses

    String[] adapterClasses
}
