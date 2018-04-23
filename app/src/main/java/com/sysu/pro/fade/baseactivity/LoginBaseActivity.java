package com.sysu.pro.fade.baseactivity;

import android.os.Bundle;

import com.sysu.pro.fade.home.activity.TintedCompatActivity;

public class LoginBaseActivity extends TintedCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LoginActivitiesCollector.addActivity(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LoginActivitiesCollector.removeActivity(this);
    }
}
