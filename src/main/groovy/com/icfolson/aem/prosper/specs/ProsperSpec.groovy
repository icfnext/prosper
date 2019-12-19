package com.icfolson.aem.prosper.specs

import com.day.cq.commons.jcr.JcrConstants
import com.day.cq.wcm.api.NameConstants
import com.day.cq.wcm.api.Page
import com.day.cq.wcm.api.PageManager
import com.icfolson.aem.groovy.extension.api.MetaClassExtensionProvider
import com.icfolson.aem.groovy.extension.builders.NodeBuilder
import com.icfolson.aem.groovy.extension.builders.PageBuilder
import com.icfolson.aem.groovy.extension.services.impl.DefaultExtensionService
import com.icfolson.aem.groovy.extension.services.impl.DefaultMetaClassExtensionProvider
import com.icfolson.aem.prosper.annotations.ModelSpec
import com.icfolson.aem.prosper.annotations.NodeTypes
import com.icfolson.aem.prosper.builders.RequestBuilder
import com.icfolson.aem.prosper.builders.ResponseBuilder
import com.icfolson.aem.prosper.context.ProsperSlingContext
import com.icfolson.aem.prosper.context.SlingContextProvider
import com.icfolson.aem.prosper.importer.ContentImporter
import io.wcm.testing.mock.aem.junit.AemContext
import io.wcm.testing.mock.aem.junit.AemContextBuilder
import org.apache.sling.api.resource.Resource
import org.apache.sling.api.resource.ResourceResolver
import org.apache.sling.testing.mock.sling.NodeTypeDefinitionScanner
import org.junit.ClassRule
import spock.lang.Shared
import spock.lang.Specification

import javax.jcr.Node
import javax.jcr.Session

import static org.apache.sling.testing.mock.sling.ResourceResolverType.JCR_OAK

/**
 * Spock specification for AEM testing that includes a Sling context for mock repository operations and a simulated
 * OSGi environment for registering services and adapters.
 */
abstract class ProsperSpec extends Specification {

    /**
     * Jackrabbit Oak system node names.  Will be ignored when cleaning up test content.
     */
    private static final List<String> SYSTEM_NODE_NAMES = [
        "jcr:system",
        "rep:security",
        "oak:index"
    ]

    /**
     * Default node types registered after repository creation.
     */
    private static final List<String> DEFAULT_NODE_TYPES = [
        "sling",
        "replication",
        "tagging",
        "core",
        "dam",
        "vlt",
        "widgets"
    ]

    @ClassRule
    @Shared
    private ProsperSlingContext slingContextProvider = new ProsperSlingContext(buildAemContext())

    // global fixtures

    /**
     * Create an administrative JCR session with content builders, register Sling adapters,
     * and instantiate a mock resource resolver.
     */
    def setupSpec() {
        slingContext.registerService(MetaClassExtensionProvider, new DefaultMetaClassExtensionProvider())
        slingContext.registerInjectActivateService(new DefaultExtensionService())

        registerNodeTypes()
        importVaultContent()
        registerSlingModels()
    }

    /**
     * Build the AEM context.  Override to build a custom AEM context rule.
     *
     * @return AEM context to supply the Prosper Sling context
     */
    AemContext buildAemContext() {
        new AemContextBuilder(JCR_OAK).build()
    }

    /**
     * Remove test content.
     */
    def cleanupSpec() {
        removeAllNodes()
    }

    /**
     * Refresh the shared resource resolver before each test run.
     */
    def setup() {
        resourceResolver.refresh()
    }

    /**
     * Remove all non-system nodes to cleanup any test data.  This method would typically be called from a test fixture
     * method to cleanup content before the entire specification has been executed.
     */
    void removeAllNodes() {
        session.rootNode.nodes.findAll { Node node -> !SYSTEM_NODE_NAMES.contains(node.name) }*.remove()
        session.save()
    }

    // accessors for shared instances

    SlingContextProvider getSlingContext() {
        slingContextProvider
    }

    /**
     * @return admin session
     */
    Session getSession() {
        resourceResolver.adaptTo(Session)
    }

    /**
     * @return JCR node builder
     */
    NodeBuilder getNodeBuilder() {
        new NodeBuilder(session)
    }

    /**
     * @return CQ page builder
     */
    PageBuilder getPageBuilder() {
        new PageBuilder(session)
    }

    /**
     * @return admin resource resolver
     */
    ResourceResolver getResourceResolver() {
        slingContextProvider.resourceResolver
    }

    /**
     * @return CQ page manager
     */
    PageManager getPageManager() {
        resourceResolver.adaptTo(PageManager)
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
        new RequestBuilder(resourceResolver, slingContext.bundleContext)
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
            def cndResourcePaths = (this.class.getAnnotation(NodeTypes).value() as List).collect { path ->
                path.startsWith("/") ? path.substring(1) : path
            }

            registerNodeTypes(cndResourcePaths)
        }
    }

    private void registerDefaultNodeTypes() {
        def cndResourcePaths = DEFAULT_NODE_TYPES.collect { type -> "SLING-INF/nodetypes/${type}.cnd" as String }

        registerNodeTypes(cndResourcePaths)
    }

    private void registerNodeTypes(List<String> cndResourcePaths) {
        NodeTypeDefinitionScanner.get().register(session, cndResourcePaths, JCR_OAK.nodeTypeMode)
    }

    private void importVaultContent() {
        new ContentImporter(this).importVaultContent()
    }

    private void registerSlingModels() {
        if (this.class.isAnnotationPresent(ModelSpec)) {
            slingContextProvider.addModelsForPackage(this.class.getPackage().name)

            def additionalPackages = this.class.getAnnotation(ModelSpec).additionalPackages()

            if (additionalPackages) {
                slingContextProvider.addModelsForPackage(additionalPackages)
            }
        }
    }
}