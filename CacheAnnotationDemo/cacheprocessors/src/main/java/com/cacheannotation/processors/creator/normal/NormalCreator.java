package com.cacheannotation.processors.creator.normal;

import com.cacheannotation.processors.creator.IJavaFileCreator;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;

/**
 * 描述：
 *
 * @author zhj on 16/10/21.
 */

public class NormalCreator implements IJavaFileCreator {

    private ICacheClassBuilderWrapper mClassBuilderWrapper;

    private ICacheMethodBuilder mMethodBuilderWrapper;

    private TypeSpec.Builder mJavaBuilder;

    public NormalCreator(TypeSpec.Builder builder,ICacheClassBuilderWrapper cb,ICacheMethodBuilder mb) {
        mClassBuilderWrapper = cb;
        mMethodBuilderWrapper = mb;
        mJavaBuilder = builder;
    }

    public NormalCreator(TypeSpec.Builder builder) {
        mJavaBuilder = builder;
    }

    public NormalCreator() {

    }

    @Override
    public void createBuilder(String fileName) {
        mJavaBuilder = TypeSpec.classBuilder(fileName);
    }

    @Override
    public void wrapperToMethodSpec(ExecutableElement element) {
        if (mMethodBuilderWrapper != null) {
            MethodSpec methodSpec = mMethodBuilderWrapper.buildMehtod(element);
            addMethod(methodSpec);
        }
    }

    @Override
    public TypeSpec create() {
        if (mJavaBuilder != null) {
            return mJavaBuilder.build();
        }
        System.out.println("没有添加buidler,构造文件失败");
        return null;
    }

    @Override
    public void setCahceClassBuilder(ICacheClassBuilderWrapper buidler) {
        mClassBuilderWrapper = buidler;
    }

    @Override
    public void setCacheMethodBuilder(ICacheMethodBuilder builder) {
        mMethodBuilderWrapper = builder;
    }

    @Override
    public void addMethod(MethodSpec spec) {
        if (mJavaBuilder != null) {
            mJavaBuilder.addMethod(spec);
        }
    }

    @Override
    public void wrapperToTypeSpec(Element element) {
        if (mClassBuilderWrapper != null) {
            mClassBuilderWrapper.wrapBuilder(element, mJavaBuilder);
        }
    }
}
