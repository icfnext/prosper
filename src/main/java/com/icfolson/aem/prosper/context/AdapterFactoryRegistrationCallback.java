package com.icfolson.aem.prosper.context;

import com.google.common.collect.ImmutableMap;
import com.icfolson.aem.prosper.adapter.ProsperAdapterFactory;
import io.wcm.testing.mock.aem.junit.AemContext;
import io.wcm.testing.mock.aem.junit.AemContextCallback;
import org.apache.sling.api.adapter.AdapterFactory;

import java.util.Map;

import static org.apache.sling.api.adapter.AdapterFactory.ADAPTABLE_CLASSES;
import static org.apache.sling.api.adapter.AdapterFactory.ADAPTER_CLASSES;

public final class AdapterFactoryRegistrationCallback implements AemContextCallback {

    private static final Map<String, Object> PROPERTIES = new ImmutableMap.Builder<String, Object>()
        .put(ADAPTABLE_CLASSES, ProsperAdapterFactory.ADAPTABLE_CLASSES)
        .put(ADAPTER_CLASSES, ProsperAdapterFactory.ADAPTER_CLASSES)
        .build();

    @Override
    public void execute(final AemContext context) throws Exception {
        context.registerService(AdapterFactory.class, new ProsperAdapterFactory(), PROPERTIES);
    }
}
