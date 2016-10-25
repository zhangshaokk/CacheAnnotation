package com.cacheannotation.processors.creator.normal;

import com.cacheannotation.annotaion.InserBean;
import com.cacheannotation.annotaion.InsertOrUpdateCache;
import com.cacheannotation.annotaion.QueryCache;
import com.cacheannotation.annotaion.SQLKey;
import com.cacheannotation.annotaion.UseCache;
import com.cacheannotation.processors.creator.AnnotationType;
import com.cacheannotation.processors.creator.IJavaFileCreator;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;


import java.util.List;
import java.util.Map;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;

/**
 * 描述：生成java方法体的工具类
 *
 * @author zhj on 16/10/21.
 */

public class NormalMethodHelper implements IJavaFileCreator.ICacheMethodBuilder {
    @Override
    public MethodSpec buildMehtod(ExecutableElement element) {
        return getJavaMethod(element);
    }

    /**
     * 编写java方法体
     */
    private MethodSpec getJavaMethod(ExecutableElement methodElement) {
        System.out.println("cacheprocessor:开始编辑方法:" + methodElement.getSimpleName().toString());
        TypeName typeName = TypeName.get(methodElement.getReturnType());
        MethodSpec.Builder builder = MethodSpec
                .methodBuilder(methodElement.getSimpleName().toString())//方法名
                .addAnnotation(AnnotationSpec.builder(Override.class).build())//添加重写注解
                .returns(typeName) //返回参数
                .addJavadoc("此处只对接口方法进行特定方式的编码实现，具体接口参数不会做任何方式的校验\n" +
                        "目前的query的实现方式为：BriteDatabase.query(string sql,string... args)\n" +
                        "sql已经在注解值中要求填入，实际的args参数需自己定义接口方法时声明好与sql占位符相应的参数个数\n" +
                        "其中参数类型必须为String[],定义成其他类型会生成错误的代码")//添加方法声明
                .addModifiers(Modifier.PUBLIC);

        List<? extends AnnotationMirror> annotations = methodElement.getAnnotationMirrors();
        if (annotations != null && annotations.size() > 0) {
            for (AnnotationMirror annotationMirror : annotations) {
                //遍历注解元素,查看是否存在指定注解
                int annoType = -1;
                annoType = getAnnotationType(annotationMirror.getAnnotationType().toString());
                if (annoType <= 0)
                    continue;
                return generatedMethod(builder, annoType, typeName.toString().equals("void"), annotationMirror, methodElement);
            }
        }
        //返回空实现
        return builder.build();
    }


    private int getAnnotationType(String annotationname) {
        if (annotationname.equals(QueryCache.class.getCanonicalName())) {
            return AnnotationType.ANNOTATION_QUERY;
        } else if (annotationname.equals(InsertOrUpdateCache.class.getCanonicalName())) {
            return AnnotationType.ANNOTATION_INSERT_OR_UPDATE;
        }
        return -1;
    }

    private MethodSpec generatedMethod(MethodSpec.Builder builder
            , int annotationType, boolean noRetrun
            , AnnotationMirror annotationMirror
            , ExecutableElement methodElement) {
        switch (annotationType) {
            case AnnotationType.ANNOTATION_QUERY:
                generatedQueryCode(builder, annotationMirror, methodElement, noRetrun);
                break;
            case AnnotationType.ANNOTATION_INSERT_OR_UPDATE:
                generatedInsertOrUpdateCode(builder, annotationMirror, methodElement, noRetrun);
                break;
        }
        return builder.build();
    }


