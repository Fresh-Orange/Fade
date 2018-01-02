package com.sysu.pro.fade.my.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.sysu.pro.fade.R;
import com.sysu.pro.fade.baseactivity.LoginBaseActivity;

public class GuideActivity extends LoginBaseActivity {

    private Button btnChooseLogin;
    private Button btnChooseRegister;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);

        btnChooseLogin = (Button) findViewById(R.id.btnChooseLogin);
        btnChooseRegister = (Button) findViewById(R.id.btnChooseRegister);

        btnChooseLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(GuideActivity.this,LoginActivity.class));
                //finish();
            }
        });

        btnChooseRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(GuideActivity.this,RegisterBySMSActivity.class));
                //finish();
            }
        });

    }
}
