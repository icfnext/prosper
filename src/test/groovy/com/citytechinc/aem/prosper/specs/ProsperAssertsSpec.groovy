package com.citytechinc.aem.prosper.specs

class ProsperAssertsSpec extends ProsperSpec {

    def "assert node exists"() {
        expect:
        assertNodeExists("/etc")
    }

    def "assert node exists with type"() {
        expect:
        assertNodeExists("/etc", "sling:Folder")
    }

    def "assert node exists with properties"() {
        expect:
        assertNodeExists("/etc/designs/default/jcr:content", ["jcr:title": "Default Design", "cq:lastModifiedBy": "admin"])
    }

    def "assert node exists with type and properties"() {
        expect:
        assertNodeExists("/etc/designs/default/jcr:content", "nt:unstructured", ["jcr:title": "Default Design", "cq:lastModifiedBy": "admin"])
    }

    def "assert page exists"() {
        expect:
        assertPageExists("/content/prosper")
    }

    def "assert page exists with properties"() {
        expect:
        assertPageExists("/content/prosper", ["jcr:title": "Prosper", "sling:resourceType": "prosper/components/page/prosper"])
    }
}
