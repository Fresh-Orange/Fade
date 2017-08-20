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

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.sysu.pro.fade.R;
import com.sysu.pro.fade.MainActivity;
import com.sysu.pro.fade.utils.Const;
import com.sysu.pro.fade.tool.LoginTool;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
用户名密码方式的登录界面
 */
public class LoginActivity extends AppCompatActivity {

    private ImageView iv_personal_icon;
    private EditText edAccount;
    private EditText edPassword;
    private Button btnLogin;
    private TextView tvToRegister;
    private SharedPreferences sharedPreferences;
    private ProgressDialog progressDialog;

    private String accountType = Const.TELEPHONE;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == 1){
                String ans_str = (String) msg.obj;
                String image_url = "";  String nickname = "";   String fade_name = "";
                String telephone = "";  String sex = "";        Integer ans = 0;
                String mail = "";       String register_time="";String summary="";
                String wallpaper_url = "";  String aera = "";
                Integer user_id = 0;
                Integer concern_num = 0; Integer fans_num = 0;
                String wehcat_id = ""; String weibo_id = "";  String qq_id="";

                try {
                    JSONObject jsonObject = new JSONObject(ans_str);
                    ans = jsonObject.getInt("ans");
                    image_url = jsonObject.getString(Const.IMAGE_URL);
                    nickname = jsonObject.getString(Const.NICKNAME);
                    fade_name = jsonObject.getString(Const.FADE_NAME);
                    telephone = jsonObject.getString(Const.TELEPHONE);
                    sex = jsonObject.getString(Const.SEX);
                    user_id = jsonObject.getInt(Const.USER_ID);
                    concern_num = jsonObject.getInt(Const.CONCERN_NUM);
                    fans_num = jsonObject.getInt(Const.FANS_NUM);
                    mail = jsonObject.getString(Const.MAIL);
                    register_time = jsonObject.getString(Const.REGISTER_TIME);
                    summary = jsonObject.getString(Const.SUMMARY);
                    wallpaper_url = jsonObject.getString(Const.WALLPAPER_URL);
                    wehcat_id = jsonObject.getString(Const.WECHAT_ID);
                    weibo_id = jsonObject.getString(Const.WEIBO_ID);
                    qq_id = jsonObject.getString(Const.QQ_ID);
                    aera = jsonObject.getString(Const.AREA);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if(ans == 1){
                    Toast.makeText(LoginActivity.this,"登录成功",Toast.LENGTH_SHORT).show();
                    //更新存储数据
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(Const.LOGIN_TYPE,"0");

                    if(accountType.equals(Const.TELEPHONE))
                        editor.putString(Const.TELEPHONE,edAccount.getText().toString());
                    if(accountType.equals(Const.FADE_NAME))
                        editor.putString(Const.FADE_NAME,edAccount.getText().toString());

                    editor.putString(Const.PASSWORD,edPassword.getText().toString());
                    editor.putString(Const.IMAGE_URL,image_url);
                    editor.putString(Const.NICKNAME,nickname);
                    editor.putString(Const.FADE_NAME,fade_name);
                    editor.putString(Const.TELEPHONE,telephone);
                    editor.putString(Const.SEX,sex);
                    editor.putInt(Const.USER_ID,user_id);
                    editor.putInt(Const.CONCERN_NUM,concern_num);
                    editor.putInt(Const.FANS_NUM,fans_num);
                    editor.putString(Const.MAIL,mail);
                    editor.putString(Const.REGISTER_TIME,register_time);
                    editor.putString(Const.SUMMARY,summary);
                    editor.putString(Const.WALLPAPER_URL,wallpaper_url);
                    editor.putString(Const.WECHAT_ID,wehcat_id);
                    editor.putString(Const.WEIBO_ID,weibo_id);
                    editor.putString(Const.QQ_ID,qq_id);
                    editor.putString(Const.AREA,aera);

                    editor.commit();
                    startActivity(new Intent(LoginActivity.this,MainActivity.class));
                    progressDialog.dismiss();
                    finish();
                }else{
                    Toast.makeText(LoginActivity.this,"登录失败，账号密码错误",Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            }
            if(msg.what == 2){
                String ans_str = (String) msg.obj;
                Integer ans = 0;  String image_url2 = "";
                try {
                    JSONObject jsonObject2 = new JSONObject(ans_str);
                    image_url2 = jsonObject2.getString(Const.IMAGE_URL);
                    ans = jsonObject2.getInt("ans");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if(image_url2 != null){
                     if(ans == 1 && (!image_url2.equals(""))){
                         Toast.makeText(LoginActivity.this,"成功获取用户头像",Toast.LENGTH_SHORT).show();
                         Picasso.with(LoginActivity.this).load(image_url2).into(iv_personal_icon);
                     }else{
                         Toast.makeText(LoginActivity.this,"使用默认头像",Toast.LENGTH_SHORT).show();
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
        edAccount = (EditText) findViewById(R.id.edAccount);
        edPassword = (EditText) findViewById(R.id.edPassword);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        tvToRegister = (TextView) findViewById(R.id.tvToRegister);
        sharedPreferences = getSharedPreferences(Const.USER_SHARE,MODE_PRIVATE);
        progressDialog = new ProgressDialog(LoginActivity.this);


        edPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                //当密码框的焦点改变时，请求头像url并设置
                if(hasFocus){
                    String  accunt = edAccount.getText().toString();
                    if(accunt != ""){
                        judgeAccount(accunt);
                        LoginTool.getHeadImageUrl(handler,Const.IP,accunt,accountType);
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
                    LoginTool.sendToLogin(handler, Const.IP,password,account,accountType);
                }else{
                    Toast.makeText(LoginActivity.this,"输入不能为空",Toast.LENGTH_SHORT).show();
                }

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
