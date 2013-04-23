package com.citytechinc.cq.testing

import groovy.transform.Synchronized
import org.apache.sling.commons.testing.jcr.RepositoryUtil
import spock.lang.Shared
import spock.lang.Specification

/**
 * Abstract Spock specification for JCR-based testing.
 */
abstract class AbstractRepositorySpec extends Specification {

    public static final def SYSTEM_NODE_NAMES = ["jcr:system", "rep:policy"]

    static final def NODE_TYPES = ["sling", "replication", "tagging", "core", "dam", "vlt"]

    static def repository

    @Shared session

    def setupSpec() {
        session = getRepository().loginAdministrative(null)
    }

    def cleanupSpec() {
        session.rootNode.nodes.findAll { !SYSTEM_NODE_NAMES.contains(it.name) }*.remove()
        session.save()
        session.logout()
    }

    @Synchronized
    def getRepository() {
        if (!repository) {
            RepositoryUtil.startRepository()

            repository = RepositoryUtil.getRepository()

            registerNodeTypes()

            addShutdownHook {
                RepositoryUtil.stopRepository()
            }
        }

        repository
    }

    def registerNodeTypes() {
        session = getRepository().loginAdministrative(null)

        NODE_TYPES.each { type ->
            this.class.getResourceAsStream("/SLING-INF/nodetypes/${type}.cnd").withStream { stream ->
                RepositoryUtil.registerNodeType(session, stream)
            }
        }

        session.logout()
    }
}