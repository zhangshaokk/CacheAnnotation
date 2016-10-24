package com.zs.cacheannotation.injection.component;

import android.app.Application;
import android.content.Context;

import com.google.gson.Gson;
import com.squareup.sqlbrite.BriteDatabase;
import com.zs.cacheannotation.DemoApplication;
import com.zs.cacheannotation.injection.ApplicationContext;
import com.zs.cacheannotation.injection.module.ApplicationModule;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Copyright (C) jaydenlau Inc.All Rights Reserved.
 * FileName：com.pengyouwan.xiaopengapp.injection.component.
 *
 * @Description：
 * ApplicationComponent 是一个 Interface，
 * 通过 @Component 添加了 Module ： 。
 * 此外还有一个inject方法，其中的参数表示要注入的位置
 * （Component中的方法还可以起到暴露资源，实现Component中的“继承”的作用）
 * History：
 * 版本号 作者 日期 简要介绍相关操作
 * ${VERSION} jaydenlau 16/8/8 Create
 */

@Singleton
@Component(modules = ApplicationModule.class)
public interface ApplicationComponent {

    void inject(DemoApplication application);

    @ApplicationContext
    Context context();
    Application application();
    BriteDatabase briteDatabase();
    Gson gson();
}
