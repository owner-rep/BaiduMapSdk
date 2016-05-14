package com.uddream.baidu;

import android.app.Application;

import com.uddream.baidu.map.BaiduMapSdk;

/**
 * Created by Glen on 2016/5/12.
 */
public class MapApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        BaiduMapSdk.init(getApplicationContext());

    }
}
