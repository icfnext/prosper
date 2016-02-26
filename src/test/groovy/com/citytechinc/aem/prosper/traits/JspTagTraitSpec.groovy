package com.citytechinc.aem.prosper.traits

import com.citytechinc.aem.prosper.specs.ProsperSpec
import com.day.cq.wcm.api.Page
import org.apache.sling.api.SlingHttpServletRequest
import org.apache.sling.api.resource.Resource
import spock.lang.IgnoreRest
import spock.lang.Unroll

import javax.jcr.Node

import static com.day.cq.wcm.tags.DefineObjectsTag.DEFAULT_CURRENT_PAGE_NAME
import static com.day.cq.wcm.tags.DefineObjectsTag.DEFAULT_PAGE_MANAGER_NAME
import static com.day.cq.wcm.tags.DefineObjectsTag.DEFAULT_PAGE_PROPERTIES_NAME
import static com.day.cq.wcm.tags.DefineObjectsTag.DEFAULT_PROPERTIES_NAME
import static org.apache.sling.scripting.jsp.taglib.DefineObjectsTag.DEFAULT_NODE_NAME
import static org.apache.sling.scripting.jsp.taglib.DefineObjectsTag.DEFAULT_RESOURCE_NAME
import static org.apache.sling.scripting.jsp.taglib.DefineObjectsTag.DEFAULT_RESOURCE_RESOLVER_NAME

@Unroll
class JspTagTraitSpec extends ProsperSpec implements JspTagTrait {

    def setupSpec() {
        pageBuilder.content {
            prosper {
                "jcr:content" {
                    component()
                }
            }
        }
    }

    def "init tag sets default page context attributes"() {
        setup:
        def proxy = init(TestTag, path)
        def pageContext = proxy.pageContext

        expect: "default attributes are non-null"
        pageContext.getAttribute(DEFAULT_RESOURCE_RESOLVER_NAME)
        pageContext.getAttribute(DEFAULT_PAGE_MANAGER_NAME)
        pageContext.getAttribute(DEFAULT_PROPERTIES_NAME) != null
        pageContext.getAttribute(DEFAULT_PAGE_PROPERTIES_NAME) != null

        and: "resource-specific values have correct paths"
        (pageContext.getAttribute(DEFAULT_RESOURCE_NAME) as Resource).path == path
        (pageContext.getAttribute(DEFAULT_NODE_NAME) as Node).path == path
        (pageContext.getAttribute(DEFAULT_CURRENT_PAGE_NAME) as Page).path == "/content/prosper"

        where:
        path << ["/content/prosper/jcr:content", "/content/prosper/jcr:content/component"]
    }

    def "init tag for non-existing resource sets default page context attributes"() {
        setup:
        def proxy = init(TestTag, "/content/test")
        def pageContext = proxy.pageContext

        expect: "default attributes are non-null"
        pageContext.getAttribute(DEFAULT_RESOURCE_RESOLVER_NAME)
        pageContext.getAttribute(DEFAULT_PAGE_MANAGER_NAME)
        pageContext.getAttribute(DEFAULT_PROPERTIES_NAME) != null
        pageContext.getAttribute(DEFAULT_PAGE_PROPERTIES_NAME) != null

        and: "resource-specific values have correct paths"
        (pageContext.getAttribute(DEFAULT_RESOURCE_NAME) as Resource).path == "/content/test"

        and: "current node and current page are null"
        !pageContext.getAttribute(DEFAULT_NODE_NAME)
        !pageContext.getAttribute(DEFAULT_CURRENT_PAGE_NAME)
    }

    def "init tag and get result"() {
        setup:
        def proxy = init(TestTag, "/content/prosper/jcr:content")

        when:
        proxy.tag.doStartTag()

        then:
        proxy.output == "hello"
    }

    def "init tag with additional page context attributes and get result"() {
        setup:
        def proxy = init(TestTag, "/content/prosper/jcr:content", ["testName": "testValue"])

        when:
        proxy.tag.doEndTag()

        then:
        proxy.output == "testValue"
    }

    @IgnoreRest
    def "init request"() {
        setup:
        def proxy = init(TestTag, "/content/prosper/jcr:content")

        expect:
        proxy.pageContext.request instanceof SlingHttpServletRequest
    }
}
