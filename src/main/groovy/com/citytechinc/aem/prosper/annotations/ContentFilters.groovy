package com.citytechinc.aem.prosper.annotations

import java.lang.annotation.Documented
import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@interface ContentFilters {

    /**
     * Path to a complete vault filter.xml file.  The provided filter.xml file will be used when automatically
     * importing content from the /SLING-INF/content directory.
     *
     * @return path to a complete filter.xml file
     */
    String xml() default ""

    ContentFilter[] filters() default []
}
