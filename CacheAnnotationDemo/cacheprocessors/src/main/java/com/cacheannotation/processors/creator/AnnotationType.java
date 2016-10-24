package com.cacheannotation.processors.creator;

/**
 * 描述：自定义注解类型
 *
 * @author zhj on 16/10/18.
 */

public interface AnnotationType {
    /**
     * 查询
     */
    int ANNOTATION_QUERY = 0X01;

    /**
     * 插入
     */
    int ANNOTATION_INSERT_OR_UPDATE = ANNOTATION_QUERY+1;
}
