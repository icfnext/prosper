package com.citytechinc.aem.prosper.mocks.resource;

import com.citytechinc.aem.prosper.mocks.adapter.TestAdaptable;
import org.apache.sling.api.resource.ResourceResolver;

/**
 * Definition for resource resolver that adds mutable adapter capabilities.
 */
public interface TestResourceResolver extends ResourceResolver, TestAdaptable {

}