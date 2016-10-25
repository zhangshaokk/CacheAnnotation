package com.zs.cacheannotation.injection.component;



import com.zs.cacheannotation.MainActivity;
import com.zs.cacheannotation.injection.PerActivity;
import com.zs.cacheannotation.injection.module.ActivityModule;

import dagger.Component;

/**
 *
 */
@PerActivity
@Component(dependencies = ApplicationComponent.class, modules = ActivityModule.class)
public interface ActivityComponent {
    void inject(MainActivity mainActivity);
}
