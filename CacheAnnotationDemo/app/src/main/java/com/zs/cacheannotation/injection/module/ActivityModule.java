package com.zs.cacheannotation.injection.module;

import android.app.Activity;
import dagger.Module;
import dagger.Provides;


@Module
public class ActivityModule {
    private Activity mActivity;

    public ActivityModule(Activity activity) {
        mActivity = activity;
    }

    @Provides
    Activity provideActivity() {
        return mActivity;
    }

}
