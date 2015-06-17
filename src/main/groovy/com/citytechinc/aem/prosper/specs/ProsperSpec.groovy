package com.citytechinc.aem.prosper.specs

import com.citytechinc.aem.groovy.extension.builders.NodeBuilder
import com.citytechinc.aem.groovy.extension.builders.PageBuilder
import com.citytechinc.aem.groovy.extension.metaclass.GroovyExtensionMetaClassRegistry
import com.citytechinc.aem.prosper.annotations.NodeTypes
import com.citytechinc.aem.prosper.builders.BindingsBuilder
import com.citytechinc.aem.prosper.builders.RequestBuilder
import com.citytechinc.aem.prosper.builders.ResponseBuilder
import com.citytechinc.aem.prosper.importer.ContentImporter
import com.citytechinc.aem.prosper.mixins.ProsperMixin
import com.citytechinc.aem.prosper.mocks.adapter.ProsperAdaptable
import com.citytechinc.aem.prosper.mocks.adapter.ProsperAdapterManager
import com.citytechinc.aem.prosper.mocks.resource.MockResourceResolver
import com.citytechinc.aem.prosper.mocks.resource.ProsperResourceResolver
import com.citytechinc.aem.prosper.traits.ProsperAsserts
import com.day.cq.replication.Replicator
import com.day.cq.tagging.TagManager
import com.day.cq.tagging.impl.JcrTagManagerImpl
import com.day.cq.wcm.api.NameConstants
import com.day.cq.wcm.api.Page
import com.day.cq.wcm.api.PageManager
import com.day.cq.wcm.core.impl.PageImpl
import com.day.cq.wcm.core.impl.PageManagerFactoryImpl
import groovy.transform.Synchronized
import org.apache.sling.api.SlingHttpServletRequest
import org.apache.sling.api.adapter.AdapterFactory
import org.apache.sling.api.resource.Resource
import org.apache.sling.api.resource.ResourceResolver
import org.apache.sling.commons.testing.jcr.RepositoryUtil
import org.apache.sling.jcr.api.SlingRepository
import org.apache.sling.testing.mock.osgi.MockOsgi
import org.osgi.framework.BundleContext
import org.osgi.service.event.EventAdmin
import spock.lang.Shared
import spock.lang.Specification

import javax.jcr.Node
import javax.jcr.Session
import java.lang.reflect.Field

/**
 * Spock specification for AEM testing that includes a Sling <code>ResourceResolver</code>, content builders, and
 * adapter registration capabilities.
 */
abstract class ProsperSpec extends Specification implements ProsperAdaptable, ProsperAsserts {

    private static final def SYSTEM_NODE_NAMES = ["jcr:system", "rep:policy"]

    private static final def NODE_TYPES = ["sling", "replication", "tagging", "core", "dam", "vlt", "widgets"]

    private static SlingRepository repositoryInternal

    @Shared
    private Session sessionInternal

    @Shared
    private ProsperResourceResolver resourceResolverInternal

    @Shared
    private PageManager pageManagerInternal

    @Shared
    private NodeBuilder nodeBuilderInternal

    @Shared
    private PageBuilder pageBuilderInternal

    @Shared
    private BundleContext bundleContextInternal = MockOsgi.newBundleContext()

    @Shared
    private ProsperAdapterManager adapterManagerInternal

    // global fixtures

    /**
     * Create an administrative JCR session with content builders, register Sling adapters,
     * and instantiate a mock resource resolver.
     */
    def setupSpec() {
        GroovyExtensionMetaClassRegistry.registerMetaClasses()

        sessionInternal = repository.loginAdministrative(null)
        nodeBuilderInternal = new NodeBuilder(sessionInternal)
        pageBuilderInternal = new PageBuilder(sessionInternal)
        adapterManagerInternal = new ProsperAdapterManager(bundleContextInternal)

        registerNodeTypes()

        ContentImporter.importVaultContent(this)

        addAdapters()

        resourceResolverInternal = new MockResourceResolver(sessionInternal, adapterManagerInternal)
        pageManagerInternal = resourceResolver.adaptTo(PageManager)

        addMixins()
    }

    def cleanupSpec() {
        GroovyExtensionMetaClassRegistry.removeMetaClasses()

        removeAllNodes()

        sessionInternal.logout()
    }

    /**
     * Refresh the shared resource resolver before each test run.
     */
    def setup() {
        resourceResolverInternal.refresh()
    }