    /**
     * 生成刷新或者插入的代码
     */
    private void generatedInsertOrUpdateCode(MethodSpec.Builder builder
            , AnnotationMirror annotationMirror
            , ExecutableElement methodElement
            , boolean noReturn) {
        System.out.println("cacheprocessor:编写更新/插入方法代码：----start");
        //要记录的数据实体
        String insertBean = null;
        //处理string数组的key值
        String keys = null;
        //useCache的参数名
        String useCache = null;
        //获取方法体的参数
        for (Element pElement : methodElement.getParameters()) {
            VariableElement paramElement = (VariableElement) pElement;
            //为方法添加参数
            builder.addParameter(getParameterSpec(paramElement));
            InserBean beanAnno = paramElement.getAnnotation(InserBean.class);
            if (beanAnno != null) {
                insertBean = paramElement.getSimpleName().toString();
                continue;
            }

            SQLKey sqlKey = paramElement.getAnnotation(SQLKey.class);
            if (sqlKey != null) {
                //没有inserBean注解，则认为是key值
                keys = paramElement.getSimpleName().toString();
                continue;
            }

            UseCache annoUse = paramElement.getAnnotation(UseCache.class);
            if (annoUse != null) {
                //useCache
                if (paramElement.asType().getKind() == TypeKind.INT) {
                    useCache = paramElement.getSimpleName().toString();
                } else {
                    System.out.println("cacheprocessor:error:UseCache 的参数类型必须为int");
                    return;
                }
            }
        }

        if (insertBean == null) {
            System.out.println("cacheprocessor:error:没有找到InserBean标注的实体参数");
            return;
        }
        if (keys == null) {
            System.out.println("cacheprocessor:error:没有找到SQLkey标注的参数");
            return;
        }

        String cacheModel = "";
        String whereClause = "";
        for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry
                : annotationMirror.getElementValues().entrySet()) {
            String keyname = entry.getKey().getSimpleName().toString();
            System.out.println("cacheprocessor:遍历注解参数 --name:" + keyname);
            if (keyname.equals("cacheModel")) {
                cacheModel = entry.getValue().getValue().toString();
            } else if (keyname.equals("whereClause")) {
                whereClause = entry.getValue().getValue().toString();
            }
        }

