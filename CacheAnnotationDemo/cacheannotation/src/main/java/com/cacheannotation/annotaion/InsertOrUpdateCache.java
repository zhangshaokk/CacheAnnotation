package com.cacheannotation.annotaion;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 描述：声明生成相应的先更新后查询方法
 *
 * @author zhj on 16/10/19.
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
public @interface InsertOrUpdateCache {
    Class cacheModel();

    String whereClause();
}
