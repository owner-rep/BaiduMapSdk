package com.uddream.baidu.map;

import android.content.Context;
import android.os.SystemClock;
import android.util.Log;

import com.baidu.mapapi.SDKInitializer;

/**
 * Created by Glen on 2016/5/12.
 */
public class BaiduMapSdk {
    public static final String TAG = "BaiduMapSdk";
    private static BaiduLocation baiduLocation;

    /**
     * 百度地图初始化
     *
     * @param context
     */
    public static void init(Context context) {
        long start = SystemClock.currentThreadTimeMillis();

        SDKInitializer.initialize(context);
        baiduLocation = new BaiduLocation(context);

        Log.d(TAG, "BaiduMapSdk Init Total Time:" + (SystemClock.currentThreadTimeMillis() - start));
    }

    public static BaiduLocation getBaiduLocation() {
        return baiduLocation;
    }
}