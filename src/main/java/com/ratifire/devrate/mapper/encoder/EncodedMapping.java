package com.ratifire.devrate.mapper.encoder;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.mapstruct.Qualifier;

/**
 * Indicates that the annotated element represents an encoded mapping. This annotation can be
 * applied to types (classes, interfaces, enums) or methods.
 */
@Qualifier
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.CLASS)
public @interface EncodedMapping {

}
