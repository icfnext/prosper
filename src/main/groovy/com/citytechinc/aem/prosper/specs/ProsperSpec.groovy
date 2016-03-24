package com.citytechinc.aem.prosper.specs

import com.citytechinc.aem.groovy.extension.builders.NodeBuilder
import com.citytechinc.aem.groovy.extension.builders.PageBuilder
import com.citytechinc.aem.groovy.extension.metaclass.GroovyExtensionMetaClassRegistry
import com.citytechinc.aem.prosper.annotations.NodeTypes
import com.citytechinc.aem.prosper.builders.RequestBuilder
import com.citytechinc.aem.prosper.builders.ResponseBuilder
import com.citytechinc.aem.prosper.context.ProsperSlingContext
import com.citytechinc.aem.prosper.context.SlingContextProvider
import com.citytechinc.aem.prosper.importer.ContentImporter
import com.day.cq.commons.jcr.JcrConstants
import com.day.cq.wcm.api.NameConstants
import com.day.cq.wcm.api.Page
import com.day.cq.wcm.api.PageManager
import org.apache.sling.api.adapter.AdapterFactory
import org.apache.sling.api.resource.Resource
import org.apache.sling.api.resource.ResourceResolver
import org.apache.sling.commons.testing.jcr.RepositoryUtil
import org.apache.sling.models.spi.Injector
import org.osgi.framework.BundleContext
import spock.lang.AutoCleanup
import spock.lang.Shared
import spock.lang.Specification

import javax.jcr.Node
import javax.jcr.Session

/**
 * Spock specification for AEM testing that includes a Sling <code>ResourceResolver</code>, content builders, and
 * adapter registration capabilities.
 */
abstract class ProsperSpec extends Specification implements SlingContextProvider {

    private static final def SYSTEM_NODE_NAMES = ["jcr:system", "rep:security", "oak:index"]

    private static final def NODE_TYPES = ["sling", "replication", "tagging", "core", "dam", "vlt", "widgets"]

    @Shared
    private ProsperSlingContext slingContext = new ProsperSlingContext()

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

        resourceResolverInternal = slingContext.resourceResolver
        sessionInternal = resourceResolver.adaptTo(Session)
        nodeBuilderInternal = new NodeBuilder(session)
        pageBuilderInternal = new PageBuilder(session)
        pageManagerInternal = resourceResolver.adaptTo(PageManager)

        registerNodeTypes()

        new ContentImporter(this).importVaultContent()
    }

    def cleanupSpec() {
        GroovyExtensionMetaClassRegistry.removeMetaClasses()

        removeAllNodes()
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
        session.rootNode.nodes.findAll { Node node -> !SYSTEM_NODE_NAMES.contains(node.name) }*.remove()
        session.save()
    }

    // expose selected methods from sling/OSGi context

    @Override
    BundleContext getBundleContext() {
        slingContext.bundleContext()
    }

    @Override
    def <T> T registerService(T service) {
        slingContext.registerService(service)
    }

    @Override
    def <T> T registerService(Class<T> serviceClass, T service) {
        slingContext.registerService(serviceClass, service)
    }

    @Override
    def <T> T registerService(Class<T> serviceClass, T service, Map<String, Object> properties) {
        slingContext.registerService(serviceClass, service, properties)
    }

    @Override
    def <T> T registerInjectActivateService(T service) {
        slingContext.registerInjectActivateService(service)
    }

    @Override
    def <T> T registerInjectActivateService(T service, Map<String, Object> properties) {
        slingContext.registerInjectActivateService(service, properties)
    }

    @Override
    void registerResourceAdapter(Class adapterType, Closure closure) {
        slingContext.registerResourceAdapter(adapterType, closure)
    }

    @Override
    void registerResourceResolverAdapter(Class adapterType, Closure closure) {
        slingContext.registerResourceResolverAdapter(adapterType, closure)
    }

    @Override
    void registerRequestAdapter(Class adapterType, Closure closure) {
        slingContext.registerRequestAdapter(adapterType, closure)
    }

    @Override
    void registerAdapter(Class adaptableType, Class adapterType, Closure closure) {
        slingContext.registerAdapter(adaptableType, adapterType, closure)
    }

    @Override
    void registerAdapterFactory(AdapterFactory adapterFactory, String[] adaptableClasses, String[] adapterClasses) {
        slingContext.registerAdapterFactory(adapterFactory, adaptableClasses, adapterClasses)
    }

    @Override
    def <ServiceType> ServiceType getService(Class<ServiceType> serviceType) {
        slingContext.getService(serviceType)
    }

    @Override
    def <ServiceType> ServiceType[] getServices(Class<ServiceType> serviceType, String filter) {
        slingContext.getServices(serviceType, filter)
    }

    @Override
    void registerInjector(Injector injector, Integer serviceRanking) {
        slingContext.registerInjector(injector, serviceRanking)
    }

    @Override
    void addModelsForPackage(String packageName) {
        slingContext.addModelsForPackage(packageName)
    }

    @Override
    void runMode(String... runModes) {
        slingContext.runMode(runModes)
    }

    // accessors for shared instances

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
        new RequestBuilder(resourceResolver)
    }

    /**
     * Get a response builder.
     *
     * @return builder
     */
    ResponseBuilder getResponseBuilder() {
        new ResponseBuilder()
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
}