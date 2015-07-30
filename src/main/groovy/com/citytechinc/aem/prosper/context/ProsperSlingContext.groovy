package com.citytechinc.aem.prosper.context

import org.apache.sling.commons.mime.MimeTypeService
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

    @Delegate(includes = ["registerService", "registerInjectActivateService", "getService", "getServices",
        "addModelsForPackage", "runMode"])
    final SlingContextImpl slingContext = new SlingContextImpl()

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
    }

    /**
     * @return mock OSGi component context
     */
    ComponentContext getComponentContext() {
        slingContext.componentContext()
    }

    /**
     * @return mock OSGi bundle context
     */
    BundleContext getBundleContext() {
        slingContext.bundleContext()
    }
}
