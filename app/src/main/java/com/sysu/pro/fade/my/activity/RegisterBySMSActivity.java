package com.sysu.pro.fade.my.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.sysu.pro.fade.R;
import com.sysu.pro.fade.tool.RegisterTool;

/*
验证手机号码的登录界面
 */
public class RegisterBySMSActivity extends AppCompatActivity {

    private Button btnSubmitTel;
    private EditText edTelphone;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what == 1){
                String ans = (String) msg.obj;
                Toast.makeText(RegisterBySMSActivity.this,ans,Toast.LENGTH_SHORT).show();
                if(ans.equals("{}")){
                    Intent intent = new Intent(RegisterBySMSActivity.this,CheckTelActivity.class);
                    intent.putExtra("mobilePhoneNumber",edTelphone.getText().toString());
                    startActivity(intent);
                    finish();
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_by_sms);
        btnSubmitTel = (Button) findViewById(R.id.btnSubmitTel);
        edTelphone = (EditText) findViewById(R.id.edTelphone);

        btnSubmitTel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RegisterTool.sendIdentifyCode(handler,edTelphone.getText().toString());
            }
        });

    }



}
