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
import com.sysu.pro.fade.tool.UserTool;
import com.sysu.pro.fade.Const;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.sysu.pro.fade.Const.FANS_NUM;
import static com.sysu.pro.fade.Const.WALLPAPER_URL;

/*
用户名密码方式的登录界面
 */
public class LoginActivity extends AppCompatActivity {

    private ImageView iv_personal_icon;
    private EditText edAccount;
    private EditText edPassword;
    private TextView btnLogin;
    private TextView tvToRegister;
    private SharedPreferences sharedPreferences;
    private ProgressDialog progressDialog;
    private ImageView backIcon1;    //登录界面的返回键

    private String accountType = Const.TELEPHONE;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == 1){
                Map<String,Object>ans_map = (Map<String, Object>) msg.obj;
                String head_image_url = (String) ans_map.get(Const.HEAD_IMAGE_URL);
                String nickname = (String) ans_map.get(Const.NICKNAME);
                String fade_name = (String) ans_map.get(Const.FADE_NAME);
                String telephone = (String) ans_map.get(Const.TELEPHONE);
                String sex = (String) ans_map.get(Const.SEX);
                String err  = (String) ans_map.get(Const.ERR);
                String register_time= (String) ans_map.get(Const.REGISTER_TIME);
                String summary = (String) ans_map.get(Const.SUMMARY);
                String wallpaper_url = (String) ans_map.get(WALLPAPER_URL);
                String aera = (String) ans_map.get(Const.AREA);
                Integer user_id = (Integer) ans_map.get(Const.USER_ID);
                Integer concern_num = (Integer) ans_map.get(Const.CONCERN_NUM);
                Integer fans_num = (Integer) ans_map.get(FANS_NUM);
                String wehcat_id = (String) ans_map.get(Const.WECHAT_ID);
                String weibo_id = (String) ans_map.get(Const.WEIBO_ID);
                String qq_id = (String) ans_map.get(Const.QQ_ID);
                String school = (String) ans_map.get(Const.SCHOOL);

                if(err == null){
                    Toast.makeText(LoginActivity.this,"登录成功",Toast.LENGTH_SHORT).show();
                    //更新存储数据
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(Const.LOGIN_TYPE,"0");

                    if(accountType.equals(Const.TELEPHONE))
                        editor.putString(Const.TELEPHONE,edAccount.getText().toString());
                    if(accountType.equals(Const.FADE_NAME))
                        editor.putString(Const.FADE_NAME,edAccount.getText().toString());

                    editor.putString(Const.PASSWORD,edPassword.getText().toString());
                    editor.putString(Const.HEAD_IMAGE_URL,head_image_url);
                    editor.putString(Const.NICKNAME,nickname);
                    editor.putString(Const.FADE_NAME,fade_name);
                    editor.putString(Const.TELEPHONE,telephone);
                    editor.putString(Const.SEX,sex);
                    editor.putInt(Const.USER_ID,user_id);
                    editor.putInt(Const.CONCERN_NUM,concern_num);
                    editor.putInt(FANS_NUM,fans_num);
                    editor.putString(Const.REGISTER_TIME,register_time);
                    editor.putString(Const.SUMMARY,summary);
                    editor.putString(WALLPAPER_URL,wallpaper_url);
                    editor.putString(Const.WECHAT_ID,wehcat_id);
                    editor.putString(Const.WEIBO_ID,weibo_id);
                    editor.putString(Const.QQ_ID,qq_id);
                    editor.putString(Const.AREA,aera);
                    editor.putString(Const.SCHOOL,school);
                    editor.commit();
                    startActivity(new Intent(LoginActivity.this,MainActivity.class));
                    progressDialog.dismiss();
                    finish();
                }else{
                    Toast.makeText(LoginActivity.this,err,Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            }
            else if(msg.what == 2){
                Map<String,Object>ans_map = (Map<String, Object>) msg.obj;
                String err = (String) ans_map.get(Const.ERR);
                String head_image_url2 = (String) ans_map.get(Const.HEAD_IMAGE_URL);
                if(err == null){
                     if(head_image_url2 != null){
                         Toast.makeText(LoginActivity.this,"成功获取用户头像",Toast.LENGTH_SHORT).show();
                         Picasso.with(LoginActivity.this).load(head_image_url2).into(iv_personal_icon);
                     }else{
                         Toast.makeText(LoginActivity.this,"使用默认头像",Toast.LENGTH_SHORT).show();
                         iv_personal_icon.setImageResource(R.drawable.default_head);
                     }
                }else{
                    iv_personal_icon.setImageResource(R.drawable.default_head);
                    Toast.makeText(LoginActivity.this,err,Toast.LENGTH_SHORT).show();
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
        edAccount = (EditText) findViewById(R.id.edAccount);
        edPassword = (EditText) findViewById(R.id.edPassword);
        btnLogin = (TextView) findViewById(R.id.btnLogin);
        tvToRegister = (TextView) findViewById(R.id.tvToRegister);
        sharedPreferences = getSharedPreferences(Const.USER_SHARE,MODE_PRIVATE);
        progressDialog = new ProgressDialog(LoginActivity.this);
        backIcon1 = (ImageView) findViewById(R.id.back_icon_1);

        edPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                //当密码框的焦点改变时，请求头像url并设置
                if(hasFocus){
                    String  accunt = edAccount.getText().toString();
                    if(accunt != ""){
                        judgeAccount(accunt);
                        UserTool.getHeadImageUrl(handler,Const.IP,accunt,accountType);
                    }
                }
            }
        });


        tvToRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,RegisterBySMSActivity.class));
                finish();
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String account = edAccount.getText().toString();
                String password = edPassword.getText().toString();
                if((!account.equals("")) && (!password.equals(""))){
                    progressDialog.show();
                    judgeAccount(account);
                    UserTool.sendToLogin(handler, Const.IP,password,account,accountType);
                }else{
                    Toast.makeText(LoginActivity.this,"输入不能为空",Toast.LENGTH_SHORT).show();
                }

            }
        });

        backIcon1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void judgeAccount(String account){
        Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
        Matcher m = p.matcher(account);
        if(m.matches()){
            accountType = Const.TELEPHONE;
        }else{
            accountType = Const.FADE_NAME;
        }

    }
}
