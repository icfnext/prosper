package com.icfolson.aem.prosper.annotations

import org.apache.jackrabbit.vault.fs.api.ImportMode

import java.lang.annotation.Documented
import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

/**
 * Filter representing an element in a workspace filter.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@interface ContentFilter {

    /**
     * Root path for filter element.
     *
     * @return JCR path
     */
    String root()

    /**
     * Import mode to apply for this filter.
     *
     * @return import mode
     */
    ImportMode mode() default ImportMode.REPLACE

    /**
     * Inclusion and/or exclusion rules to apply for this filter.
     *
     * @return array of filter rules
     */
    ContentFilterRule[] rules() default []
}
