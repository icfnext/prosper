package com.citytechinc.aem.prosper.specs

import com.citytechinc.aem.groovy.extension.builders.NodeBuilder
import com.citytechinc.aem.groovy.extension.builders.PageBuilder
import com.citytechinc.aem.groovy.extension.metaclass.GroovyExtensionMetaClassRegistry
import com.citytechinc.aem.prosper.annotations.ContentFilterRuleType
import com.citytechinc.aem.prosper.annotations.ContentFilters
import com.citytechinc.aem.prosper.annotations.SkipContentImport
import com.citytechinc.aem.prosper.builders.RequestBuilder
import com.citytechinc.aem.prosper.builders.ResponseBuilder
import com.citytechinc.aem.prosper.mixins.ProsperMixin
import com.citytechinc.aem.prosper.mocks.adapter.TestAdaptable
import com.citytechinc.aem.prosper.mocks.resource.MockResourceResolver
import com.citytechinc.aem.prosper.mocks.resource.TestResourceResolver
import com.day.cq.commons.jcr.JcrConstants
import com.day.cq.replication.Replicator
import com.day.cq.tagging.TagManager
import com.day.cq.tagging.impl.JcrTagManagerImpl
import com.day.cq.wcm.api.NameConstants
import com.day.cq.wcm.api.Page
import com.day.cq.wcm.api.PageManager
import com.day.cq.wcm.core.impl.PageImpl
import com.day.cq.wcm.core.impl.PageManagerFactoryImpl
import groovy.transform.Synchronized
import org.apache.jackrabbit.vault.fs.api.PathFilterSet
import org.apache.jackrabbit.vault.fs.api.WorkspaceFilter
import org.apache.jackrabbit.vault.fs.config.DefaultWorkspaceFilter
import org.apache.jackrabbit.vault.fs.filter.DefaultPathFilter
import org.apache.jackrabbit.vault.fs.io.FileArchive
import org.apache.jackrabbit.vault.fs.io.ImportOptions
import org.apache.jackrabbit.vault.fs.io.Importer
import org.apache.sling.api.adapter.AdapterFactory
import org.apache.sling.api.resource.Resource
import org.apache.sling.api.resource.ResourceResolver
import org.apache.sling.commons.json.jcr.JsonItemWriter
import org.apache.sling.commons.testing.jcr.RepositoryUtil
import org.apache.sling.jcr.api.SlingRepository
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
abstract class ProsperSpec extends Specification implements TestAdaptable {

    private static final def SYSTEM_NODE_NAMES = ["jcr:system", "rep:policy"]

    private static final def NODE_TYPES = ["sling", "replication", "tagging", "core", "dam", "vlt", "widgets"]

    private static SlingRepository repositoryInternal

    @Shared
    private Session sessionInternal

    @Shared
    private TestResourceResolver resourceResolverInternal

    @Shared
    private PageManager pageManagerInternal

    @Shared
    private NodeBuilder nodeBuilderInternal

    @Shared
    private PageBuilder pageBuilderInternal

    @Shared
    private List<AdapterFactory> adapterFactories = []

    @Shared
    private Map<Class, Closure> resourceResolverAdapters = [:]

    @Shared
    private Map<Class, Closure> resourceAdapters = [:]

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

        registerCustomNodeTypes()
        addAdapters()

        resourceResolverInternal = new MockResourceResolver(sessionInternal, resourceResolverAdapters,
            resourceAdapters, adapterFactories)
        pageManagerInternal = resourceResolver.adaptTo(PageManager)

        addMixins()
        importVaultContent()

        new File("/Users/mark/Downloads/out.json").withWriter {
            new JsonItemWriter(null).dump(session.rootNode, it, -1, true)
        }
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
     * Add JCR namespaces and node types based on any number of CND file input streams.  Specs should override this
     * method to add CND files to be registered at runtime.  Note that the <code>InputStream</code> is closed
     * automatically after the CND file is consumed.
     *
     * @return list of InputStreams to add
     */
    List<InputStream> addCndInputStreams() {
        Collections.emptyList()
    }

    /**
     * Add JCR namespaces and node types by providing a list of paths to CND files.  Specs should override this
     * method to add CND files to be registered at runtime.
     *
     * @return list of paths to CND file resources
     */
    List<String> addNodeTypes() {
        Collections.emptyList()
    }

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
     * Add a <code>Resource</code> adapter for the current specification.  This method can be called as many times as
     * necessary in a feature method to add adapters for the current test.
     *
     * @param adapterType adapter class
     * @param closure closure with a single <code>Resource</code> that returns an instance of the adapter class
     */
    @Override
    void addResourceAdapter(Class adapterType, Closure closure) {
        resourceResolverInternal.addResourceAdapter(adapterType, closure)
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
        resourceResolverInternal.addResourceResolverAdapter(adapterType, closure)
    }

