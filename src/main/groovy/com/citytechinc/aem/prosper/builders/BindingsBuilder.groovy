package com.citytechinc.aem.prosper.builders

import com.adobe.cq.sightly.SightlyWCMMode
import com.adobe.cq.sightly.WCMBindings
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
import org.apache.sling.api.resource.Resource
import org.apache.sling.api.resource.ResourceResolver
import org.apache.sling.api.resource.ResourceUtil
import org.apache.sling.api.resource.ValueMap
import org.apache.sling.api.scripting.SlingBindings
import org.apache.sling.api.scripting.SlingScriptHelper

import javax.script.Bindings
import javax.script.SimpleBindings

/**
 *
 */
class BindingsBuilder {

    private final ResourceResolver resourceResolver

    @Delegate
    private final RequestBuilder requestBuilder

    @Delegate
    private final ResponseBuilder responseBuilder = new ResponseBuilder()

    private final def services = [:]

    private final def servicesWithFilters = [:]

    private def wcmMode

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
     * Add a service instance to the mock Sling Script Helper.
     *
     * @param serviceType type of service to register
     * @param instance service instance (real or mocked)
     */
    public <T> void addService(Class<T> serviceType, T instance) {
        assert serviceType != null, "service type must be non-null"
        assert instance != null, "service instance must be non-null"

        services[serviceType] = [instance]
    }

    /**
     * Add service instances and associated filter criteria to the mock Sling Script Helper.  This method can be called
     * multiple times to register different service and filter value combinations.
     *
     * @param serviceType type of service to register
     * @param instances array of service instances for the given service type and filter
     * @param filter filter string
     */
    public <T> void addServices(Class<T> serviceType, T[] instances, String filter) {
        assert serviceType != null, "service type must be non-null"
        assert instances != null, "service instances must be non-null"

        if (filter == null) {
            services[serviceType] = instances as List
        } else {
            def map = servicesWithFilters[serviceType] ?: [:]
            def allInstances = map[filter] ?: []

            allInstances.addAll(instances as List)

            map[filter] = allInstances

            servicesWithFilters[serviceType] = map
        }
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

        createBindings()
    }

    private def createBindings() {
        def slingRequest = requestBuilder.build()
        def slingResponse = responseBuilder.build()

        if (wcmMode) {
            wcmMode.toRequest(slingRequest)
        }

        def resource = slingRequest.resource

        def bindings = [
            (SlingBindings.SLING)          : createSlingScriptHelper(),
            (SlingBindings.REQUEST)        : slingRequest,
            (SlingBindings.RESPONSE)       : slingResponse,
            (SlingBindings.RESOURCE)       : resource,
            (WCMBindings.PROPERTIES)       : ResourceUtil.getValueMap(resource),
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

        bindings.putAll(createPageBindings(resource))

        new SimpleBindings(bindings)
    }

    private def createPageBindings(Resource resource) {
        def pageManager = resourceResolver.adaptTo(PageManager)
        def currentPage = pageManager.getContainingPage(resource)

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

        [
            (WCMBindings.CURRENT_PAGE)             : currentPage,
            (WCMBindings.RESOURCE_PAGE)            : currentPage,
            (WCMBindings.PAGE_MANAGER)             : pageManager,
            (WCMBindings.INHERITED_PAGE_PROPERTIES): inheritedPageProperties,
            (WCMBindings.PAGE_PROPERTIES)          : pageProperties
        ]
    }

    private def createSlingScriptHelper() {
        [
            getService : { Class serviceType ->
                def instances = services[serviceType]

                instances?.getAt(0)
            },
            getServices: { Class serviceType, String filter ->
                def result

                if (filter == null) {
                    def servicesNoFilters = services[serviceType] ?: []
                    def servicesFilters = []

                    servicesWithFilters[serviceType]?.each { entry ->
                        servicesFilters.addAll(entry.value)
                    }

                    result = (servicesNoFilters + servicesFilters).toArray()
                } else {
                    result = servicesWithFilters[serviceType]?.get(filter)?.toArray()
                }

                result
            }
        ] as SlingScriptHelper
    }
}
