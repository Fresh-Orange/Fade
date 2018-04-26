package com.sysu.pro.fade.my.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sysu.pro.fade.Const;
import com.sysu.pro.fade.R;
import com.sysu.pro.fade.baseactivity.LoginBaseActivity;
import com.sysu.pro.fade.beans.Code;
import com.sysu.pro.fade.tool.UserTool;

/**
 * Created by huanggzh5 on 2018/3/11.
 */

public class ValidationActivity extends LoginBaseActivity{
    Code mCode;
    private ImageView nextbtn;
    private ImageView backbtn;
    private LinearLayout red_wrong_valid;
    private TextView send_telephone;
    private TextView send_again;
    private String mobilePhoneNumber;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what == 1){
                String ans = (String) msg.obj;
                //Toast.makeText(CheckTelActivity.this,ans,Toast.LENGTH_SHORT).show();
                if(ans.equals("{}")){

                //验证成功，跳转到输入密码界面
                Intent intent = new Intent(ValidationActivity.this,SetNewPasswordActivity.class);
                intent.putExtra(Const.TELEPHONE,mobilePhoneNumber);
                startActivity(intent);

                finish();
                }else{
                    red_wrong_valid.setVisibility(View.VISIBLE);
                    handler2.sendEmptyMessageDelayed(1,1000);
                }
            }
        }
    };

    private Handler handler1 = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what == 1){
                String ans_str = (String) msg.obj;
                //Toast.makeText(RegisterBySMSActivity.this,ans_str,Toast.LENGTH_SHORT).show();
                //暂时取消验证限制，到时候将if语句恢复
                if(ans_str.equals("{}")){
                    Toast.makeText(ValidationActivity.this, "重发成功", Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

    private Handler handler2 = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what == 1){
                red_wrong_valid.setVisibility(View.INVISIBLE);
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        /**标题是属于View的，所以窗口所有的修饰部分被隐藏后标题依然有效,需要去掉标题**/
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_validation);
        mCode = (Code) findViewById(R.id.code);
        nextbtn = (ImageView) findViewById(R.id.next);
        backbtn = (ImageView) findViewById(R.id.back_btn);
        red_wrong_valid = (LinearLayout) findViewById(R.id.red_wrong_valid);
        send_telephone = (TextView) findViewById(R.id.send_telephone);
        send_again = (TextView) findViewById(R.id.send_again);
        mobilePhoneNumber = getIntent().getStringExtra("telephone");

        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        nextbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserTool.toCheck(handler,mobilePhoneNumber,mCode.getVerificationCode());
            }
        });

        mCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager inputMethodManager = (InputMethodManager)ValidationActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                red_wrong_valid.setVisibility(View.INVISIBLE);
            }
        });

        send_telephone.setText("验证码已发送至手机号 +86 " + mobilePhoneNumber);

        send_again.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserTool.sendIdentifyCode(handler1,mobilePhoneNumber);
            }
        });
    }

    /**
     * 监听删除键
     * @param view
     * @param i
     * @param keyEvent
     * @return

    @Override
    public boolean onKey(View view, int i, KeyEvent keyEvent) {
        if (i == KeyEvent.KEYCODE_DEL && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
            Message msg = new Message();
            msg.what = 1;
            handler2.sendMessage(msg);
            Log.d("delete", "success");
            return true;
        }
        return false;
    }*/
}
