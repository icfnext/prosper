package com.citytechinc.aem.prosper.adapter

import groovy.transform.TupleConstructor
import org.apache.sling.api.adapter.AdapterFactory

@TupleConstructor
class ClosureAdapterFactory implements AdapterFactory {

    Closure closure

    @Override
    <AdapterType> AdapterType getAdapter(Object adaptable, Class<AdapterType> type) {
        closure.call(adaptable) as AdapterType
    }
}
