package com.sysu.pro.fade.my.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.sysu.pro.fade.Const;
import com.sysu.pro.fade.MainActivity;
import com.sysu.pro.fade.R;
import com.sysu.pro.fade.baseactivity.LoginActivitiesCollector;
import com.sysu.pro.fade.baseactivity.LoginBaseActivity;
import com.sysu.pro.fade.beans.Code;
import com.sysu.pro.fade.beans.SimpleResponse;
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
    private Button into_fade_btn;
    private SharedPreferences sharedPreferences;
    private LinearLayout red_wrong_password;
    private TextView red_text;
    private int fp_flag;
    private String fp;
    private String sp;
    private String mobilePhoneNumber;
    private ImageView backbtn;
    private int number_flag = 0;
    private int zifu_flag = 0;

    TextWatcher mTextWatchr = new TextWatcher() {
        private CharSequence temp;
        //private int editStart ;
        //private int editEnd ;
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            temp = charSequence;
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            //editStart = first_password.getSelectionStart();
            //editEnd = first_password.getSelectionEnd();
            judgePassword(temp.toString());
            if (temp.length() < 6) {
                if (temp.length() == 0){
                    red_wrong_password.setVisibility(View.INVISIBLE);
                }else{
                    red_text.setText("密码太短");
                    red_wrong_password.setVisibility(View.VISIBLE);
                    into_fade_btn.setBackgroundResource(R.drawable.button_shape_nomal);
                }
            }else  if (temp.length() > 16){
                red_text.setText("密码太长");
                red_wrong_password.setVisibility(View.VISIBLE);
                into_fade_btn.setBackgroundResource(R.drawable.button_shape_nomal);
            }else if(fp_flag == 1){
                red_text.setText("密码需包含英文与数字");
                red_wrong_password.setVisibility(View.VISIBLE);
                into_fade_btn.setBackgroundResource(R.drawable.button_shape_nomal);
            }else if (fp_flag == 2){
                red_wrong_password.setVisibility(View.INVISIBLE);
            }else{
                red_text.setText("密码包含非法字符");
                red_wrong_password.setVisibility(View.VISIBLE);
                into_fade_btn.setBackgroundResource(R.drawable.button_shape_nomal);
            }
        }
    };

    TextWatcher mTextWatchr1 = new TextWatcher() {
        private CharSequence temp;
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            temp = charSequence;
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void afterTextChanged(Editable editable) {
            if (temp.length() > 0 && fp_flag == 2){
                red_wrong_password.setVisibility(View.INVISIBLE);
                into_fade_btn.setBackgroundResource(R.drawable.button_shape_blue);
            }else{
                into_fade_btn.setBackgroundResource(R.drawable.button_shape_nomal);
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
        setContentView(R.layout.activity_new_password);
        first_password = (EditText) findViewById(R.id.my_telephone);
        second_password = (EditText) findViewById(R.id.my_valid);
        into_fade_btn = (Button) findViewById(R.id.into_fade);
        red_wrong_password = (LinearLayout) findViewById(R.id.red_wrong_password);
        red_text = (TextView) findViewById(R.id.red_text);
        first_password.addTextChangedListener(mTextWatchr);
        second_password.addTextChangedListener(mTextWatchr1);
        sharedPreferences = getSharedPreferences(Const.USER_SHARE,MODE_PRIVATE);
        mobilePhoneNumber = getIntent().getStringExtra("telephone");
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
                if (fp_flag == 2){
                    if (fp.equals(sp)){
                        userService.changePasswordTel(mobilePhoneNumber,fp)
                                .subscribeOn(Schedulers.newThread())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Subscriber<SimpleResponse>() {
                                    @Override
                                    public void onCompleted() {

                                    }
                                    @Override
                                    public void onError(Throwable e) {
                                        Log.e("修改","失败");
                                        Toast.makeText(SetNewPasswordActivity.this,"修改失败，请重新修改！",Toast.LENGTH_SHORT).show();
                                        //progressDialog.dismiss();
                                        e.printStackTrace();
                                    }
                                    @Override
                                    public void onNext(SimpleResponse response) {
                                        Log.i("success",response.toString());
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
                                    }
                                });
                    }else {
                        red_text.setText("密码不相同");
                        red_wrong_password.setVisibility(View.VISIBLE);
                    }
                }
                /*if (fp_flag == 0 && fp.equals(sp)){
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
                }*/
            }
        });
    }

    private void judgePassword(String account){
        fp_flag = 0;
        number_flag = 0;
        zifu_flag = 0;
        for (int i = 0; i < account.length(); i++){
            if ((account.charAt(i) > '9' || account.charAt(i) < '0')
                    && (account.charAt(i) > 'z' || account.charAt(i) < 'a')
                    && (account.charAt(i) > 'Z' || account.charAt(i) < 'A')){
                fp_flag = 3;
                break;
            }else if (account.charAt(i) <= '9' && account.charAt(i) >= '0'){
                number_flag = 1;
            }else if ((account.charAt(i) <= 'z' && account.charAt(i) >= 'a')
                    || (account.charAt(i) <= 'Z' && account.charAt(i) >= 'A')){
                zifu_flag = 1;
            }
        }
        if (fp_flag != 3){
            if (number_flag == 1 && zifu_flag == 1){
                fp_flag = 2;
            }else {
                fp_flag = 1;
            }
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
