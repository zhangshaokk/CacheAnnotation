package com.zs.cacheannotation;

import android.app.Application;

import com.zs.cacheannotation.injection.component.ApplicationComponent;
import com.zs.cacheannotation.injection.module.ApplicationModule;
import com.zs.cacheannotation.injection.component.DaggerApplicationComponent;

/**
 * 描述：
 *
 * @author zhj on 16/10/24.
 */

public class DemoApplication extends Application {

    ApplicationComponent mApplicationComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        //这里使用dagger2进行依赖注入
        mApplicationComponent = DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this)).build();
        mApplicationComponent.inject(this);
    }
}
