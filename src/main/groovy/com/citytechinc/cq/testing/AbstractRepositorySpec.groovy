package com.citytechinc.cq.testing

import groovy.transform.Synchronized
import org.apache.sling.commons.testing.jcr.RepositoryUtil
import spock.lang.Shared
import spock.lang.Specification

/**
 * Abstract Spock specification for JCR-based testing.
 */
abstract class AbstractRepositorySpec extends Specification {

    static final def SYSTEM_NODE_NAMES = ["jcr:system", "rep:policy"]

    static final def NODE_TYPES = ["sling", "replication", "tagging", "core", "dam", "vlt"]

    static def repository

    @Shared session

    /**
     * Create an administrative JCR session.
     */
    def setupSpec() {
        session = getRepository().loginAdministrative(null)
    }

    /**
     * Remove all non-system nodes to cleanup any test data and logout of the JCR session.
     */
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

    /**
     * Remove all non-system nodes to cleanup any test data.  This method would typically be called from a test fixture
     * method to cleanup content before the entire specification has been executed.
     */
    void removeAllNodes() {
        session.rootNode.nodes.findAll { !SYSTEM_NODE_NAMES.contains(it.name) }*.remove()
        session.save()
    }

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

        properties.each { k, v ->
            assert node.get(k) == v
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

        properties.each { k, v ->
            assert node.get(k) == v
        }
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