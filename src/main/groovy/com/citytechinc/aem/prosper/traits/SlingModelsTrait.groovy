package com.citytechinc.aem.prosper.traits

import com.citytechinc.aem.prosper.context.ProsperSlingContext
import org.apache.sling.models.spi.Injector

import static org.osgi.framework.Constants.SERVICE_RANKING

/**
 * Trait for "mixing in" Sling Models support for a spec.
 */
trait SlingModelsTrait {

    abstract ProsperSlingContext getSlingContext()

    void registerInjector(Injector injector, Integer serviceRanking) {
        slingContext.registerInjectActivateService(injector, [(SERVICE_RANKING): serviceRanking])
    }

    void addModelsForPackage(String packageName) {
        slingContext.addModelsForPackage(packageName)
    }
}