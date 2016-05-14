package com.uddream.baidu;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;

import com.uddream.baidu.map.BaiduMapFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction bt = manager.beginTransaction();
        bt.add(Window.ID_ANDROID_CONTENT, new BaiduMapFragment(), "map");
        bt.commit();
    }
}