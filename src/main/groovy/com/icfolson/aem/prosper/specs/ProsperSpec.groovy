package com.icfolson.aem.prosper.specs

import com.citytechinc.aem.groovy.extension.builders.NodeBuilder
import com.citytechinc.aem.groovy.extension.builders.PageBuilder
import com.citytechinc.aem.groovy.extension.metaclass.GroovyExtensionMetaClassRegistry
import com.day.cq.commons.jcr.JcrConstants
import com.day.cq.wcm.api.NameConstants
import com.day.cq.wcm.api.Page
import com.day.cq.wcm.api.PageManager
import com.icfolson.aem.prosper.annotations.ContentFilterRuleType
import com.icfolson.aem.prosper.annotations.ContentFilters
import com.icfolson.aem.prosper.annotations.NodeTypes
import com.icfolson.aem.prosper.annotations.SkipContentImport
import com.icfolson.aem.prosper.builders.RequestBuilder
import com.icfolson.aem.prosper.builders.ResponseBuilder
import com.icfolson.aem.prosper.context.ProsperSlingContext
import com.icfolson.aem.prosper.context.SlingContextProvider
import org.apache.jackrabbit.vault.fs.api.PathFilterSet
import org.apache.jackrabbit.vault.fs.api.WorkspaceFilter
import org.apache.jackrabbit.vault.fs.config.DefaultWorkspaceFilter
import org.apache.jackrabbit.vault.fs.filter.DefaultPathFilter
import org.apache.jackrabbit.vault.fs.io.FileArchive
import org.apache.jackrabbit.vault.fs.io.ImportOptions
import org.apache.jackrabbit.vault.fs.io.Importer
import org.apache.sling.api.resource.Resource
import org.apache.sling.api.resource.ResourceResolver
import org.apache.sling.testing.mock.sling.NodeTypeDefinitionScanner
import org.apache.sling.testing.mock.sling.ResourceResolverType
import spock.lang.Shared
import spock.lang.Specification

import javax.jcr.Node
import javax.jcr.Session

/**
 * Spock specification for AEM testing that includes a Sling context for mock repository operations and a simulated
 * OSGi environment for registering services and adapters.
 */
abstract class ProsperSpec extends Specification {

    /**
     * Jackrabbit Oak system node names.  Will be ignored when cleaning up test content.
     */
    private static final def SYSTEM_NODE_NAMES = ["jcr:system", "rep:security", "oak:index"]

    /**
     * Default node types registered after repository creation.
     */
    private static final def DEFAULT_NODE_TYPES = ["sling", "replication", "tagging", "core", "dam", "vlt", "widgets"]

    @Shared
    private ProsperSlingContext slingContextInternal = new ProsperSlingContext()

    // global fixtures

    /**
     * Create an administrative JCR session with content builders, register Sling adapters,
     * and instantiate a mock resource resolver.
     */
    def setupSpec() {
        GroovyExtensionMetaClassRegistry.registerMetaClasses()

        slingContextInternal.setup()

        registerNodeTypes()
        importVaultContent()
    }

    /**
     * Remove Groovy metaclasses and test content.
     */
    def cleanupSpec() {
        GroovyExtensionMetaClassRegistry.removeMetaClasses()

        removeAllNodes()

        slingContextInternal.cleanup()
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
        slingContextInternal
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
        slingContext.resourceResolver
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
        NodeTypeDefinitionScanner.get().register(session, cndResourcePaths, ResourceResolverType.JCR_OAK.nodeTypeMode)
    }

    private void importVaultContent() {
        if (!this.class.isAnnotationPresent(SkipContentImport)) {
            def contentRootUrl = this.class.getResource("/SLING-INF/content")

            if (contentRootUrl && "file".equalsIgnoreCase(contentRootUrl.protocol) && !contentRootUrl.host) {
                def contentImporter = buildImporter()
                def contentArchive = new FileArchive(new File(contentRootUrl.file))

                try {
                    contentArchive.open(false)
                    contentImporter.run(contentArchive, session.rootNode)
                } finally {
                    contentArchive.close()
                }
            }
        }
    }

    private Importer buildImporter() {
        def importer

        this.class.annotations

        if (this.class.isAnnotationPresent(ContentFilters)) {
            def contentImportOptions = new ImportOptions()

            contentImportOptions.filter = buildWorkspaceFilter()
            importer = new Importer(contentImportOptions)
        } else {
            importer = new Importer()
        }

        importer
    }

    private WorkspaceFilter buildWorkspaceFilter() {
        def contentImportFilter = new DefaultWorkspaceFilter()

        def filterDefinitions = this.class.getAnnotation(ContentFilters)

        if (filterDefinitions.xml()) {
            contentImportFilter.load(this.class.getResourceAsStream(filterDefinitions.xml()))
        }

        filterDefinitions.filters().each { filterDefinition ->
            def pathFilterSet = new PathFilterSet(filterDefinition.root())

            pathFilterSet.importMode = filterDefinition.mode()

            filterDefinition.rules().each { rule ->
                def pathFilter = new DefaultPathFilter(rule.pattern())

                if (rule.type() == ContentFilterRuleType.INCLUDE) {
                    pathFilterSet.addInclude(pathFilter)
                } else if (rule.type() == ContentFilterRuleType.EXCLUDE) {
                    pathFilterSet.addExclude(pathFilter)
                }
            }

            contentImportFilter.add(pathFilterSet)
        }

        contentImportFilter
    }
}