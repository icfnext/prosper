package com.citytechinc.aem.prosper.annotations

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

/**
 *
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@interface ImporterConfiguration {

    /**
     * Path to a complete vault filter.xml file.  The provided filter.xml file will be used when automatically
     * importing content from the /SLING-INF/content directory.
     *
     * @return path to a complete filter.xml file
     */
    String filterXmlPath() default ""

    /**
     * Set additional JCR content paths for content that should be imported from the /SLING-INF/content directory.
     *
     * @return array of content paths to import
     */
    String[] filterPaths() default []
}