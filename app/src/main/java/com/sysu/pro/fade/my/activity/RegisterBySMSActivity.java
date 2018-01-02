package com.sysu.pro.fade.my.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

/*
验证手机号码界面
 */
public class RegisterBySMSActivity extends LoginBaseActivity {

    private Button btnSubmitTel;
    private EditText edTelphone;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what == 1){
                String ans_str = (String) msg.obj;
                //Toast.makeText(RegisterBySMSActivity.this,ans_str,Toast.LENGTH_SHORT).show();
                //暂时取消验证限制，到时候将if语句恢复
                //if(ans_str.equals("{}")){
                    Intent intent = new Intent(RegisterBySMSActivity.this,CheckTelActivity.class);
                    intent.putExtra("mobilePhoneNumber",edTelphone.getText().toString());
                    startActivity(intent);
                    finish();
                //}
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_by_sms);
        btnSubmitTel = (Button) findViewById(R.id.btnSubmitTel);
        edTelphone = (EditText) findViewById(R.id.edTelphone);

        btnSubmitTel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //先校验该手机号是否已经被注册
                //UserTool.checkTel(handler,edTelphone.getText().toString());
                Retrofit retrofit = RetrofitUtil.createRetrofit(Const.BASE_IP,null);
                UserService userService = retrofit.create(UserService.class);
                userService.registerQueryTel(edTelphone.getText().toString())
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
                                    Toast.makeText(RegisterBySMSActivity.this,"该手机号没有注册",Toast.LENGTH_SHORT).show();
                                    UserTool.sendIdentifyCode(handler,edTelphone.getText().toString());
                                }else{
                                    Toast.makeText(RegisterBySMSActivity.this,"该手机号已经注册",Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

    }



}
