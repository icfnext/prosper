package com.citytechinc.aem.prosper.annotations

import java.lang.annotation.Documented
import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

/**
 * Rule to apply for a content filter.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@interface ContentFilterRule {

    /**
     * Regular expression pattern to apply for the path filter.
     *
     * @return regex pattern
     */
    String pattern()

    /**
     * Set whether this rule is an include or an exclude.
     *
     * @return rule type
     */
    ContentFilterRuleType type()
}