        ClassName cacheModelName = ClassName.get(cacheModel.substring(0, cacheModel.lastIndexOf("."))
                , cacheModel.substring(cacheModel.lastIndexOf(".") + 1));
        ClassName log = ClassName.get("android.util", "Log");
        ClassName contentValues = ClassName.get("android.content", "ContentValues");
        if (useCache == null || useCache.equals("")) {
            builder.addCode(getFormatInsertCode(),
                    log, insertBean, contentValues, cacheModelName,
                    keys, cacheModelName,
                    whereClause, keys, cacheModelName, log,
                    cacheModelName, keys,
                    cacheModelName, whereClause, keys);
        } else {
            builder.addCode(getUsecacheStr(),
                    log, insertBean, contentValues, cacheModelName,
                    keys, useCache, cacheModelName, insertBean, cacheModelName,
                    whereClause, keys, cacheModelName, log,
                    cacheModelName, keys,
                    cacheModelName, whereClause, keys);
        }
    }

    private String getUsecacheStr() {
        return "$T.d(TAG , \": insert or update start\");\n" +
                "if ($N != null) {\n" +
                "\t$T cv = new $T.Marshal()\n" +
                "\t\t  .userid(Long.parseLong($N[0]))\n" +
                "\t\t  .modifytime(System.currentTimeMillis() + \"\")\n" +
                "\t\t  .asContentValues();\n" +
                "\tif ($N==0) {\n" +
                "\t\tcv.put($T.DATA, gson.toJson($N));\n" +
                "\t}\n" +
                "\tint line = mBriteDatabase.update($T.TABLE_NAME, cv,\n" +
                "\t\t$S, $N);\n" +
                "\tif (line <= 0) {\n" +
                "\t\t//执行更新没成功，说明不存在此条数据，执行插入操作\n" +
                "\t\tmBriteDatabase.insert($T.TABLE_NAME, cv);\n" +
                "\t}\n" +
                "\t$T.d(TAG, \"update commpletd\");\n" +
                "} else {\n" +
                "\tContentValues cv = new $T.Marshal()\n" +
                "\t\t.userid(Long.parseLong($N[0]))\n" +
                "\t\t.modifytime(System.currentTimeMillis() + \"\")\n" +
                "\t\t.asContentValues();\n" +
                "\tmBriteDatabase.update($T.TABLE_NAME, cv,\n" +
                "\t\t$S,$N);\n" +
                "}\n";
    }

    /**
     * 插入数据库的硬编码代码
     */
    private String getFormatInsertCode() {
        return "$T.d(TAG , \": insert or update start\");\n" +
                "if ($N != null) {\n" +
                "\t$T cv = new $T.Marshal()\n" +
                "\t\t  .userid(Long.parseLong($N[0]))\n" +
                "\t\t  .modifytime(System.currentTimeMillis() + \"\")\n" +
                "\t\t  .asContentValues();\n" +
                "\tint line = mBriteDatabase.update($T.TABLE_NAME, cv,\n" +
                "\t\t$S, $N);\n" +
                "\tif (line <= 0) {\n" +
                "\t\t//执行更新没成功，说明不存在此条数据，执行插入操作\n" +
                "\t\tmBriteDatabase.insert($T.TABLE_NAME, cv);\n" +
                "\t}\n" +
                "\t$T.d(TAG, \"update commpletd\");\n" +
                "} else {\n" +
                "\tContentValues cv = new $T.Marshal()\n" +
                "\t\t.userid(Long.parseLong($N[0]))\n" +
                "\t\t.modifytime(System.currentTimeMillis() + \"\")\n" +
                "\t\t.asContentValues();\n" +
                "\tmBriteDatabase.update($T.TABLE_NAME, cv,\n" +
                "\t\t$S,$N);\n" +
                "}\n";
    }


    /**
     * 生成查询方法的代码
     */
    private void generatedQueryCode(MethodSpec.Builder builder, AnnotationMirror annotationMirror, ExecutableElement methodElement, boolean noReturn) {
        System.out.println("cacheprocessor:编写查找方法代码:-----start");

        String keys = null;
        //获取方法体的参数
        for (Element pElement : methodElement.getParameters()) {
            VariableElement paramElement = (VariableElement) pElement;
            builder.addParameter(getParameterSpec(paramElement));
            //这里，是要找到被SQLKey标志的参数，然后拿到参数的名称
            SQLKey sqlKey = paramElement.getAnnotation(SQLKey.class);
            if (sqlKey != null) {
                keys = paramElement.getSimpleName().toString();
            }
        }

        if (keys == null) {
            System.out.println("cacheprocessor:error:没有找到SQLkey标注的参数");
            return;
        }

        //处理代码
        String sql = "";
        String cacheModel = "";
        String returnBean = "";
        for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry
                : annotationMirror.getElementValues().entrySet()) {
            String keyname = entry.getKey().getSimpleName().toString();
            System.out.println("cacheprocessor:遍历注解参数 --name:" + keyname);
            if (keyname.equals("querySql")) {
                sql = entry.getValue().getValue().toString();
            } else if (keyname.equals("cacheModel")) {
                cacheModel = entry.getValue().getValue().toString();
            } else if (keyname.equals("returnBean")) {
                returnBean = entry.getValue().getValue().toString();
            }
        }
        ClassName cacheModelName = ClassName.get(cacheModel.substring(0, cacheModel.lastIndexOf("."))
                , cacheModel.substring(cacheModel.lastIndexOf(".") + 1));
        ClassName beanName = ClassName.get(returnBean.substring(0, returnBean.lastIndexOf("."))
                , returnBean.substring(returnBean.lastIndexOf(".") + 1));
        ClassName cursor = ClassName.get("android.database", "Cursor");
        ClassName log = ClassName.get("android.util", "Log");
        builder.addCode(getFormatSearchCode(noReturn), log, cursor, sql, keys
                , cacheModelName, cacheModelName, beanName, beanName, log);
    }

    /**
     * 返回查询的硬编码代码
     * 简单说明一下占位符
     * $S for Strings
     * $T for Types
     * $N for Names(我们自己生成的方法名或者变量名等等)
     这里的$T，在生成的源代码里面，也会自动导入你的类。
     * @param noReturn 是否有返回值
     * @return
     */
    private String getFormatSearchCode(boolean noReturn) {
        return noReturn ? "" : "return " + "Observable.create(subscriber -> {\n" +
                "$T.d(TAG, \"do get cache start:--\");\n" +
                "$T cursor = mBriteDatabase.query($S,$N);\n" +
                "boolean result = false;\n" +
                "if (cursor.moveToNext()) {\n" +
                "\ttry {\n" +
                "\t\t$T cacheModel = $T.MAPPER.map(cursor);\n" +
                "\t\t$T bean = $T.typeAdapter(gson).fromJson(cacheModel.data());\n" +
                "\t\tbean.cache_time = Long.parseLong(cacheModel.modifytime());\n"+
                "\t\tsubscriber.onNext(bean);\n" +
                "\t\tsubscriber.onCompleted();\n" +
                "\t\tresult = true;\n" +
                "\t} catch (Exception e) {\n" +
                "\t\tsubscriber.onError(e);\n" +
                "\t\tresult = false;\n" +
                "\t}\n" +
                "} else {\n" +
                "\tsubscriber.onCompleted();\n" +
                "\tresult = true;\n" +
                "}\n" +
                "$T.d(TAG, \"do get cache result:\" + (result ? \"finish\" : \"error\"));\n" +
                "});\n";
    }

    /**
     * 编写参数实体
     */
    private ParameterSpec getParameterSpec(VariableElement paramElement) {
        System.out.println("cacheprocessor:开始编辑方法参数:" + paramElement.getSimpleName().toString());
        return ParameterSpec.builder(
                TypeName.get(paramElement.asType())
                , paramElement.getSimpleName().toString()).build();
    }
}
