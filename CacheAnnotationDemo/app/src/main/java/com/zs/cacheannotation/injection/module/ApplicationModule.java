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
