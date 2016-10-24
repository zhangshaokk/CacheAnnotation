package com.cacheannotation.processors;


import com.cacheannotation.annotaion.AutoCache;
import com.cacheannotation.annotaion.InserBean;
import com.cacheannotation.annotaion.InsertOrUpdateCache;
import com.cacheannotation.annotaion.QueryCache;
import com.cacheannotation.annotaion.SQLKey;
import com.cacheannotation.annotaion.UseCache;
import com.cacheannotation.processors.creator.IJavaFileCreator;
import com.cacheannotation.processors.creator.normal.NormalClassHelper;
import com.cacheannotation.processors.creator.normal.NormalCreator;
import com.cacheannotation.processors.creator.normal.NormalMethodHelper;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;


import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;

/**
 * 处理自定义cache注解.
 */
@AutoService(Processor.class)
public class CacheProcessor extends AbstractProcessor {
    private Filer mFiler;

    private IJavaFileCreator mJavaFileCreator;

    /**
     * init()方法会被注解处理工具调用，并输入ProcessingEnviroment参数。
     * ProcessingEnviroment提供很多有用的工具类Elements, Types 和 Filer
     *
     * @param processingEnv 提供给 processor 用来访问工具框架的环境
     */
    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        mFiler = processingEnv.getFiler();
        //先创建文件构造工具
        mJavaFileCreator = new NormalCreator();
        //设置构造函数，java类名以及集成关系的辅助工具
        mJavaFileCreator.setCahceClassBuilder(new NormalClassHelper());
        //设置生成java方法辅助工具
        mJavaFileCreator.setCacheMethodBuilder(new NormalMethodHelper());
    }

    /**
     * 这相当于每个处理器的主函数main()，你在这里写你的扫描、评估和处理注解的代码，以及生成Java文件。
     * 输入参数RoundEnviroment，可以让你查询出包含特定注解的被注解元素
     *
     * @param annotations 请求处理的注解类型
     * @param roundEnv    有关当前和以前的信息环境
     * @return 如果返回 true，则这些注解已声明并且不要求后续 Processor 处理它们；
     * 如果返回 false，则这些注解未声明并且可能要求后续 Processor 处理它们
     */
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (!roundEnv.processingOver()) {
            //遍历AutoCache标注的接口类
            for (Element iClassElement : roundEnv.getElementsAnnotatedWith(AutoCache.class)) {
                //创建java文件，文件名为xxxImpl
                String fileName = iClassElement.getSimpleName().toString() + "Impl";
                mJavaFileCreator.createBuilder(fileName);
                mJavaFileCreator.wrapperToTypeSpec(iClassElement);
                //获取类里的抽象方法,并遍历
                for (Element method : iClassElement.getEnclosedElements()) {
                    mJavaFileCreator.wrapperToMethodSpec((ExecutableElement) method);
                }

                PackageElement packageElement = (PackageElement) iClassElement.getEnclosingElement();
                writeJavaFile(mJavaFileCreator.create()
                        , packageElement.getQualifiedName().toString());
            }
        }
        return true;
    }


    /**
     * 编写Java文件
     *
     * @param javaClass   java文件对应的class内容实体
     * @param packageName 文件存放路径
     */
    private void writeJavaFile(TypeSpec javaClass, String packageName) {
        System.out.println("cacheprocessor:开始编写文件:" + packageName + "." + javaClass.name.toLowerCase());
        JavaFile javaFile = JavaFile.builder(packageName, javaClass)
                .addFileComment("     自动编写的代码，请不要改动...\n" +
                        "     只针对特定注解的方法进行指定的方法体编写，一定要确保注解里的值准确\n" +
                        "     没有添加注解的接口方法只会进行空实现，稍微进行简单的容错处理")
                .build();
        try {
            javaFile.writeTo(mFiler);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 这里必须指定，这个注解处理器是注册给哪个注解的。注意，它的返回值是一个字符串的集合，包含本处理器想要处理的注解类型的合法全称
     *
     * @return 注解器所支持的注解类型集合，如果没有这样的类型，则返回一个空集合
     */
    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotataions = new LinkedHashSet<>();
        annotataions.add(AutoCache.class.getCanonicalName());
        annotataions.add(QueryCache.class.getCanonicalName());
        annotataions.add(InserBean.class.getCanonicalName());
        annotataions.add(InsertOrUpdateCache.class.getCanonicalName());
        annotataions.add(SQLKey.class.getCanonicalName());
        annotataions.add(UseCache.class.getCanonicalName());
        return annotataions;
    }

    /**
     * 指定使用的Java版本，通常这里返回SourceVersion.latestSupported()，默认返回SourceVersion.RELEASE_6
     *
     * @return 使用的Java版本
     */
    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }
}

