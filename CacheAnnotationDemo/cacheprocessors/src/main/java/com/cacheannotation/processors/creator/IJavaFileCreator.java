package com.cacheannotation.processors.creator;

import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;

/**
 * 描述：java文件构造器
 *
 * @author zhj on 16/10/21.
 */
public interface IJavaFileCreator {
    /**
     * 将之前生成或者传入的TypeSpec.Builder 调用其build方法
     * 若之前没有传入或者生成过任何builder,则返回空
     *
     * @return
     */
    TypeSpec create();

    /**
     * 设置java文件类辅助工具，由于生成代码大部分是硬编码形式
     * 因此，若有特定的需求要对代码改动，可以替换成自定义实现的辅助工具
     *
     * @param buidler
     */
    void setCahceClassBuilder(ICacheClassBuilderWrapper buidler);

    /**
     * 设置java方法辅助工具，由于生成代码大部分是硬编码形式
     * 因此，若有特定需求要对代码改动，可以替换成自定义实现的辅助工具
     *
     * @param builder
     */
    void setCacheMethodBuilder(ICacheMethodBuilder builder);

    /**
     * 将生成的MethodSpec设置到之前生成或者传入的TypeSpec.Builder中
     *
     * @param spec
     */
    void addMethod(MethodSpec spec);

    /**
     * 将扫描到的类元素信息转换成相应的java类信息
     *
     * @param element
     */
    void wrapperToTypeSpec(Element element);

    /**
     * 根据传入的文件名字，生成一个新的TypeSpec.Builder
     *
     * @param fileName
     */
    void createBuilder(String fileName);

    /**
     * 将方法元素转换成相应的java方法信息
     *
     * @param element
     */
    void wrapperToMethodSpec(ExecutableElement element);

    /**
     * 描述：
     *
     * @author zhj on 16/10/21.
     */

    interface ICacheClassBuilderWrapper {

        TypeSpec.Builder wrapBuilder(Element element, TypeSpec.Builder buidler);
    }

    interface ICacheMethodBuilder {
        MethodSpec buildMehtod(ExecutableElement element);
    }
}
