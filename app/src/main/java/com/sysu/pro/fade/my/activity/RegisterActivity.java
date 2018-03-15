package com.sysu.pro.fade.my.activity;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.sysu.pro.fade.R;
import com.sysu.pro.fade.baseactivity.LoginBaseActivity;

/**
 * Created by huanggzh5 on 2018/3/9.
 */

public class RegisterActivity extends LoginBaseActivity {

    private ImageView registerbtn;
    private TextView loginbtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_main);
        registerbtn = (ImageView) findViewById(R.id.btnRegister);
        loginbtn = (TextView) findViewById(R.id.tologin);
        registerbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this, SetPasswordActivity.class));
            }
        });
        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this, LoginMainActivity.class));
            }
        });
    }
}
