package com.citytechinc.aem.prosper.specs

import com.citytechinc.aem.groovy.extension.builders.NodeBuilder
import com.citytechinc.aem.groovy.extension.builders.PageBuilder
import com.citytechinc.aem.groovy.extension.metaclass.GroovyExtensionMetaClassRegistry
import com.citytechinc.aem.prosper.adapter.ProsperAdapterFactory
import com.citytechinc.aem.prosper.adapter.ProsperAdapterManager
import com.citytechinc.aem.prosper.annotations.NodeTypes
import com.citytechinc.aem.prosper.builders.BindingsBuilder
import com.citytechinc.aem.prosper.builders.RequestBuilder
import com.citytechinc.aem.prosper.builders.ResponseBuilder
import com.citytechinc.aem.prosper.context.ProsperSlingContext
import com.citytechinc.aem.prosper.importer.ContentImporter
import com.citytechinc.aem.prosper.mixins.ProsperMixin
import com.day.cq.commons.jcr.JcrConstants
import com.day.cq.wcm.api.NameConstants
import com.day.cq.wcm.api.Page
import com.day.cq.wcm.api.PageManager
import org.apache.sling.api.SlingHttpServletRequest
import org.apache.sling.api.adapter.AdapterFactory
import org.apache.sling.api.adapter.SlingAdaptable
import org.apache.sling.api.resource.Resource
import org.apache.sling.api.resource.ResourceResolver
import org.apache.sling.api.resource.ResourceResolverFactory
import org.apache.sling.commons.testing.jcr.RepositoryUtil
import org.apache.sling.jcr.api.SlingRepository
import org.apache.sling.testing.mock.sling.MockSling
import org.apache.sling.testing.mock.sling.ResourceResolverType
import spock.lang.AutoCleanup
import spock.lang.Shared
import spock.lang.Specification

import javax.jcr.Node
import javax.jcr.Session
import java.lang.reflect.Field

/**
 * Spock specification for AEM testing that includes a Sling <code>ResourceResolver</code>, content builders, and
 * adapter registration capabilities.
 */
abstract class ProsperSpec extends Specification {

    private static final def SYSTEM_NODE_NAMES = ["jcr:system", "rep:policy"]

    private static final def NODE_TYPES = ["sling", "replication", "tagging", "core", "dam", "vlt", "widgets"]

    @Shared
    private ProsperSlingContext slingContextInternal = new ProsperSlingContext()

    @Shared
    private ProsperAdapterManager adapterManagerInternal = new ProsperAdapterManager(slingContextInternal)

    @Shared
    private ResourceResolverFactory resourceResolverFactoryInternal

    @Shared
    @AutoCleanup
    private ResourceResolver resourceResolverInternal

    @Shared
    @AutoCleanup("logout")
    private Session sessionInternal

    @Shared
    private PageManager pageManagerInternal

    @Shared
    private NodeBuilder nodeBuilderInternal

    @Shared
    private PageBuilder pageBuilderInternal

    // global fixtures

    /**
     * Create an administrative JCR session with content builders, register Sling adapters,
     * and instantiate a mock resource resolver.
     */
    def setupSpec() {
        GroovyExtensionMetaClassRegistry.registerMetaClasses()

        SlingAdaptable.adapterManager = adapterManagerInternal
        MockSling.setAdapterManagerBundleContext(slingContext.bundleContext)

        resourceResolverFactoryInternal = MockSling.newResourceResolverFactory(ResourceResolverType.JCR_JACKRABBIT,
            slingContext.bundleContext)
        resourceResolverInternal = resourceResolverFactoryInternal.getAdministrativeResourceResolver(null)
        sessionInternal = resourceResolverInternal.adaptTo(Session)



        nodeBuilderInternal = new NodeBuilder(sessionInternal)
        pageBuilderInternal = new PageBuilder(sessionInternal)

        registerNodeTypes()

        ContentImporter.importVaultContent(this)

        addAdapters()

        // resourceResolverInternal = new MockResourceResolver(sessionInternal, adapterManager)
        pageManagerInternal = resourceResolver.adaptTo(PageManager)

        addMixins()
    }

    def cleanupSpec() {
        GroovyExtensionMetaClassRegistry.removeMetaClasses()

        removeAllNodes()
    }

    /**
     * Refresh the shared resource resolver before each test run.
     */
    def setup() {
        //resourceResolverInternal = resourceResolverFactoryInternal.getAdministrativeResourceResolver(null)
        resourceResolverInternal.refresh()
    }

