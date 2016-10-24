package com.cacheannotation.processors.creator.normal;

import com.cacheannotation.annotaion.AutoCache;
import com.cacheannotation.processors.creator.IJavaFileCreator;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;

/**
 * 描述：
 *
 * @author zhj on 16/10/21.
 */

public class NormalClassHelper implements IJavaFileCreator.ICacheClassBuilderWrapper {


    @Override
    public TypeSpec.Builder wrapBuilder(Element iClassElement,TypeSpec.Builder buidler) {
        AutoCache autoCache = iClassElement.getAnnotation(AutoCache.class);
        String tagName = "";
        if (autoCache != null) {
            tagName = autoCache.tagName();
        }
        if (tagName.equals("")) {
            tagName = "TAG";
        }
        buidler.addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addField(generatedTag(tagName))//增加tag
                .addField(generatedDatabase())//初始化database
                .addField(generatedGson())
                .addMethod(generatedConstructor())
                .addJavadoc("本类为自动编写，无法删改或者继承\n" +
                        "对任何被注解{@AutoCache}标记的接口里缺少其他注解标明的方法，只会进行空实现")
                .addSuperinterface(TypeName.get(iClassElement.asType()));
        return buidler;
    }


    /**
     * 生成tag
     *
     * @param tagName
     * @return
     */
    private FieldSpec generatedTag(String tagName) {
        return FieldSpec.builder(String.class, "TAG", Modifier.PRIVATE, Modifier.FINAL)
                .initializer("\"" + tagName + "\"").build();
    }

    /**
     * 生成com.squareup.sqlbrite.BriteDatabase成员
     */
    private FieldSpec generatedDatabase() {
        return FieldSpec.builder(
                ClassName.get("com.squareup.sqlbrite", "BriteDatabase"), "mBriteDatabase")
                .addAnnotation(ClassName.get("javax.inject", "Inject"))//声明Inject注解
                .build();
    }

    /**
     * 生成Gson
     */
    private FieldSpec generatedGson() {
        return FieldSpec.builder(ClassName.get("com.google.gson", "Gson"), "gson").build();
    }

    /**
     * 生成构造函数
     */
    private MethodSpec generatedConstructor() {
        return MethodSpec.constructorBuilder()
                .addAnnotation(ClassName.get("javax.inject", "Inject"))//声明Inject注解
                .addParameter(ParameterSpec.builder(ClassName.get("com.google.gson", "Gson"), "gson").build())
                .addStatement("this.gson = gson")
                .build();
    }
}
