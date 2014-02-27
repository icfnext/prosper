package com.citytechinc.aem.spock.specs

import com.citytechinc.aem.groovy.extension.builders.NodeBuilder
import com.citytechinc.aem.groovy.extension.builders.PageBuilder
import com.citytechinc.aem.groovy.extension.metaclass.GroovyExtensionMetaClassRegistry
import spock.lang.Shared

abstract class AbstractAemSpec extends AbstractSlingRepositorySpec {

    @Shared nodeBuilder

    @Shared pageBuilder

    def setupSpec() {
        GroovyExtensionMetaClassRegistry.registerMetaClasses()

        nodeBuilder = new NodeBuilder(session)
        pageBuilder = new PageBuilder(session)
    }
}