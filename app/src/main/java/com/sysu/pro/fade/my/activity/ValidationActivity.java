package com.sysu.pro.fade.my.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.sysu.pro.fade.R;
import com.sysu.pro.fade.baseactivity.LoginBaseActivity;
import com.sysu.pro.fade.beans.Code;

/**
 * Created by huanggzh5 on 2018/3/11.
 */

public class ValidationActivity extends LoginBaseActivity{
    Code mCode;
    private ImageView nextbtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_validation);
        mCode = (Code) findViewById(R.id.code);
        nextbtn = (ImageView) findViewById(R.id.next);
        nextbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ValidationActivity.this, SetNewPasswordActivity.class));
            }
        });
    }
}
