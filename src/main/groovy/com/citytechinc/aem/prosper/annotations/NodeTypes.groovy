package com.citytechinc.aem.prosper.annotations

import java.lang.annotation.Documented
import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

/**
 * Add JCR namespaces and node types by providing a list of paths to CND files.  Specs should use this annotation to
 * add CND files to be registered at runtime.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@interface NodeTypes {

    /**
     * Set additional node types to register for the annotated spec.
     *
     * @return array of paths to CND file resources
     */
    String[] value() default []
}