    // accessors for shared instances

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
     * @return admin resource resolver
     */
    TestResourceResolver getResourceResolver() {
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
        new RequestBuilder(resourceResolverInternal)
    }

    /**
     * Get a response builder.
     *
     * @return builder
     */
    ResponseBuilder getResponseBuilder() {
        new ResponseBuilder()
    }

    // assertion methods for use in Spock specification 'expect' blocks

    /**
     * Assert that a node exists for the given path.
     *
     * @param path node path
     */
    void assertNodeExists(String path) {
        assert sessionInternal.nodeExists(path)
    }

    /**
     * Assert that a node exists for the given path and node type.
     *
     * @param path node path
     * @param primaryNodeTypeName primary node type name
     */
    void assertNodeExists(String path, String primaryNodeTypeName) {
        assert sessionInternal.nodeExists(path)

        def node = sessionInternal.getNode(path)

        assert node.primaryNodeType.name == primaryNodeTypeName
    }

    /**
     * Assert that a node exists for the given path and property map.
     *
     * @param path node path
     * @param properties map of property names and values to verify for the node
     */
    void assertNodeExists(String path, Map<String, Object> properties) {
        assert sessionInternal.nodeExists(path)

        def node = sessionInternal.getNode(path)

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
        assert sessionInternal.nodeExists(path)

        def node = sessionInternal.getNode(path)

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
        assert sessionInternal.nodeExists(path)

        def pageNode = sessionInternal.getNode(path)

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

        def contentNode = sessionInternal.getNode(path).getNode(JcrConstants.JCR_CONTENT)

        properties.each { name, value ->
            assert contentNode.get(name) == value
        }
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
            NODE_TYPES.each { type ->
                this.class.getResourceAsStream("/SLING-INF/nodetypes/${type}.cnd").withStream { stream ->
                    RepositoryUtil.registerNodeType(session, stream)
                }
            }
        } finally {
            session.logout()
        }
    }

    private void registerCustomNodeTypes() {
        addCndInputStreams().each {
            it.withStream { stream ->
                RepositoryUtil.registerNodeType(sessionInternal, stream)
            }
        }

        addNodeTypes().each { type ->
            this.class.getResourceAsStream(type).withStream { stream ->
                RepositoryUtil.registerNodeType(sessionInternal, stream)
            }
        }
    }

    private void addAdapters() {
        adapterFactories.addAll(addAdapterFactories())

        addDefaultResourceAdapters()
        addDefaultResourceResolverAdapters()

        resourceAdapters.putAll(addResourceAdapters())
        resourceResolverAdapters.putAll(addResourceResolverAdapters())
    }

    private void addDefaultResourceAdapters() {
        resourceAdapters[Page.class] = { Resource resource ->
            NameConstants.NT_PAGE == resource.resourceType ? new PageImpl(resource) : null
        }
    }

    private void addDefaultResourceResolverAdapters() {
        resourceResolverAdapters[PageManager.class] = { ResourceResolver resourceResolver ->
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
        }

        resourceResolverAdapters[TagManager.class] = { ResourceResolver resourceResolver ->
            new JcrTagManagerImpl(resourceResolver, null, null, "/etc/tags")
        }

        resourceResolverAdapters[Session.class] = { sessionInternal }
    }

    private void addMixins() {
        findAllMixins(this.class).each { mixin ->
            def instance = mixin.type.getConstructor(ResourceResolver).newInstance(resourceResolver)

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

    private void importVaultContent() {
        if (!this.class.isAnnotationPresent(SkipContentImport)) {
            def contentRootUrl = this.class.getResource("/SLING-INF/content")

            if (contentRootUrl && "file".equalsIgnoreCase(contentRootUrl.protocol) && !contentRootUrl.host) {
                def contentArchive = new FileArchive(new File(contentRootUrl.file))

                try {
                    contentArchive.open(false)
                    buildContentImporter().run(contentArchive, session.rootNode)
                } finally {
                    contentArchive.close()
                }
            }
        }
    }

    private Importer buildContentImporter() {
        def importer

        if (this.class.isAnnotationPresent(ContentFilters)) {
            def importOptions = new ImportOptions()

            importOptions.filter = buildContentImportFilter(this.class.getAnnotation(ContentFilters))
            importer = new Importer(importOptions)

            println importer.options.filter.sourceAsString
        } else {
            importer = new Importer()
        }

        importer
    }

    private WorkspaceFilter buildContentImportFilter(ContentFilters filterDefinitions) {
        def filter = new DefaultWorkspaceFilter()

        if (filterDefinitions.xml()) {
            filter.load(this.class.getResourceAsStream(filterDefinitions.xml()))
        }

        if (filterDefinitions.filters()) {
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

                filter.add(pathFilterSet)
            }
        }

        filter
    }
}