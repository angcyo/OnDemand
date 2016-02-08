package com.angcyo.ondemand;

import android.app.Application;
import android.content.Intent;

import com.angcyo.ondemand.model.UserInfo;
import com.orhanobut.hawk.Hawk;

/**
 * Created by angcyo on 15-09-26-026.
 */
public class OdApplication extends Application {
    public static UserInfo userInfo;

    @Override
    public void onCreate() {
        super.onCreate();
        Hawk.init(this).build();
        Intent service = new Intent("work_service");
        service.setPackage(getPackageName());
        startService(service);
    }
}