    /**
     * Remove all non-system nodes to cleanup any test data.  This method would typically be called from a test fixture
     * method to cleanup content before the entire specification has been executed.
     */
    void removeAllNodes() {
        sessionInternal.rootNode.nodes.findAll { !SYSTEM_NODE_NAMES.contains(it.name) }*.remove()
        sessionInternal.save()
    }

    // "overridable" instance methods returning default (empty) values

    /**
     * Add <code>AdapterFactory</code> instances for adapting <code>Resource</code> or <code>ResourceResolver</code>
     * instances to different types at test runtime.  Specs should override this method to add testing adapter
     * factories at runtime.
     *
     * @return collection of Sling adapter factories
     */
    Collection<AdapterFactory> addAdapterFactories() {
        Collections.emptyList()
    }

    /**
     * Add <code>Resource</code> adapters and their associated adapter functions.  The mapped closure will be called
     * with a single <code>Resource</code> argument.  Specs should override this method to add resource adapters at
     * runtime.
     *
     * @return map of adapter types to adapter functions
     */
    Map<Class, Closure> addResourceAdapters() {
        Collections.emptyMap()
    }

    /**
     * Add <code>ResourceResolver</code> adapters and their associated adapter functions. The mapped closure will be
     * called with a single <code>ResourceResolver</code> argument.  Specs should override this method to add
     * resource resolver adapters at runtime.
     *
     * @return map of adapter types to adapter functions
     */
    Map<Class, Closure> addResourceResolverAdapters() {
        Collections.emptyMap()
    }

    /**
     * Add <code>SlingHttpServletRequest</code> adapters and their associated adapter functions. The mapped closure
     * will be called with a single <code>SlingHttpServletRequest</code> argument.  Specs should override this method
     * to add request adapters at runtime.
     *
     * @return map of adapter types to adapter functions
     */
    Map<Class, Closure> addRequestAdapters() {
        Collections.emptyMap()
    }

    /**
     * Add a <code>Resource</code> adapter for the current specification.  This method can be called as many times as
     * necessary in a feature method to add adapters for the current test.
     *
     * @param adapterType adapter class
     * @param closure closure with a single <code>Resource</code> that returns an instance of the adapter class
     */
    @Override
    void addResourceAdapter(Class adapterType, Closure closure) {
        adapterManagerInternal.addAdapter(Resource.class, adapterType, closure)
    }

    /**
     * Add a <code>ResourceResolver</code> adapter for the current specification.  This method can be called as many
     * times as necessary in a feature method to add adapters for the current test.
     *
     * @param adapterType adapter class
     * @param closure closure with a single <code>ResourceResolver</code> that returns an instance of the adapter class
     */
    @Override
    void addResourceResolverAdapter(Class adapterType, Closure closure) {
        adapterManagerInternal.addAdapter(ResourceResolver.class, adapterType, closure)
    }

    @Override
    void addAdapter(Class adaptableType, Class adapterType, Closure closure) {
        adapterManagerInternal.addAdapter(adaptableType, adapterType, closure)
    }

    @Override
    void addAdapterFactory(AdapterFactory adapterFactory) {
        adapterManagerInternal.addAdapterFactory(adapterFactory)
    }

    // accessors for shared instances

    /**
     * @return adapter manager
     */
    ProsperAdapterManager getAdapterManager() {
        adapterManagerInternal
    }

    /**
     * @return admin session
     */
    Session getSession() {
        sessionInternal
    }

    /**
     * Get the Node for a path.
     *
     * @param path valid JCR Node path
     * @return node for given path
     */
    Node getNode(String path) {
        sessionInternal.getNode(path)
    }

    /**
     * @return JCR node builder
     */
    NodeBuilder getNodeBuilder() {
        nodeBuilderInternal
    }

    /**
     * @return CQ page builder
     */
    PageBuilder getPageBuilder() {
        pageBuilderInternal
    }

    /**
     * @return Mock BundleContext
     */
    BundleContext getBundleContext() {
        bundleContextInternal
    }

    /**
     * @return admin resource resolver
     */
    ProsperResourceResolver getResourceResolver() {
        resourceResolverInternal
    }

    /**
     * @return CQ page manager
     */
    PageManager getPageManager() {
        pageManagerInternal
    }

    // convenience getters

    /**
     * Get the Resource for a path.
     *
     * @param path valid Resource path
     * @return resource for given path or null
     */
    Resource getResource(String path) {
        resourceResolverInternal.getResource(path)
    }

    /**
     * Get the Page for a path.
     *
     * @param path valid Page path
     * @return Page for given path or null
     */
    Page getPage(String path) {
        pageManagerInternal.getPage(path)
    }

