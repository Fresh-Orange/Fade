package com.sysu.pro.fade.baseactivity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainBaseActivity extends AppCompatActivity {

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
