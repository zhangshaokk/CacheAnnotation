package com.cacheannotation.annotaion;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 描述：声明需要生成相应的查询方法
 *
 * @author zhj on 16/10/18.
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
public @interface QueryCache {
    String querySql() default "";
    Class cacheModel();
    Class returnBean();
}