    // builders

    /**
     * Get a request builder.  If the path is not specified as an argument to the <code>build()
     * </code> closure, the root resource will be bound to the request.
     *
     * @return request builder instance for this resource resolver
     */
    RequestBuilder getRequestBuilder() {
        new RequestBuilder(this)
    }

    /**
     * Get a response builder.
     *
     * @return builder
     */
    ResponseBuilder getResponseBuilder() {
        new ResponseBuilder()
    }

    /**
     * Get a bindings builder.
     *
     * @return builder
     */
    BindingsBuilder getBindingsBuilder() {
        new BindingsBuilder(this)
    }

    // internals

    @Synchronized
    protected SlingRepository getRepository() {
        if (!repositoryInternal) {
            RepositoryUtil.startRepository()

            repositoryInternal = RepositoryUtil.repository

            registerCoreNodeTypes()

            addShutdownHook {
                RepositoryUtil.stopRepository()
            }
        }

        repositoryInternal
    }

    private void registerCoreNodeTypes() {
        def session = repository.loginAdministrative(null)

        try {
            def cndResourcePaths = NODE_TYPES.collect { type -> "/SLING-INF/nodetypes/${type}.cnd" }

            registerNodeTypes(session, cndResourcePaths)
        } finally {
            session.logout()
        }
    }

    private void registerNodeTypes() {
        if (this.class.isAnnotationPresent(NodeTypes)) {
            def cndResourcePaths = this.class.getAnnotation(NodeTypes).value() as List

            registerNodeTypes(sessionInternal, cndResourcePaths)
        }
    }

    private void registerNodeTypes(Session session, List<String> cndResourcePaths) {
        cndResourcePaths.each { cndResourcePath ->
            this.class.getResourceAsStream(cndResourcePath).withStream { stream ->
                RepositoryUtil.registerNodeType(session, stream)
            }
        }
    }

    private void addAdapters() {
        addAdapterFactories().each { adapterFactory ->
            adapterManagerInternal.addAdapterFactory(adapterFactory)
        }

        addDefaultResourceAdapters()
        addDefaultResourceResolverAdapters()

        addResourceAdapters().each { Map.Entry<Class, Closure> resourceAdapter ->
            adapterManagerInternal.addAdapter(Resource, resourceAdapter.key, resourceAdapter.value)
        }

        addResourceResolverAdapters().each { Map.Entry<Class, Closure> resourceResolverAdapter ->
            adapterManagerInternal.addAdapter(ResourceResolver, resourceResolverAdapter.key,
                resourceResolverAdapter.value)
        }

        addRequestAdapters().each { Map.Entry<Class, Closure> requestAdapter ->
            adapterManagerInternal.addAdapter(SlingHttpServletRequest, requestAdapter.key, requestAdapter.value)
        }
    }

    private void addDefaultResourceAdapters() {
        adapterManagerInternal.addAdapter(Resource, Page, { Resource resource ->
            NameConstants.NT_PAGE == resource.resourceType ? new PageImpl(resource) : null
        })
    }

    private void addDefaultResourceResolverAdapters() {
        adapterManagerInternal.addAdapter(ResourceResolver.class, PageManager.class, {
            ResourceResolver resourceResolver ->
                def factory = new PageManagerFactoryImpl()

                def fields = [
                    replicator: [replicate: {}] as Replicator,
                    eventAdmin: [postEvent: {}, sendEvent: {}] as EventAdmin,
                    repository: this.repository
                ]

                fields.each { name, instance ->
                    factory.class.getDeclaredField(name).with {
                        accessible = true
                        set(factory, instance)
                    }
                }

                factory.getPageManager(resourceResolver)
            })

        adapterManagerInternal.addAdapter(ResourceResolver, TagManager, { ResourceResolver resourceResolver ->
            new JcrTagManagerImpl(resourceResolver, null, null, "/etc/tags")
        })

        adapterManagerInternal.addAdapter(ResourceResolver, Session, { sessionInternal })
    }

    private void addMixins() {
        findAllMixins(this.class).each { mixin ->
            def instance = mixin.type.getConstructor(ProsperSpec).newInstance(this)

            mixin.with {
                accessible = true
                set(this, instance)
            }
        }
    }

    private List<Field> findAllMixins(Class mixinClass) {
        def mixins = []
        def clazz = mixinClass

        while (clazz && clazz != ProsperSpec) {
            mixins.addAll(clazz.declaredFields.findAll { ProsperMixin.isAssignableFrom(it.type) })

            clazz = clazz.superclass
        }

        mixins
    }
}