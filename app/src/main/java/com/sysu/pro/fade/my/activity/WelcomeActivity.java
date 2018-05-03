package com.sysu.pro.fade.my.activity;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.sysu.pro.fade.MainActivity;
import com.sysu.pro.fade.R;
import com.sysu.pro.fade.Const;
import com.sysu.pro.fade.baseactivity.LoginBaseActivity;

import java.util.ArrayList;
import java.util.List;

/*
app启动时的欢迎界面
 */
public class WelcomeActivity extends LoginBaseActivity {

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            SharedPreferences sharedPreferences = getSharedPreferences(Const.USER_SHARE,MODE_PRIVATE);
            String login_type = sharedPreferences.getString(Const.LOGIN_TYPE,"");

            if(login_type == ""){
                startActivity(new Intent(WelcomeActivity.this,RegisterActivity.class));
                finish();
            }else{
                startActivity(new Intent(WelcomeActivity.this,MainActivity.class));
                finish();
            }
            super.handleMessage(msg);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        /**标题是属于View的，所以窗口所有的修饰部分被隐藏后标题依然有效,需要去掉标题**/
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_welcome);



        handler.sendEmptyMessageDelayed(0,2000);

    }


}
