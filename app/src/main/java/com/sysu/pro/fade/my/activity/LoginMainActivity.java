package com.sysu.pro.fade.my.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.sysu.pro.fade.Const;
import com.sysu.pro.fade.MainActivity;
import com.sysu.pro.fade.R;
import com.sysu.pro.fade.baseactivity.LoginActivitiesCollector;
import com.sysu.pro.fade.baseactivity.LoginBaseActivity;
import com.sysu.pro.fade.beans.User;
import com.sysu.pro.fade.service.UserService;
import com.sysu.pro.fade.utils.RetrofitUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Retrofit;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by huanggzh5 on 2018/3/11.
 */

public class LoginMainActivity extends LoginBaseActivity {
    private TextView forget_password_btn;
    private ImageView backbtn;
    private ImageView loginbtn;
    private EditText input_account;
    private EditText input_password;
    private SharedPreferences sharedPreferences;
    private ProgressDialog progressDialog;
    private String accountType = Const.TELEPHONE;
    private String telephone;
    private String fade_name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        /**标题是属于View的，所以窗口所有的修饰部分被隐藏后标题依然有效,需要去掉标题**/
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_main);

        forget_password_btn = (TextView) findViewById(R.id.forget_password);
        backbtn = (ImageView) findViewById(R.id.back_btn);
        loginbtn = (ImageView) findViewById(R.id.login);
        input_account = (EditText) findViewById(R.id.my_telephone);
        input_password = (EditText) findViewById(R.id.my_password);
        sharedPreferences = getSharedPreferences(Const.USER_SHARE,MODE_PRIVATE);
        progressDialog = new ProgressDialog(LoginMainActivity.this);

        Retrofit retrofit = RetrofitUtil.createRetrofit(Const.BASE_IP,null);
        final UserService userService = retrofit.create(UserService.class);

        forget_password_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginMainActivity.this, TelephoneActivity.class));
                //Toast.makeText(LoginMainActivity.this, "功能暂未完善", Toast.LENGTH_SHORT).show();
            }
        });

        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String account = input_account.getText().toString();
                String password = input_password.getText().toString();
                if((!account.equals("")) && (!password.equals(""))){
                    //progressDialog.show();
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
                                        Toast.makeText(LoginMainActivity.this,"登录失败,账号或密码错误",Toast.LENGTH_SHORT).show();
                                        //progressDialog.dismiss();
                                        e.printStackTrace();
                                    }
                                    @Override
                                    public void onNext(User user) {
                                        Log.i("user",user.toString());
                                        if(user.getUser_id() != null){
                                            loginSuccess(user);
                                        }else {
                                            Toast.makeText(LoginMainActivity.this,"登录失败",Toast.LENGTH_SHORT).show();
                                            //progressDialog.dismiss();
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
                                            Toast.makeText(LoginMainActivity.this,"登录失败",Toast.LENGTH_SHORT).show();
                                            //progressDialog.dismiss();
                                        }
                                    }
                                });
                    }
                }else{
                    Toast.makeText(LoginMainActivity.this,"输入不能为空",Toast.LENGTH_SHORT).show();
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

    public void loginSuccess(User user){
        Toast.makeText(LoginMainActivity.this,"登录成功",Toast.LENGTH_SHORT).show();
        //更新存储数据
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("user", JSON.toJSONString(user));
        //最后设置登陆类型 为账号密码登陆
        editor.putString(Const.LOGIN_TYPE,"0");
        editor.commit();
        //progressDialog.dismiss();
        startActivity(new Intent(LoginMainActivity.this,MainActivity.class));
        LoginActivitiesCollector.finishAll();
    }
}
