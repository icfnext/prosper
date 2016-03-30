package com.citytechinc.aem.prosper.specs

import org.apache.sling.models.spi.DisposalCallbackRegistry
import org.apache.sling.models.spi.Injector

import java.lang.reflect.AnnotatedElement
import java.lang.reflect.Type

class TestInjector implements Injector {

    @Override
    String getName() {
        "test"
    }

    @Override
    Object getValue(Object adaptable, String name, Type declaredType, AnnotatedElement element,
        DisposalCallbackRegistry callbackRegistry) {
        name == "injectedValue" ? this.class.name : null
    }
}
