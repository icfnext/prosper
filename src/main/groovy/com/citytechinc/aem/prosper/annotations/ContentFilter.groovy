package com.citytechinc.aem.prosper.annotations

import org.apache.jackrabbit.vault.fs.api.ImportMode

import java.lang.annotation.Documented
import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@interface ContentFilter {

    String root()

    ImportMode mode() default ImportMode.REPLACE

    ContentFilterRule[] rules() default []
}
