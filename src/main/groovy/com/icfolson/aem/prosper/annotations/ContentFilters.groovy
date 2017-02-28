package com.icfolson.aem.prosper.annotations

import java.lang.annotation.Documented
import java.lang.annotation.ElementType
import java.lang.annotation.Inherited
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

/**
 * Specify the filters to use when importing content for an annotated spec.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
@interface ContentFilters {

    /**
     * Path to a complete vault filter.xml file.  The provided filter.xml file will be used when automatically
     * importing content from the /SLING-INF/content directory.
     *
     * @return path to a complete filter.xml file
     */
    String xml() default ""

    /**
     * Explicitly define filters for the content import.  These filters will override any filters defined in either
     * the default filter.xml file or one defined using the <code>xml</code> property on this annotation.  Thus,
     * specs should define either this property or the <code>xml</code> property, but not both.
     *
     * @return array of content filters to apply when importing content for this spec
     */
    ContentFilter[] filters() default []
}
