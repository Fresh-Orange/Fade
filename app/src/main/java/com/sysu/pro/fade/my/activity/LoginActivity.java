package com.sysu.pro.fade.my.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.squareup.picasso.Picasso;
import com.sysu.pro.fade.Const;
import com.sysu.pro.fade.MainActivity;
import com.sysu.pro.fade.R;
import com.sysu.pro.fade.beans.SimpleResponse;
import com.sysu.pro.fade.beans.User;
import com.sysu.pro.fade.service.UserService;
import com.sysu.pro.fade.utils.RetrofitUtils;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Retrofit;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/*
用户名密码方式的登录界面
 */
public class LoginActivity extends AppCompatActivity {

    private ImageView iv_personal_icon;
    private EditText edAccount;
    private EditText edPassword;
    private ImageView btnLogin;
    private TextView tvToRegister;
    private SharedPreferences sharedPreferences;
    private ProgressDialog progressDialog;
    private ImageView backIcon1;    //登录界面的返回键
    private String accountType = Const.TELEPHONE;
    private String telephone;
    private String fade_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        iv_personal_icon = (ImageView) findViewById(R.id.ivLoginUserHead);
        edAccount = (EditText) findViewById(R.id.edAccount);
        edPassword = (EditText) findViewById(R.id.edPassword);
        btnLogin = (ImageView) findViewById(R.id.btnLogin);
        tvToRegister = (TextView) findViewById(R.id.tvToRegister);
        sharedPreferences = getSharedPreferences(Const.USER_SHARE,MODE_PRIVATE);
        progressDialog = new ProgressDialog(LoginActivity.this);
        backIcon1 = (ImageView) findViewById(R.id.back_icon_1);

        Retrofit retrofit = RetrofitUtils.createRetrofit(Const.BASE_IP,null);
        final UserService userService = retrofit.create(UserService.class);
        edPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                //当密码框的焦点改变时，请求头像url并设置
                if(hasFocus){
                    String  accunt = edAccount.getText().toString();
                    if(accunt != ""){
                        judgeAccount(accunt);
                        //UserTool.getHeadImageUrl(handler,accunt,accountType);
                        if(accountType.equals("telephone")){
                            telephone = accunt;
                        }else {
                            fade_name = accunt;
                        }
                        userService.getHeadImageUrl(telephone,fade_name,null)
                                .subscribeOn(Schedulers.newThread())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Subscriber<SimpleResponse>() {
                                    @Override
                                    public void onCompleted() {
                                    }
                                    @Override
                                    public void onError(Throwable e) {
                                        Log.e("获取头像","失败");
                                        e.printStackTrace();
                                    }
                                    @Override
                                    public void onNext(SimpleResponse simpleResponse) {
                                        Map<String,Object>extra = simpleResponse.getExtra();
                                        String head_image_url = (String) extra.get("head_image_url");
                                        if(simpleResponse.getErr() == null){
                                            if(head_image_url != null){
                                                Toast.makeText(LoginActivity.this,"成功获取用户头像",Toast.LENGTH_SHORT).show();
                                                Log.i("头像",Const.BASE_IP + head_image_url);
                                                Picasso.with(LoginActivity.this).load(Const.BASE_IP + head_image_url).into(iv_personal_icon);
                                                btnLogin.setImageResource(R.drawable.login_btn_active);
                                            }else{
                                                Toast.makeText(LoginActivity.this,"使用默认头像",Toast.LENGTH_SHORT).show();
                                                iv_personal_icon.setImageResource(R.drawable.login_head_ic);
                                                btnLogin.setImageResource(R.drawable.login_btn_inactive);
                                            }
                                        }else{
                                            iv_personal_icon.setImageResource(R.drawable.login_head_ic);
                                            btnLogin.setImageResource(R.drawable.login_btn_inactive);
                                            Toast.makeText(LoginActivity.this,simpleResponse.getErr(),Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
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
                    if(accountType.equals("telephone")){
                        telephone = account;
                        userService.loginUserByTel(telephone,password)
                                .subscribeOn(Schedulers.newThread())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Subscriber<User>() {
                                    @Override
                                    public void onCompleted() {

                                    }
                                    @Override
                                    public void onError(Throwable e) {
                                        Log.e("登录","失败");
                                        Toast.makeText(LoginActivity.this,"登录失败,账号或密码错误",Toast.LENGTH_SHORT).show();
                                        e.printStackTrace();
                                    }
                                    @Override
                                    public void onNext(User user) {
                                        Log.i("user",user.toString());
                                        if(user.getUser_id() != null){
                                            loginSuccess(user);
                                        }else {
                                            Toast.makeText(LoginActivity.this,"登录失败",Toast.LENGTH_SHORT).show();
                                            progressDialog.dismiss();
                                        }
                                    }
                                });
                    }else {
                        fade_name = account;
                        userService.loginUserByName(fade_name,password)
                                .subscribeOn(Schedulers.newThread())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Subscriber<User>() {
                                    @Override
                                    public void onCompleted() {
                                    }
                                    @Override
                                    public void onError(Throwable e) {
                                        Log.e("登录","失败");
                                    }
                                    @Override
                                    public void onNext(User user) {
                                        if(user.getUser_id() != null){
                                            loginSuccess(user);
                                        }else {
                                            Toast.makeText(LoginActivity.this,"登录失败",Toast.LENGTH_SHORT).show();
                                            progressDialog.dismiss();
                                        }
                                    }
                                });
                    }
                }else{
                    Toast.makeText(LoginActivity.this,"输入不能为空",Toast.LENGTH_SHORT).show();
                }
            }
        });
        //登录界面的返回icon
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

    public void loginSuccess(User user){
            Toast.makeText(LoginActivity.this,"登录成功",Toast.LENGTH_SHORT).show();
            //更新存储数据
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("user", JSON.toJSONString(user));
            //最后设置登陆类型 为账号密码登陆
            editor.putString(Const.LOGIN_TYPE,"0");
            editor.apply();
            progressDialog.dismiss();
            startActivity(new Intent(LoginActivity.this,MainActivity.class));
            finish();
    }
}
