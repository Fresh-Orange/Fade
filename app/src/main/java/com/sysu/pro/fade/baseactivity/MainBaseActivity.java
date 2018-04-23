package com.sysu.pro.fade.baseactivity;

import android.os.Bundle;

import com.sysu.pro.fade.home.activity.TintedCompatActivity;

public class MainBaseActivity extends TintedCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainActivitiesCollector.addActivity(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MainActivitiesCollector.removeActivity(this);
    }
}
