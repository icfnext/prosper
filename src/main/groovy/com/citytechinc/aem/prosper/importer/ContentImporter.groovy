package com.citytechinc.aem.prosper.importer

import com.citytechinc.aem.prosper.annotations.ContentFilterRuleType
import com.citytechinc.aem.prosper.annotations.ContentFilters
import com.citytechinc.aem.prosper.annotations.SkipContentImport
import com.citytechinc.aem.prosper.specs.ProsperSpec
import org.apache.jackrabbit.vault.fs.api.PathFilterSet
import org.apache.jackrabbit.vault.fs.api.WorkspaceFilter
import org.apache.jackrabbit.vault.fs.config.DefaultWorkspaceFilter
import org.apache.jackrabbit.vault.fs.filter.DefaultPathFilter
import org.apache.jackrabbit.vault.fs.io.FileArchive
import org.apache.jackrabbit.vault.fs.io.ImportOptions
import org.apache.jackrabbit.vault.fs.io.Importer

class ContentImporter {

    /**
     * Import content from a local filter definition.
     *
     * @param spec current test spec
     */
    static void importVaultContent(ProsperSpec spec) {
        if (!spec.class.isAnnotationPresent(SkipContentImport)) {
            def contentRootUrl = this.class.getResource("/SLING-INF/content")

            if (contentRootUrl && "file".equalsIgnoreCase(contentRootUrl.protocol) && !contentRootUrl.host) {
                def contentImporter = buildContentImporter(spec)
                def contentArchive = new FileArchive(new File(contentRootUrl.file))

                try {
                    contentArchive.open(false)
                    contentImporter.run(contentArchive, spec.session.rootNode)
                } finally {
                    contentArchive.close()
                }
            }
        }
    }

    private static Importer buildContentImporter(ProsperSpec spec) {
        def contentImporter

        if (spec.class.isAnnotationPresent(ContentFilters)) {
            def contentImportOptions = new ImportOptions()

            contentImportOptions.filter = buildContentImportFilter(spec)
            contentImporter = new Importer(contentImportOptions)
        } else {
            contentImporter = new Importer()
        }

        contentImporter
    }

    private static WorkspaceFilter buildContentImportFilter(ProsperSpec spec) {
        def contentImportFilter = new DefaultWorkspaceFilter()

        def filterDefinitions = spec.class.getAnnotation(ContentFilters)

        if (filterDefinitions.xml()) {
            contentImportFilter.load(spec.class.getResourceAsStream(filterDefinitions.xml()))
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
