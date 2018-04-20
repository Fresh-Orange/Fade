package com.sysu.pro.fade.my.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.sysu.pro.fade.Const;
import com.sysu.pro.fade.MainActivity;
import com.sysu.pro.fade.R;
import com.sysu.pro.fade.baseactivity.LoginActivitiesCollector;
import com.sysu.pro.fade.baseactivity.LoginBaseActivity;
import com.sysu.pro.fade.beans.Code;
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

public class SetNewPasswordActivity extends LoginBaseActivity {
    private EditText first_password;
    private EditText second_password;
    private ImageView into_fade_btn;
    private SharedPreferences sharedPreferences;
    private LinearLayout red_wrong_valid;
    private int fp_flag;
    private String fp;
    private String sp;
    private String mobilePhoneNumber;
    private ImageView backbtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        /**标题是属于View的，所以窗口所有的修饰部分被隐藏后标题依然有效,需要去掉标题**/
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_password);
        first_password = (EditText) findViewById(R.id.my_telephone);
        second_password = (EditText) findViewById(R.id.my_valid);
        into_fade_btn = (ImageView) findViewById(R.id.into_fade);
        red_wrong_valid = (LinearLayout) findViewById(R.id.red_wrong_valid);
        sharedPreferences = getSharedPreferences(Const.USER_SHARE,MODE_PRIVATE);
        mobilePhoneNumber = getIntent().getStringExtra("mobilePhoneNumber");
        backbtn = (ImageView) findViewById(R.id.back_btn);

        Retrofit retrofit = RetrofitUtil.createRetrofit(Const.BASE_IP,null);
        final UserService userService = retrofit.create(UserService.class);

        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        into_fade_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fp = first_password.getText().toString();
                sp = second_password.getText().toString();
                judgePassword(fp);
                if (fp_flag == 0 && fp.equals(sp)){
                    userService.loginUserByTel(mobilePhoneNumber,fp)
                            .subscribeOn(Schedulers.newThread())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Subscriber<User>() {
                                @Override
                                public void onCompleted() {

                                }
                                @Override
                                public void onError(Throwable e) {
                                    Log.e("登录","失败");
                                    Toast.makeText(SetNewPasswordActivity.this,"登录失败,账号或密码错误",Toast.LENGTH_SHORT).show();
                                    //progressDialog.dismiss();
                                    e.printStackTrace();
                                }
                                @Override
                                public void onNext(User user) {
                                    Log.i("user",user.toString());
                                    if(user.getUser_id() != null){
                                        loginSuccess(user);
                                    }else {
                                        Toast.makeText(SetNewPasswordActivity.this,"登录失败",Toast.LENGTH_SHORT).show();
                                        //progressDialog.dismiss();
                                    }
                                }
                            });
                }else{
                    Intent intent = new Intent(SetNewPasswordActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    private void judgePassword(String account){
        Pattern p = Pattern.compile("/^[a-zA-Z0-9]{4,16}$/");
        Matcher m = p.matcher(account);
        if(m.matches()){
            fp_flag = 0;
        }else{
            fp_flag = 1;
        }
    }

    public void loginSuccess(User user){
        Toast.makeText(SetNewPasswordActivity.this,"登录成功",Toast.LENGTH_SHORT).show();
        //更新存储数据
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("user", JSON.toJSONString(user));
        //最后设置登陆类型 为账号密码登陆
        editor.putString(Const.LOGIN_TYPE,"0");
        editor.commit();
        //progressDialog.dismiss();
        startActivity(new Intent(SetNewPasswordActivity.this,MainActivity.class));
        LoginActivitiesCollector.finishAll();
    }
}
