package com.sysu.pro.fade.baseactivity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.sysu.pro.fade.R;

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
