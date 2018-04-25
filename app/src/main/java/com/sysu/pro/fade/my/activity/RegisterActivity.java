package com.sysu.pro.fade.my.activity;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sysu.pro.fade.Const;
import com.sysu.pro.fade.R;
import com.sysu.pro.fade.baseactivity.LoginBaseActivity;
import com.sysu.pro.fade.beans.SimpleResponse;
import com.sysu.pro.fade.service.UserService;
import com.sysu.pro.fade.tool.UserTool;
import com.sysu.pro.fade.utils.RetrofitUtil;

import retrofit2.Retrofit;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by huanggzh5 on 2018/3/9.
 */

public class RegisterActivity extends LoginBaseActivity {
    private EditText my_telephone;
    private EditText my_valid;
    private TextView get_valid;
    private ImageView registerbtn;
    private TextView loginbtn;
    private String mobilePhoneNumber;
    private String validation;
    private LinearLayout red_wrong_valid;
    private int flag = 0;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what == 1){
                String ans = (String) msg.obj;
                //Toast.makeText(CheckTelActivity.this,ans,Toast.LENGTH_SHORT).show();
                if(ans.equals("{}")){

                    //验证成功，跳转到输入密码界面
                    Intent intent = new Intent(RegisterActivity.this,SetPasswordActivity.class);
                    intent.putExtra(Const.TELEPHONE,mobilePhoneNumber);
                    startActivity(intent);

                    //finish();
                }else{
                    red_wrong_valid.setVisibility(View.VISIBLE);
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
                    Toast.makeText(RegisterActivity.this, "发送成功", Toast.LENGTH_SHORT).show();
                    get_valid.setText("重发验证码");
                    flag = 1;
                }else{
                    Toast.makeText(RegisterActivity.this, "发送失败", Toast.LENGTH_SHORT).show();
                    get_valid.setText("重发验证码");
                    flag = 2;
                }
            }
        }
    };

    private Handler handle2 = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                registerbtn.setImageResource(R.drawable.register_blue);
                red_wrong_valid.setVisibility(View.INVISIBLE);
            }else {
                registerbtn.setImageResource(R.drawable.register_gray);
                red_wrong_valid.setVisibility(View.INVISIBLE);
            }
        }
    };

    TextWatcher mTextWatchr = new TextWatcher() {
        private CharSequence temp;
        private int editStart ;
        private int editEnd ;
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            temp = charSequence;
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            editStart = my_valid.getSelectionStart();
            editEnd = my_valid.getSelectionEnd();
            if (temp.length() == 6) {
                Message msg = new Message();
                msg.what = 1;
                handle2.sendMessage(msg);
            }else {
                Message msg = new Message();
                msg.what = 2;
                handle2.sendMessage(msg);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_main);
        registerbtn = (ImageView) findViewById(R.id.btnRegister);
        loginbtn = (TextView) findViewById(R.id.tologin);
        get_valid = (TextView) findViewById(R.id.get_valid);
        my_telephone = (EditText) findViewById(R.id.my_telephone);
        my_valid = (EditText) findViewById(R.id.my_valid);
        red_wrong_valid = (LinearLayout) findViewById(R.id.red_wrong_valid);
        my_valid.addTextChangedListener(mTextWatchr);

        Retrofit retrofit = RetrofitUtil.createRetrofit(Const.BASE_IP,null);
        final UserService userService = retrofit.create(UserService.class);

        registerbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mobilePhoneNumber = my_telephone.getText().toString();
                validation = my_valid.getText().toString();
                UserTool.toCheck(handler,mobilePhoneNumber,validation);
                //startActivity(new Intent(RegisterActivity.this, SetPasswordActivity.class));
            }
        });

        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this, LoginMainActivity.class));
            }
        });

        get_valid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mobilePhoneNumber = my_telephone.getText().toString();
                userService.registerQueryTel(mobilePhoneNumber)
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Subscriber<SimpleResponse>() {
                            @Override
                            public void onCompleted() {
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.e("register","查询手机号失败");
                                e.printStackTrace();
                            }

                            @Override
                            public void onNext(SimpleResponse simpleResponse) {
                                if(simpleResponse.getSuccess().equals("0")){
                                    UserTool.sendIdentifyCode(handler1,mobilePhoneNumber);
                                }else{
                                    Toast.makeText(RegisterActivity.this,"该手机号已经注册",Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }
}
