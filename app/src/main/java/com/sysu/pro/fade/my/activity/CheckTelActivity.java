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
短信发出后输入验证码的界面
 */
public class CheckTelActivity extends AppCompatActivity {

    private EditText edGetCheckNum;
    private Button btnToCheckNum;
    private String mobilePhoneNumber;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what == 1){
                String ans = (String) msg.obj;
                Toast.makeText(CheckTelActivity.this,ans,Toast.LENGTH_SHORT).show();
                if(ans.equals("{}")){
                    startActivity(new Intent(CheckTelActivity.this,RegisterActivity.class));
                    finish();
                }
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_tel);
        edGetCheckNum = (EditText) findViewById(R.id.edGetCheckNum);
        btnToCheckNum = (Button) findViewById(R.id.btnToCheckNum);
        mobilePhoneNumber = getIntent().getStringExtra("mobilePhoneNumber");

        btnToCheckNum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RegisterTool.toCheck(handler,mobilePhoneNumber,edGetCheckNum.getText().toString());
            }
        });
    }
}
