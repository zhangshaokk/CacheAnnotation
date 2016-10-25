package com.cacheannotation.annotaion;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * 是否使用缓存
 * 1、使用
 * 0、不使用
 *
 * 如果使用缓存，则刷新数据库时，不会更新这条记录的json，只会更新这条记录的缓存时间
 * @author zhj on 16/10/21.
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.PARAMETER)
public @interface UseCache {
}
