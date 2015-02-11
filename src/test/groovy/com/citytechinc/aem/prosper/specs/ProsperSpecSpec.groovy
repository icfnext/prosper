package com.citytechinc.aem.prosper.specs

import com.day.cq.tagging.TagManager
import com.day.cq.wcm.api.Page
import com.day.cq.wcm.api.PageManager
import org.apache.sling.api.adapter.AdapterFactory
import org.apache.sling.api.resource.Resource
import org.apache.sling.api.resource.ResourceResolver
import org.apache.sling.api.resource.ValueMap
import org.apache.sling.commons.json.jcr.JsonItemWriter
import spock.lang.Unroll

import javax.jcr.Node
import javax.jcr.Session

@Unroll
class ProsperSpecSpec extends ProsperSpec {

    @Override
    Collection<AdapterFactory> addAdapterFactories() {
        def adapterFactory = new AdapterFactory() {
            @Override
            def <AdapterType> AdapterType getAdapter(Object adaptable, Class<AdapterType> type) {
                def result

                if (adaptable instanceof Resource) {
                    result = type == Integer ? 1982 : null
                } else if (adaptable instanceof ResourceResolver) {
                    result = type == Integer ? 2014 : null
                } else {
                    result = null
                }

                (AdapterType) result
            }
        }

        [adapterFactory]
    }

    @Override
    Map<Class, Closure> addResourceAdapters() {
        [(String.class): { "hello" }]
    }

    @Override
    Map<Class, Closure> addResourceResolverAdapters() {
        [(String.class): { "world" }]
    }

    @Override
    List<InputStream> addCndInputStreams() {
        [this.class.getResourceAsStream("/SLING-INF/nodetypes/prosper.cnd")]
    }

    @Override
    List<String> addNodeTypes() {
        ["/SLING-INF/nodetypes/spock.cnd"]
    }

    def setupSpec() {
        pageBuilder.content {
            home() {
                "jcr:content"()
            }
        }

        nodeBuilder.etc {
            prosper("prosper:TestType")
            spock("spock:TestType")
        }

        new File("/Users/mark/Downloads/out.json").withWriter {
            new JsonItemWriter(null).dump(session.getNode("/content"), it, -1, true)
        }
    }

    def "registered adapter factory"() {
        expect:
        resourceResolver.adaptTo(Integer) == 2014
        resourceResolver.getResource("/").adaptTo(Integer) == 1982
    }

    def "adapt to page manager"() {
        expect:
        resourceResolver.adaptTo(PageManager)
    }

    def "adapt to tag manager"() {
        expect:
        resourceResolver.adaptTo(TagManager)
    }

    def "adapt to session"() {
        expect:
        resourceResolver.adaptTo(Session)
    }

    def "adapt to invalid type returns null"() {
        expect:
        !resourceResolver.adaptTo(Boolean)
    }

    def "additional resource adapter"() {
        expect:
        resourceResolver.getResource("/").adaptTo(String) == "hello"
    }

    def "additional resource resolver adapter"() {
        expect:
        resourceResolver.adaptTo(String) == "world"
    }

    def "add resource adapter for test"() {
        setup:
        addResourceAdapter(Map, { [:] })

        expect:
        resourceResolver.getResource("/").adaptTo(Map) == [:]
    }

    def "add resource resolver adapter for test"() {
        setup:
        addResourceResolverAdapter(Map, { [:] })

        expect:
        resourceResolver.adaptTo(Map) == [:]
    }

    def "adapt resource to page"() {
        setup:
        def resource = getResource("/content/home")

        expect:
        resource.adaptTo(Page)
    }

    def "adapt resource to value map"() {
        setup:
        def resource = getResource("/content/home")

        expect:
        resource.adaptTo(ValueMap)
    }

    def "adapt resource to node"() {
        setup:
        def resource = getResource("/content/home")

        expect:
        resource.adaptTo(Node)
    }

    def "check node type for node with custom type"() {
        setup:
        def node = getNode("/etc/prosper")

        expect:
        node.isNodeType("prosper:TestType")

        where:
        path           | nodeType
        "/etc/prosper" | "prosper:TestType"
        "/etc/spock"   | "spock:TestType"
    }

    def "import vault content"() {

    }
}
