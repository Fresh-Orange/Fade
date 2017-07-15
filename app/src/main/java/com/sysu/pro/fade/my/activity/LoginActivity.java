package com.sysu.pro.fade.my.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.sysu.pro.fade.R;
import com.sysu.pro.fade.MainActivity;
import com.sysu.pro.fade.utils.Const;
import com.sysu.pro.fade.tool.LoginTool;

public class LoginActivity extends AppCompatActivity {

    private ImageView iv_personal_icon;
    private EditText edUserName;
    private EditText edPassword;
    private Button btnLogin;
    private String   imagePath;
    private TextView tvToRegister;
    private SharedPreferences sharedPreferences;
    private ProgressDialog progressDialog;


    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == 1){
                String ans = (String) msg.obj;
                Toast.makeText(LoginActivity.this,ans,Toast.LENGTH_SHORT).show();
                if(ans.equals("登陆成功")){
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("nickname",edUserName.getText().toString());
                    editor.putString("password",edPassword.getText().toString());
                    editor.putString("head_image",imagePath);
                    editor.commit();
                    startActivity(new Intent(LoginActivity.this,MainActivity.class));
                    progressDialog.dismiss();
                    finish();
                }else{
                    progressDialog.dismiss();
                }
            }
            if(msg.what == 2){
                imagePath = (String) msg.obj;
                if(imagePath != null){
                     if(!(imagePath.equals("") || imagePath.equals("用户不存在") || imagePath.equals("未设置头像") )){
                         Toast.makeText(LoginActivity.this,imagePath,Toast.LENGTH_SHORT).show();
                         Picasso.with(LoginActivity.this).load(imagePath).into(iv_personal_icon);
                     }else{
                         iv_personal_icon.setImageResource(R.drawable.default_head);
                     }
                }else{
                    iv_personal_icon.setImageResource(R.drawable.default_head);
                }

            }

            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        iv_personal_icon = (ImageView) findViewById(R.id.ivLoginUserHead);
        edUserName = (EditText) findViewById(R.id.edLoginNickname);
        edPassword = (EditText) findViewById(R.id.edLoginPassword);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        tvToRegister = (TextView) findViewById(R.id.tvToRegister);
        sharedPreferences = getSharedPreferences(Const.USER_SHARE,MODE_PRIVATE);
        progressDialog = new ProgressDialog(LoginActivity.this);


        edPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    String  name = edUserName.getText().toString();
                    if(name != ""){
//                        Toast.makeText(LoginActivity.this,name,Toast.LENGTH_SHORT).show();
                        LoginTool.getHeadImageUrl(handler,Const.IP,name);
                    }
                }
            }
        });


        tvToRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
                finish();
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                String nickname = edUserName.getText().toString();
                String password = edPassword.getText().toString();

                LoginTool.sendToLogin(handler, Const.IP,nickname,password);
            }
        });


    }
}
