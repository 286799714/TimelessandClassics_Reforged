package com.fasterxml.jackson.databind.annotation;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation that can be used to indicate a {@link PropertyNamingStrategy}
 * to use for annotated class. Overrides the global (default) strategy.
 * Note that if the {@link #value} property is omitted, its default value
 * means "use default naming" (that is, no alternate naming method is used).
 * This can be used as an override with mix-ins.
 * 
 * @since 2.1
 */
@Target({ElementType.ANNOTATION_TYPE, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@com.fasterxml.jackson.annotation.JacksonAnnotation
public @interface JsonNaming
{
    /**
     * @return Type of {@link PropertyNamingStrategy} to use, if any; default value of
     *    <code>PropertyNamingStrategy.class</code> means "no strategy specified"
     *    (and may also be used for overriding to remove otherwise applicable
     *    naming strategy)
     */
    public Class<? extends PropertyNamingStrategy> value() default PropertyNamingStrategy.class;
}
