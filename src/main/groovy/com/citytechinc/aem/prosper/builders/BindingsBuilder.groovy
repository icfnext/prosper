package com.citytechinc.aem.prosper.builders

import com.adobe.cq.sightly.SightlyWCMMode
import com.adobe.cq.sightly.WCMBindings
import com.adobe.cq.sightly.WCMUse
import com.adobe.cq.sightly.internal.WCMInheritanceValueMap
import com.adobe.granite.xss.XSSAPI
import com.day.cq.commons.inherit.HierarchyNodeInheritanceValueMap
import com.day.cq.wcm.api.PageManager
import com.day.cq.wcm.api.WCMMode
import com.day.cq.wcm.api.components.Component
import com.day.cq.wcm.api.components.ComponentContext
import com.day.cq.wcm.api.components.EditContext
import com.day.cq.wcm.api.designer.Design
import com.day.cq.wcm.api.designer.Designer
import com.day.cq.wcm.api.designer.Style
import org.apache.sling.api.resource.ResourceResolver
import org.apache.sling.api.resource.ResourceUtil
import org.apache.sling.api.scripting.SlingBindings
import org.apache.sling.api.scripting.SlingScriptHelper

import javax.script.Bindings
import javax.script.SimpleBindings

class BindingsBuilder {

    private final ResourceResolver resourceResolver

    @Delegate RequestBuilder requestBuilder

    @Delegate ResponseBuilder responseBuilder = new ResponseBuilder()

    private def wcmMode

    private def sling

    private def component

    private def componentContext

    private def currentDesign

    private def currentStyle

    private def designer

    private def editContext

    private def resourceDesign

    private def xssApi

    BindingsBuilder(ResourceResolver resourceResolver) {
        this.resourceResolver = resourceResolver

        requestBuilder = new RequestBuilder(resourceResolver)
    }

    /**
     * Set the WCMMode bindings attribute.
     *
     * @param mode WCMMode value
     */
    void setWcmMode(WCMMode wcmMode) {
        this.wcmMode = wcmMode
    }

    void setSling(SlingScriptHelper sling) {
        this.sling = sling
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

    public <T extends WCMUse> void init(T instance, Closure closure) {
        def bindings = build(closure)

        instance.init(bindings)
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

        def slingRequest = requestBuilder.build()
        def slingResponse = responseBuilder.build()

        if (wcmMode) {
            wcmMode.toRequest(slingRequest)
        }

        def resource = slingRequest.resource
        def pageManager = resourceResolver.adaptTo(PageManager)
        def currentPage = pageManager.getContainingPage(resource)

        def bindings = [
            (SlingBindings.SLING): sling,
            (WCMBindings.COMPONENT): component,
            (WCMBindings.COMPONENT_CONTEXT): componentContext,
            (WCMBindings.CURRENT_DESIGN): currentDesign,
            (WCMBindings.CURRENT_STYLE): currentStyle,
            (WCMBindings.DESIGNER): designer,
            (WCMBindings.EDIT_CONTEXT): editContext,
            (WCMBindings.RESOURCE_DESIGN): resourceDesign,
            (WCMBindings.XSSAPI): xssApi,
            (SlingBindings.REQUEST): slingRequest,
            (SlingBindings.RESPONSE): slingResponse,
            (SlingBindings.RESOURCE): resource,
            (WCMBindings.CURRENT_PAGE): pageManager.getContainingPage(resource),
            (WCMBindings.INHERITED_PAGE_PROPERTIES): new WCMInheritanceValueMap(currentPage.contentResource),
            (WCMBindings.PAGE_MANAGER): pageManager,
            (WCMBindings.PAGE_PROPERTIES): new HierarchyNodeInheritanceValueMap(currentPage.contentResource),
            (WCMBindings.PROPERTIES): ResourceUtil.getValueMap(resource),
            (WCMBindings.RESOURCE_PAGE): pageManager.getContainingPage(resource),
            (WCMBindings.WCM_MODE): new SightlyWCMMode(slingRequest)
        ]

        new SimpleBindings(bindings)
    }
}
