package com.icfolson.aem.prosper.context;

import com.google.common.collect.ImmutableMap;
import com.icfolson.aem.prosper.adapter.ProsperAdapterFactory;
import io.wcm.testing.mock.aem.MockAemAdapterFactory;
import org.apache.sling.api.adapter.AdapterFactory;
import org.apache.sling.testing.mock.sling.junit.SlingContext;
import org.apache.sling.testing.mock.sling.junit.SlingContextCallback;

import java.util.Map;

import static org.apache.sling.api.adapter.AdapterFactory.ADAPTABLE_CLASSES;
import static org.apache.sling.api.adapter.AdapterFactory.ADAPTER_CLASSES;

public final class AdapterFactoryRegistrationCallback implements SlingContextCallback {

    private static final Map<String, Object> PROPERTIES = new ImmutableMap.Builder<String, Object>()
        .put(ADAPTABLE_CLASSES, ProsperAdapterFactory.ADAPTABLE_CLASSES)
        .put(ADAPTER_CLASSES, ProsperAdapterFactory.ADAPTER_CLASSES)
        .build();

    @Override
    public void execute(SlingContext context) {
        // register prosper adapter factory
        context.registerService(AdapterFactory.class, new ProsperAdapterFactory(), PROPERTIES);

        // register mock adapter factory
        context.registerService(AdapterFactory.class, new MockAemAdapterFactory());
    }
}
