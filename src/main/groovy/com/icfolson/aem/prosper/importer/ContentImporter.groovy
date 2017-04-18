package com.icfolson.aem.prosper.importer

import com.icfolson.aem.prosper.annotations.ContentFilterRuleType
import com.icfolson.aem.prosper.annotations.ContentFilters
import com.icfolson.aem.prosper.annotations.SkipContentImport
import com.icfolson.aem.prosper.specs.ProsperSpec
import groovy.transform.TupleConstructor
import org.apache.jackrabbit.vault.fs.api.PathFilterSet
import org.apache.jackrabbit.vault.fs.api.WorkspaceFilter
import org.apache.jackrabbit.vault.fs.config.DefaultWorkspaceFilter
import org.apache.jackrabbit.vault.fs.filter.DefaultPathFilter
import org.apache.jackrabbit.vault.fs.io.FileArchive
import org.apache.jackrabbit.vault.fs.io.ImportOptions
import org.apache.jackrabbit.vault.fs.io.Importer

@TupleConstructor
class ContentImporter {

    ProsperSpec spec

    /**
     * Import content from a local filter definition.
     */
    void importVaultContent() {
        if (!spec.class.isAnnotationPresent(SkipContentImport)) {
            def contentRootUrl = this.class.getResource("/SLING-INF/content")

            if (contentRootUrl && "file".equalsIgnoreCase(contentRootUrl.protocol) && !contentRootUrl.host) {
                def contentArchive = new FileArchive(new File(contentRootUrl.file))

                try {
                    contentArchive.open(false)
                    importer.run(contentArchive, spec.session.rootNode)
                } finally {
                    contentArchive.close()
                }
            }
        }
    }

    private Importer getImporter() {
        if (spec.class.isAnnotationPresent(ContentFilters)) {
            def contentImportOptions = new ImportOptions()

            contentImportOptions.filter = workspaceFilter

            new Importer(contentImportOptions)
        } else {
            new Importer()
        }
    }

    private WorkspaceFilter getWorkspaceFilter() {
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
