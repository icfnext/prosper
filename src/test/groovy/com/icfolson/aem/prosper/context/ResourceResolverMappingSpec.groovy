package com.icfolson.aem.prosper.context

import com.icfolson.aem.prosper.specs.ProsperSpec
import io.wcm.testing.mock.aem.junit.AemContext
import io.wcm.testing.mock.aem.junit.AemContextBuilder
import spock.lang.Unroll

import static org.apache.sling.testing.mock.sling.ResourceResolverType.JCR_OAK

@Unroll
class ResourceResolverMappingSpec extends ProsperSpec {

    def setupSpec() {
        nodeBuilder.content {
            prosper()
        }

        nodeBuilder.etc {
            prosper()
        }
    }

    @Override
    AemContext getAemContext() {
        new AemContextBuilder(JCR_OAK)
            .beforeSetUp(new ProsperSlingContextCallback())
            .resourceResolverFactoryActivatorProps(["resource.resolver.mapping": ["/content/:/", "/-/"] as String[]])
            .build()
    }

    def "mapped resource path"() {
        expect:
        resourceResolver.map(path) == mappedPath

        where:
        path               | mappedPath
        "/content/prosper" | "/prosper"
        "/etc/prosper"     | "/etc/prosper"
    }
}
