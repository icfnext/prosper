package com.citytechinc.aem.prosper.specs

import com.citytechinc.aem.groovy.extension.builders.NodeBuilder
import com.citytechinc.aem.groovy.extension.builders.PageBuilder
import com.citytechinc.aem.groovy.extension.metaclass.GroovyExtensionMetaClassRegistry
import com.citytechinc.aem.prosper.annotations.ContentFilterRuleType
import com.citytechinc.aem.prosper.annotations.ContentFilters
import com.citytechinc.aem.prosper.annotations.NodeTypes
import com.citytechinc.aem.prosper.annotations.SkipContentImport
import com.citytechinc.aem.prosper.builders.RequestBuilder
import com.citytechinc.aem.prosper.builders.ResponseBuilder
import com.citytechinc.aem.prosper.mixins.ProsperMixin
import com.citytechinc.aem.prosper.mocks.adapter.TestAdaptable
import com.citytechinc.aem.prosper.mocks.resource.MockResourceResolver
import com.citytechinc.aem.prosper.mocks.resource.TestResourceResolver
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
abstract class ProsperSpec extends Specification implements TestAdaptable, ProsperAsserts {

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

        registerNodeTypes()
        addAdapters()

        resourceResolverInternal = new MockResourceResolver(sessionInternal, resourceResolverAdapters,
            resourceAdapters, adapterFactories)
        pageManagerInternal = resourceResolver.adaptTo(PageManager)

        addMixins()
        importVaultContent()
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
                def contentImporter = buildContentImporter()
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

    private Importer buildContentImporter() {
        def contentImporter

        if (this.class.isAnnotationPresent(ContentFilters)) {
            def contentImportOptions = new ImportOptions()

            contentImportOptions.filter = buildContentImportFilter(this.class.getAnnotation(ContentFilters))
            contentImporter = new Importer(contentImportOptions)
        } else {
            contentImporter = new Importer()
        }

        contentImporter
    }

    private WorkspaceFilter buildContentImportFilter(ContentFilters filterDefinitions) {
        def contentImportFilter = new DefaultWorkspaceFilter()

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