package com.zs.cacheannotation.injection.module;

import android.app.Application;
import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ryanharter.auto.value.gson.GsonTypeAdapterFactory;
import com.squareup.sqlbrite.BriteDatabase;
import com.zs.cacheannotation.data.DataBaseManager;
import com.zs.cacheannotation.data.MyGsonAdapterFactory;
import com.zs.cacheannotation.injection.ApplicationContext;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Copyright (C) jaydenlau Inc.All Rights Reserved.
 * FileName：com.pengyouwan.xiaopengapp.injection.module.
 *
 * @Description：
 * Provide application-level dependencies. Mainly singleton object that can be injected from
 * anywhere in the app.
 *
 * History：
 * 版本号 作者 日期 简要介绍相关操作
 * ${VERSION} jaydenlau 16/8/8 Create
 * 2.2
 */

@Module
public class ApplicationModule {
    protected final Application mApplication;

    public ApplicationModule(Application application) {
        mApplication = application;
    }

    @Provides
    Application provideApplication() {
        return mApplication;
    }

    @Provides
    @ApplicationContext
    Context provideContext() {
        return mApplication;
    }



    @Provides
    @Singleton
    BriteDatabase provideBriteDatabase() {
        return DataBaseManager.getBriteDatabase(mApplication);
    }

    @Provides
    @Singleton
    Gson provideGson() {
        return  new GsonBuilder()
                .registerTypeAdapterFactory(MyGsonAdapterFactory.create())
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                .create();
    }

}
