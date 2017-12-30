package com.sysu.pro.fade.my.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.sysu.pro.fade.R;
import com.sysu.pro.fade.tool.UserTool;
import com.sysu.pro.fade.Const;

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
                //Toast.makeText(CheckTelActivity.this,ans,Toast.LENGTH_SHORT).show();
                //if(ans.equals("{}")){

                    //验证成功，跳转到输入密码界面
                    Intent intent = new Intent(CheckTelActivity.this,AddPasswordActivity.class);
                    intent.putExtra(Const.TELEPHONE,mobilePhoneNumber);
                    startActivity(intent);

                    finish();
                //}
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
                UserTool.toCheck(handler,mobilePhoneNumber,edGetCheckNum.getText().toString());
            }
        });
    }
}
