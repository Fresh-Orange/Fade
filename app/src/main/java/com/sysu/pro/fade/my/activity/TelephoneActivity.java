package com.sysu.pro.fade.my.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
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
 * Created by huanggzh5 on 2018/3/11.
 */

public class TelephoneActivity extends LoginBaseActivity {
    private ImageView nextbtn;
    private EditText telephone;
    private ImageView backbtn;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what == 1){
                String ans_str = (String) msg.obj;
                //Toast.makeText(RegisterBySMSActivity.this,ans_str,Toast.LENGTH_SHORT).show();
                //暂时取消验证限制，到时候将if语句恢复
                if(ans_str.equals("{}")){
                Intent intent = new Intent(TelephoneActivity.this,ValidationActivity.class);
                intent.putExtra("mobilePhoneNumber",telephone.getText().toString());
                startActivity(intent);
                finish();
                }
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
        setContentView(R.layout.activity_telephone);
        nextbtn = (ImageView) findViewById(R.id.next);
        backbtn = (ImageView) findViewById(R.id.back_btn);
        telephone = (EditText) findViewById(R.id.my_telephone);

        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        nextbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //先校验该手机号是否已经被注册
                //UserTool.checkTel(handler,edTelphone.getText().toString());
                Retrofit retrofit = RetrofitUtil.createRetrofit(Const.BASE_IP,null);
                UserService userService = retrofit.create(UserService.class);
                userService.registerQueryTel(telephone.getText().toString())
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
                                    Toast.makeText(TelephoneActivity.this,"该手机号没有注册",Toast.LENGTH_SHORT).show();
                                }else{
                                    UserTool.sendIdentifyCode(handler,telephone.getText().toString());
                                }
                            }
                        });
            }
        });
    }
}