    /**
     * Remove all non-system nodes to cleanup any test data.  This method would typically be called from a test fixture
     * method to cleanup content before the entire specification has been executed.
     */
    void removeAllNodes() {
        session.rootNode.nodes.findAll { Node node -> !SYSTEM_NODE_NAMES.contains(node.name) }*.remove()
        session.save()
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
     * Register an adapter for this spec.
     *
     * @param adaptableType
     * @param adapterType target adapter type
     * @param closure
     */
    void addAdapter(Class adaptableType, Class adapterType, Closure closure) {
        adapterManager.addAdapter(adaptableType, adapterType, closure)
    }

    /**
     * Register an adapter factory for this spec.
     *
     * @param adapterFactory adapter factory instance
     */
    void addAdapterFactory(AdapterFactory adapterFactory) {
        adapterManager.addAdapterFactory(adapterFactory)
    }

    // accessors for shared instances

    /**
     * @return Sling context
     */
    ProsperSlingContext getSlingContext() {
        slingContextInternal
    }

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
     * @return admin resource resolver
     */
    ResourceResolver getResourceResolver() {
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
     * Get the Node for a path.
     *
     * @param path valid JCR Node path
     * @return node for given path
     */
    Node getNode(String path) {
        session.getNode(path)
    }

    /**
     * Get the Resource for a path.
     *
     * @param path valid Resource path
     * @return resource for given path or null
     */
    Resource getResource(String path) {
        resourceResolver.getResource(path)
    }

    /**
     * Get the Page for a path.
     *
     * @param path valid Page path
     * @return Page for given path or null
     */
    Page getPage(String path) {
        pageManager.getPage(path)
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

    // assertions

    /**
     * Assert that a node exists for the given path.
     *
     * @param path node path
     */
    void assertNodeExists(String path) {
        assert session.nodeExists(path)
    }

    /**
     * Assert that a node exists for the given path and node type.
     *
     * @param path node path
     * @param primaryNodeTypeName primary node type name
     */
    void assertNodeExists(String path, String primaryNodeTypeName) {
        assert session.nodeExists(path)

        def node = session.getNode(path)

        assert node.primaryNodeType.name == primaryNodeTypeName
    }

    /**
     * Assert that a node exists for the given path and property map.
     *
     * @param path node path
     * @param properties map of property names and values to verify for the node
     */
    void assertNodeExists(String path, Map<String, Object> properties) {
        assert session.nodeExists(path)

        def node = session.getNode(path)

        properties.each { name, value ->
            assert node.get(name) == value
        }
    }

    /**
     * Assert that a node exists for the given path, node type, and property map.
     *
     * @param path node path
     * @param primaryNodeTypeName primary node type name
     * @param properties map of property names and values to verify for the node
     */
    void assertNodeExists(String path, String primaryNodeTypeName, Map<String, Object> properties) {
        assert session.nodeExists(path)

        def node = session.getNode(path)

        assert node.primaryNodeType.name == primaryNodeTypeName

        properties.each { name, value ->
            assert node.get(name) == value
        }
    }

    /**
     * Assert that a page exists for the given path.
     *
     * @param path page path
     */
    void assertPageExists(String path) {
        assert session.nodeExists(path)

        def pageNode = session.getNode(path)

        assert pageNode.primaryNodeType.name == NameConstants.NT_PAGE
        assert pageNode.hasNode(JcrConstants.JCR_CONTENT)
    }

    /**
     * Assert that a page exists for the given path and contains the given properties.
     *
     * @param path page path
     * @param properties map of property names and values to verify for the page
     */
    void assertPageExists(String path, Map<String, Object> properties) {
        assertPageExists(path)

        def contentNode = session.getNode(path).getNode(JcrConstants.JCR_CONTENT)

        properties.each { name, value ->
            assert contentNode.get(name) == value
        }
    }

    // internals

    private void registerNodeTypes() {
        registerDefaultNodeTypes()

        if (this.class.isAnnotationPresent(NodeTypes)) {
            def cndResourcePaths = this.class.getAnnotation(NodeTypes).value() as List

            registerNodeTypes(cndResourcePaths)
        }
    }

    private void registerDefaultNodeTypes() {
        def cndResourcePaths = NODE_TYPES.collect { type -> "/SLING-INF/nodetypes/${type}.cnd" }

        registerNodeTypes(cndResourcePaths)
    }

    private void registerNodeTypes(List<String> cndResourcePaths) {
        cndResourcePaths.each { cndResourcePath ->
            this.class.getResourceAsStream(cndResourcePath).withStream { stream ->
                RepositoryUtil.registerNodeType(session, stream)
            }
        }
    }

    private void addAdapters() {
        def repository = slingContext.getService(SlingRepository)

        addAdapterFactory(new ProsperAdapterFactory(repository, session))

        addAdapterFactories().each { adapterFactory ->
            addAdapterFactory(adapterFactory)
        }

        addResourceAdapters().each { Map.Entry<Class, Closure> resourceAdapter ->
            addAdapter(Resource, resourceAdapter.key, resourceAdapter.value)
        }

        addResourceResolverAdapters().each { Map.Entry<Class, Closure> resourceResolverAdapter ->
            addAdapter(ResourceResolver, resourceResolverAdapter.key, resourceResolverAdapter.value)
        }

        addRequestAdapters().each { Map.Entry<Class, Closure> requestAdapter ->
            addAdapter(SlingHttpServletRequest, requestAdapter.key, requestAdapter.value)
        }
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