package com.icfolson.aem.prosper.annotations

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

/**
 * Indicate that a spec should register Sling Models for the current package.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@interface ModelSpec {

    /**
     * Register Sling Models for additional package names.  If empty, the spec will only register models for the
     * current package.
     *
     * @return package names containing Sling Model classes
     */
    String[] additionalPackages() default []
}