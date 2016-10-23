package com.cacheannotation.annotaion;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 描述：用于要实现自动编码的接口类
 *
 * @author zhj on 16/10/14.
 */

@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
public @interface AutoCache {
    String tagName() default "";
}
