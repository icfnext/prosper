package com.citytechinc.aem.prosper.builders

import com.adobe.cq.sightly.SightlyWCMMode
import com.adobe.cq.sightly.WCMBindings
import com.adobe.cq.sightly.internal.WCMInheritanceValueMap
import com.citytechinc.aem.prosper.context.ProsperSlingContext
import com.citytechinc.aem.prosper.specs.ProsperSpec
import com.day.cq.commons.inherit.HierarchyNodeInheritanceValueMap
import com.day.cq.wcm.api.PageManager
import com.day.cq.wcm.api.WCMMode
import com.day.cq.wcm.api.components.Component
import com.day.cq.wcm.api.components.ComponentContext
import com.day.cq.wcm.api.components.EditContext
import com.day.cq.wcm.api.designer.Design
import com.day.cq.wcm.api.designer.Designer
import com.day.cq.wcm.api.designer.Style
import org.apache.sling.api.resource.Resource
import org.apache.sling.api.resource.ResourceResolver
import org.apache.sling.api.resource.ValueMap
import org.apache.sling.api.scripting.SlingBindings
import org.apache.sling.testing.mock.sling.MockSling
import org.apache.sling.xss.XSSAPI

import javax.script.Bindings
import javax.script.SimpleBindings

/**
 * Builder for creating <code>Bindings</code> instances containing WCM objects for use in Sightly components.
 */
class BindingsBuilder {

    private final ResourceResolver resourceResolver

    private final ProsperSlingContext slingContext

    @Delegate
    private final RequestBuilder requestBuilder

    @Delegate
    private final ResponseBuilder responseBuilder

    private WCMMode wcmMode

    private Component component

    private ComponentContext componentContext

    private Design currentDesign

    private Style currentStyle

    private Designer designer

    private EditContext editContext

    private Design resourceDesign

    private XSSAPI xssApi

    /**
     * Create a new builder for a test spec.
     *
     * @param resourceResolver Sling resource resolver
     * @param adapterManager adapter manager for the current spec
     */
    BindingsBuilder(ProsperSpec spec) {
        resourceResolver = spec.resourceResolver
        slingContext = spec.slingContext
        requestBuilder = new RequestBuilder(spec)
        responseBuilder = new ResponseBuilder()
    }

    /**
     * Add a service instance to the mock Sling Script Helper.
     *
     * @param serviceType type of service to register
     * @param instance service instance (real or mocked)
     */
    public <T> void registerService(Class<T> serviceType, T instance) {
        assert serviceType, "service type must be non-null"
        assert instance, "service instance must be non-null"

        slingContext.registerService(serviceType, instance)
    }

    /**
     * Set the WCMMode bindings attribute.
     *
     * @param mode WCMMode value
     */
    void setWcmMode(WCMMode wcmMode) {
        this.wcmMode = wcmMode
    }

    void setComponent(Component component) {
        this.component = component
    }

    void setComponentContext(ComponentContext componentContext) {
        this.componentContext = componentContext
    }

    void setCurrentDesign(Design currentDesign) {
        this.currentDesign = currentDesign
    }

    void setCurrentStyle(Style currentStyle) {
        this.currentStyle = currentStyle
    }

    void setDesigner(Designer designer) {
        this.designer = designer
    }

    void setEditContext(EditContext editContext) {
        this.editContext = editContext
    }

    void setResourceDesign(Design resourceDesign) {
        this.resourceDesign = resourceDesign
    }

    void setXssApi(XSSAPI xssApi) {
        this.xssApi = xssApi
    }

    Bindings build() {
        build(null)
    }

    Bindings build(Closure closure) {
        if (closure) {
            closure.delegate = this
            closure.resolveStrategy = Closure.DELEGATE_ONLY
            closure()
        }

        buildBindings()
    }

    private def buildBindings() {
        def slingRequest = requestBuilder.build()
        def slingResponse = responseBuilder.build()
        def sling = MockSling.newSlingScriptHelper(slingRequest, slingResponse, slingContext.bundleContext)

        if (wcmMode) {
            wcmMode.toRequest(slingRequest)
        }

        def resource = slingRequest.resource

        def bindings = [
            (SlingBindings.SLING)          : sling,
            (SlingBindings.REQUEST)        : slingRequest,
            (SlingBindings.RESPONSE)       : slingResponse,
            (SlingBindings.RESOURCE)       : resource,
            (WCMBindings.PROPERTIES)       : resource?.valueMap ?: ValueMap.EMPTY,
            (WCMBindings.WCM_MODE)         : new SightlyWCMMode(slingRequest),
            (WCMBindings.COMPONENT)        : component,
            (WCMBindings.COMPONENT_CONTEXT): componentContext,
            (WCMBindings.CURRENT_DESIGN)   : currentDesign,
            (WCMBindings.CURRENT_STYLE)    : currentStyle,
            (WCMBindings.DESIGNER)         : designer,
            (WCMBindings.EDIT_CONTEXT)     : editContext,
            (WCMBindings.RESOURCE_DESIGN)  : resourceDesign,
            (WCMBindings.XSSAPI)           : xssApi
        ]

        bindings.putAll(buildPageBindings(resource))

        new SimpleBindings(bindings)
    }

    private def buildPageBindings(Resource resource) {
        def bindings = [:]

        def pageManager = resourceResolver.adaptTo(PageManager)
        def currentPage = pageManager.getContainingPage(resource)

        bindings.put(WCMBindings.PAGE_MANAGER, pageManager)
        bindings.put(WCMBindings.CURRENT_PAGE, currentPage)
        bindings.put(WCMBindings.RESOURCE_PAGE, currentPage)

        def inheritedPageProperties
        def pageProperties

        if (currentPage) {
            def contentResource = currentPage.contentResource

            inheritedPageProperties = new WCMInheritanceValueMap(contentResource)
            pageProperties = new HierarchyNodeInheritanceValueMap(contentResource)
        } else {
            inheritedPageProperties = ValueMap.EMPTY
            pageProperties = ValueMap.EMPTY
        }

        bindings.put(WCMBindings.INHERITED_PAGE_PROPERTIES, inheritedPageProperties)
        bindings.put(WCMBindings.PAGE_PROPERTIES, pageProperties)

        bindings
    }
}
