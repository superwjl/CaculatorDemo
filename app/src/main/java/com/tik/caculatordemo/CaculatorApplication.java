package com.tik.caculatordemo;

import android.app.Application;

import com.aitangba.swipeback.ActivityLifecycleHelper;


public class CaculatorApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        registerActivityLifecycleCallbacks(ActivityLifecycleHelper.build());
    }
}
