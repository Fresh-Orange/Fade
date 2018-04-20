package com.sysu.pro.fade.my.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.sysu.pro.fade.R;
import com.sysu.pro.fade.baseactivity.LoginBaseActivity;

/**
 * Created by huanggzh5 on 2018/3/11.
 */

public class LoginMainActivity extends LoginBaseActivity {
    private TextView forget_password_btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_main);
        forget_password_btn = (TextView) findViewById(R.id.forget_password);
        forget_password_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginMainActivity.this, TelephoneActivity.class));
            }
        });
    }
}
