package com.citytechinc.aem.prosper.traits

import com.citytechinc.aem.prosper.context.ProsperSlingContext
import org.apache.sling.models.spi.Injector

import static org.osgi.framework.Constants.SERVICE_RANKING

/**
 * Trait for adding Sling Models support for a spec.
 */
trait SlingModelTrait {

    /**
     * This method will be implemented automatically when the test spec extends <code>ProsperSpec</code>.
     *
     * @return the Prosper Sling context
     */
    abstract ProsperSlingContext getSlingContext()

    /**
     * Register a Sling Injector for use in a test.
     *
     * @param injector injector to register
     * @param serviceRanking OSGi service ranking
     */
    void registerInjector(Injector injector, Integer serviceRanking) {
        slingContext.registerInjectActivateService(injector, [(SERVICE_RANKING): serviceRanking])
    }

    /**
     * Add <code>@Model</code>-annotated classes for the specified package for use in a test.
     *
     * @param packageName package name to scan for annotated classes
     */
    void addModelsForPackage(String packageName) {
        slingContext.addModelsForPackage(packageName)
    }
}