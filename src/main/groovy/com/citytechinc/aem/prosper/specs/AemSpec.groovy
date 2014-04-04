package com.citytechinc.aem.prosper.specs

import groovy.transform.Synchronized
import org.apache.sling.commons.testing.jcr.RepositoryUtil
import org.apache.sling.jcr.api.SlingRepository
import spock.lang.Shared
import spock.lang.Specification

import javax.jcr.Node
import javax.jcr.Session

/**
 * Basic AEM spec that includes a transient JCR session.
 */
class AemSpec extends Specification {

    private static final def SYSTEM_NODE_NAMES = ["jcr:system", "rep:policy"]

    private static final def NODE_TYPES = ["sling", "replication", "tagging", "core", "dam", "vlt"]

    private static SlingRepository repository

    @Shared Session sessionInternal

    // global fixtures

    /**
     * Create an administrative JCR session.
     */
    def setupSpec() {
        sessionInternal = getRepository().loginAdministrative(null)
    }

    /**
     * Remove all non-system nodes to cleanup any test data and logout of the JCR session.
     */
    def cleanupSpec() {
        removeAllNodes()

        sessionInternal.logout()
    }

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
     * Remove all non-system nodes to cleanup any test data.  This method would typically be called from a test fixture
     * method to cleanup content before the entire specification has been executed.
     */
    void removeAllNodes() {
        sessionInternal.rootNode.nodes.findAll { !SYSTEM_NODE_NAMES.contains(it.name) }*.remove()
        sessionInternal.save()
    }

    // internals

    @Synchronized
    private def getRepository() {
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

    private def registerNodeTypes() {
        sessionInternal = getRepository().loginAdministrative(null)

        NODE_TYPES.each { type ->
            this.class.getResourceAsStream("/SLING-INF/nodetypes/${type}.cnd").withStream { InputStream stream ->
                RepositoryUtil.registerNodeType(sessionInternal, stream)
            }
        }

        sessionInternal.logout()
    }
}
