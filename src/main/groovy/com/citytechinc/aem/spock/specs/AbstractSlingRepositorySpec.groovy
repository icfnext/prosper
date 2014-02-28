package com.citytechinc.aem.spock.specs

import com.citytechinc.aem.groovy.extension.builders.NodeBuilder
import com.citytechinc.aem.groovy.extension.builders.PageBuilder
import com.citytechinc.aem.groovy.extension.metaclass.GroovyExtensionMetaClassRegistry
import com.citytechinc.aem.spock.builders.RequestBuilder
import com.citytechinc.aem.spock.builders.ResponseBuilder
import com.citytechinc.aem.spock.mocks.resource.MockResourceResolver
import com.day.cq.tagging.TagManager
import com.day.cq.tagging.impl.JcrTagManagerImpl
import com.day.cq.wcm.api.PageManager
import com.day.cq.wcm.core.impl.PageManagerFactoryImpl
import org.apache.sling.api.resource.ResourceResolver
import spock.lang.Shared

import javax.jcr.Session

/**
 * Spock specification for JCR testing that includes a Sling resource resolver.
 */
abstract class AbstractSlingRepositorySpec extends AbstractRepositorySpec {

    @Shared ResourceResolver resourceResolver

    @Shared nodeBuilder

    @Shared pageBuilder

    @Shared
    private def resourceResolverAdapters = [:]

    @Shared
    private def resourceAdapters = [:]

    def setupSpec() {
        GroovyExtensionMetaClassRegistry.registerMetaClasses()

        addDefaultResourceResolverAdapters()
        addResourceResolverAdapters()
        addResourceAdapters()

        resourceResolver = new MockResourceResolver(session, resourceResolverAdapters, resourceAdapters)
        nodeBuilder = new NodeBuilder(session)
        pageBuilder = new PageBuilder(session)
    }

    def cleanupSpec() {
        GroovyExtensionMetaClassRegistry.removeMetaClasses()
    }

    /**
     * Implementing specs should override this method to add adapters to the Sling
     * <code>ResourceResolver</code> at runtime.
     */
    void addResourceResolverAdapters() {

    }

    /**
     * Implementing specs should override this method to add adapters to Sling
     * <code>Resource</code> instances at runtime.
     */
    void addResourceAdapters() {

    }

    /**
     * Add an adapter type with an instantiation function.
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
