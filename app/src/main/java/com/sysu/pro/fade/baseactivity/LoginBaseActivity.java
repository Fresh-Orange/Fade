package com.sysu.pro.fade.baseactivity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.sysu.pro.fade.R;

public class LoginBaseActivity extends AppCompatActivity {

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
