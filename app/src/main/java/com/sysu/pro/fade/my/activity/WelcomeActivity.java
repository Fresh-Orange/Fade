package com.sysu.pro.fade.my.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.sysu.pro.fade.MainActivity;
import com.sysu.pro.fade.R;
import com.sysu.pro.fade.utils.Const;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        SharedPreferences sharedPreferences = getSharedPreferences(Const.USER_SHARE,MODE_PRIVATE);
        String nickname = sharedPreferences.getString("nickname","");
        if(nickname == ""){
            startActivity(new Intent(WelcomeActivity.this,LoginActivity.class));
            finish();
        }else{
            startActivity(new Intent(WelcomeActivity.this,MainActivity.class));
            finish();
        }
//        startActivity(new Intent(WelcomeActivity.this,LoginActivity.class));
//        finish();
    }
}
