package com.uddream.baidu.map;

import android.content.Context;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Glen on 2016/5/13.
 */
public class BaiduLocation implements BDLocationListener {
    private Context context;
    private LocationClient mLocationClient;
    private BDLocation mLastBDLocation;
    private long mCacheTime = 1000 * 60 * 5;//默认5分钟
    private List<OnLocationListener> mListeners = new ArrayList<>();

    public BaiduLocation(Context context) {
        this.context = context;
    }

    private void initLocation() {
        if (mLocationClient == null) {
            mLocationClient = new LocationClient(context);//声明LocationClient类
            LocationClientOption option = new LocationClientOption();
            option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
            option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
            option.setScanSpan(0);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
            option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
            option.setOpenGps(true);//可选，默认false,设置是否使用gps
            option.setLocationNotify(true);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
            option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
            option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
            option.setIgnoreKillProcess(false);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
            option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
            option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤gps仿真结果，默认需要
            mLocationClient.setLocOption(option);
            mLocationClient.registerLocationListener(this);    //注册监听函数
        }
    }

    private void updateLocationCache(BDLocation location) {
        if (location != null) {
            this.mLastBDLocation = location;
            this.mLastBDLocation.setTime(String.valueOf(System.currentTimeMillis()));
            Log.d(BaiduMapSdk.TAG, "Update Location Cache Data Success");
        }
    }

    public BDLocation getCacheLocation() {
        if (this.mLastBDLocation != null) {
            long lastTime = Long.parseLong(this.mLastBDLocation.getTime());
            if (System.currentTimeMillis() - lastTime > mCacheTime) {
                this.mLastBDLocation = null;//缓存失效
            } else {
                Log.d(BaiduMapSdk.TAG, "Read Cache Location Success");
            }
        }
        return this.mLastBDLocation;
    }

    /**
     * 设置缓存失效时间
     *
     * @param mCacheTime
     */
    public void setCacheTime(long mCacheTime) {
        this.mCacheTime = mCacheTime;
    }

    public void clearCacheLocation() {
        this.mLastBDLocation = null;
    }

    public void startLocation(OnLocationListener listener) {
        //读取缓存数据
        BDLocation location = getCacheLocation();
        if (location != null) {
            listener.onLocationFinish(location);
            return;
        }
        //启动定位
        if (listener != null && !mListeners.contains(listener)) {
            synchronized (mListeners) {
                mListeners.add(listener);
            }
        }
        if (mLocationClient == null) {
            initLocation();
        }
        if (!mLocationClient.isStarted()) {
            mLocationClient.start();
        }
    }

    @Override
    public void onReceiveLocation(BDLocation location) {
        Log.d(BaiduMapSdk.TAG, "Location Call Back Success");
        mLocationClient.stop();
        if (location != null
                && (location.getLocType() == BDLocation.TypeGpsLocation//GPS
                || location.getLocType() == BDLocation.TypeOffLineLocation//OffLine
                || location.getLocType() == BDLocation.TypeNetWorkLocation)) {//Net
            if (location.getLatitude() > 0 && location.getLongitude() > 0) {
                //定位成功
                updateLocationCache(location);
            } else {
                location = null;
            }
        } else {
            location = null;
        }

        synchronized (mListeners) {
            for (OnLocationListener item : mListeners) {
                item.onLocationFinish(location);
            }
            mListeners.clear();
        }
    }

    public static String getLocationInfo(BDLocation location) {
        if (location == null) return "null";
        StringBuffer sb = new StringBuffer(256);
        sb.append("time : ");
        sb.append(location.getTime());
        sb.append("\nerror code : ");
        sb.append(location.getLocType());
        sb.append("\nlatitude : ");
        sb.append(location.getLatitude());
        sb.append("\nlontitude : ");
        sb.append(location.getLongitude());
        if (location.getLocType() == BDLocation.TypeGpsLocation) {// GPS定位结果
            sb.append("\naddr : ");
            sb.append(location.getAddrStr());
            sb.append("\ndescribe : ");
            sb.append("gps定位成功");
        } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
            sb.append("\naddr : ");
            sb.append(location.getAddrStr());
            sb.append("\ndescribe : ");
            sb.append("网络定位成功");
        } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
            sb.append("\ndescribe : ");
            sb.append("离线定位成功，离线定位结果也是有效的");
        } else if (location.getLocType() == BDLocation.TypeServerError) {
            sb.append("\ndescribe : ");
            sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
        } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
            sb.append("\ndescribe : ");
            sb.append("网络不同导致定位失败，请检查网络是否通畅");
        } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
            sb.append("\ndescribe : ");
            sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
        }
        sb.append("\nlocationdescribe : ");
        sb.append(location.getLocationDescribe());// 位置语义化信息
        return sb.toString();
    }
}