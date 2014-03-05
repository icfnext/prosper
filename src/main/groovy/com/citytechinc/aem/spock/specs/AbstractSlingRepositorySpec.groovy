package com.citytechinc.aem.spock.specs

import com.citytechinc.aem.groovy.extension.builders.NodeBuilder
import com.citytechinc.aem.groovy.extension.builders.PageBuilder
import com.citytechinc.aem.groovy.extension.metaclass.GroovyExtensionMetaClassRegistry
import com.citytechinc.aem.spock.builders.RequestBuilder
import com.citytechinc.aem.spock.builders.ResponseBuilder
import com.citytechinc.aem.spock.mocks.resource.MockResourceResolver
import com.day.cq.tagging.TagManager
import com.day.cq.tagging.impl.JcrTagManagerImpl
import com.day.cq.wcm.api.NameConstants
import com.day.cq.wcm.api.Page
import com.day.cq.wcm.api.PageManager
import com.day.cq.wcm.core.impl.PageImpl
import com.day.cq.wcm.core.impl.PageManagerFactoryImpl
import org.apache.sling.api.adapter.AdapterFactory
import org.apache.sling.api.resource.Resource
import org.apache.sling.api.resource.ResourceResolver
import org.apache.sling.api.resource.ValueMap
import org.apache.sling.jcr.resource.JcrPropertyMap
import spock.lang.Shared

import javax.jcr.Node
import javax.jcr.Session

/**
 * Spock specification for AEM testing that includes a Sling <code>ResourceResolver</code>.
 */
abstract class AbstractSlingRepositorySpec extends AbstractRepositorySpec {

    @Shared ResourceResolver resourceResolver

    @Shared nodeBuilder

    @Shared pageBuilder

    @Shared
    private def adapterFactories = []

    @Shared
    private def resourceResolverAdapters = [:]

    @Shared
    private def resourceAdapters = [:]

    def setupSpec() {
        GroovyExtensionMetaClassRegistry.registerMetaClasses()

        addDefaultResourceAdapters()
        addDefaultResourceResolverAdapters()

        nodeBuilder = new NodeBuilder(session)
        pageBuilder = new PageBuilder(session)
    }

    def setup() {
        resourceResolver = new MockResourceResolver(session, resourceResolverAdapters, resourceAdapters,
            adapterFactories)
    }

    def cleanupSpec() {
        GroovyExtensionMetaClassRegistry.removeMetaClasses()
    }

    /**
     * Register an <code>AdapterFactory</code> for adapting <code>Resource</code> or <code>ResourceResolver</code>
     * instances to different types at test runtime.  This method should be called within a <code>setupSpec</code>
     * fixture method.
     *
     * @param adapterFactory Sling adapter factory instance
     */
    void registerAdapterFactory(AdapterFactory adapterFactory) {
        adapterFactories.add(adapterFactory)
    }

    /**
     * Add a <code>ResourceResolver</code> adapter type with an instantiation function.  Implementing specs should
     * call this method as necessary in a <code>setupSpec()</code> fixture method to add adapters to Sling
     * <code>ResourceResolver</code> instances at runtime.
     *
     * @param type adapter type
     * @param c closure to instantiate the provided adapter type; closure may contain a
     * <code>ResourceResolver</code>
     * argument
     */
    void addResourceResolverAdapter(Class type, Closure c) {
        resourceResolverAdapters[type] = c
    }

    /**
     * Add a <code>Resource</code> adapter with an instantiation function.  Implementing specs should override this
     * method in a <code>setupSpec()</code> fixture method to add adapters to Sling
     * <code>Resource</code> instances at runtime.
     *
     * @param type adapter type
     * @param c closure to instantiate the provided adapter type; closure may contain a
     * <code>Resource</code>
     * argument
     */
    void addResourceAdapter(Class type, Closure c) {
        resourceAdapters[type] = c
    }

    /**
     * Get a request builder.  If the path is not specified as an argument to the <code>build()
     * </code> closure, the root resource will be bound to the request.
     *
     * @return request builder instance for this resource resolver
     */
    RequestBuilder getRequestBuilder() {
        new RequestBuilder(resourceResolver)
    }

    /**
     * Get a request builder.
     *
     * @param path content path
     * @return request builder instance for this resource resolver
     */
    RequestBuilder getRequestBuilder(String path) {
        new RequestBuilder(resourceResolver, path)
    }

    /**
     * Get a response builder.
     *
     * @return builder
     */
    ResponseBuilder getResponseBuilder() {
        new ResponseBuilder()
    }

    private void addDefaultResourceAdapters() {
        addResourceAdapter(Page, { Resource resource ->
            NameConstants.NT_PAGE == resource.resourceType ? new PageImpl(resource) : null
        })

        addResourceAdapter(ValueMap, { Resource resource ->
            def node = session.getNode(resource.path)

            new JcrPropertyMap(node)
        })

        addResourceAdapter(Node, { Resource resource ->
            session.getNode(resource.path)
        })
    }

    private void addDefaultResourceResolverAdapters() {
        addResourceResolverAdapter(PageManager, { ResourceResolver resourceResolver ->
            def factory = new PageManagerFactoryImpl()

            factory.getPageManager(resourceResolver)
        })

        addResourceResolverAdapter(TagManager, { ResourceResolver resourceResolver ->
            new JcrTagManagerImpl(resourceResolver, null, null, "/etc/tags")
        })

        addResourceResolverAdapter(Session, {
            session
        })
    }
}
