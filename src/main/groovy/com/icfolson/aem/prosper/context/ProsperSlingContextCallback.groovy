package com.icfolson.aem.prosper.context

import com.icfolson.aem.prosper.adapter.ProsperAdapterFactory
import io.wcm.testing.mock.aem.junit.AemContext
import io.wcm.testing.mock.aem.junit.AemContextCallback
import org.apache.sling.api.adapter.AdapterFactory

class ProsperSlingContextCallback implements AemContextCallback {

    private static final Map<String, Object> PROPERTIES = [
        (AdapterFactory.ADAPTABLE_CLASSES): ProsperAdapterFactory.ADAPTABLE_CLASSES,
        (AdapterFactory.ADAPTER_CLASSES): ProsperAdapterFactory.ADAPTER_CLASSES
    ]

    @Override
    void execute(AemContext context) throws Exception {
        context.registerService(AdapterFactory, new ProsperAdapterFactory(), PROPERTIES)
    }
}
