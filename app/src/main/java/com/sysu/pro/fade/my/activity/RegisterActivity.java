package com.sysu.pro.fade.my.activity;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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

import com.sysu.pro.fade.Const;
import com.sysu.pro.fade.Permission;
import com.sysu.pro.fade.R;
import com.sysu.pro.fade.baseactivity.LoginBaseActivity;
import com.sysu.pro.fade.beans.SimpleResponse;
import com.sysu.pro.fade.service.UserService;
import com.sysu.pro.fade.tool.UserTool;
import com.sysu.pro.fade.utils.RetrofitUtil;
import com.unistrong.yang.zb_permission.ZbPermission;
import com.unistrong.yang.zb_permission.ZbPermissionFail;
import com.unistrong.yang.zb_permission.ZbPermissionSuccess;

import java.util.ArrayList;
import java.util.List;

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
    private Button registerbtn;
    private TextView loginbtn;
    private String mobilePhoneNumber;
    private String validation;
    private LinearLayout red_wrong_valid;
    private int flag = 0;
    private Integer count = 1;

    private final int REQUEST_LOCATION = 50;
    private final int REQUEST_STORAGE = 100;
    private final int REQUEST_CAMERA = 200;
    private final int REQUEST_AUDIO = 250;
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
                    //get_valid.setText("重发验证码");
                    flag = 1;
                }else{
                    Toast.makeText(RegisterActivity.this, "发送失败", Toast.LENGTH_SHORT).show();
                    //get_valid.setText("重发验证码");
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
                registerbtn.setBackgroundResource(R.drawable.button_shape_blue);
                red_wrong_valid.setVisibility(View.INVISIBLE);
            }else {
                registerbtn.setBackgroundResource(R.drawable.button_shape_nomal);
                red_wrong_valid.setVisibility(View.INVISIBLE);
            }
        }
    };

    private Handler handler3 = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what == 1){
                if (count == 1){
                    get_valid.setText("重发验证码");
                }else {
                    count--;
                    get_valid.setText(count+"s");
                    handler3.sendEmptyMessageDelayed(1,1000);
                }
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
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        /**标题是属于View的，所以窗口所有的修饰部分被隐藏后标题依然有效,需要去掉标题**/
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_main);
        getPermission();
        registerbtn = (Button) findViewById(R.id.btnRegister);
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
                if (!mobilePhoneNumber.equals("")){
                    UserTool.toCheck(handler,mobilePhoneNumber,validation);
                }
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
                if (count == 1){
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
                                        count = 59;
                                        get_valid.setText(count+"s");
                                        handler3.sendEmptyMessageDelayed(1,1000);
                                    }else{
                                        Toast.makeText(RegisterActivity.this,"该手机号已经注册",Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }

            }
        });
    }
    public void getPermission() {
//        List<String> permissionList = new ArrayList<>();
//        if (ContextCompat.checkSelfPermission(RegisterActivity.this, Manifest.permission
//                .ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
////            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
////                    ACCESS_FINE_LOCATION);
//        }
//        if (ContextCompat.checkSelfPermission(RegisterActivity.this, Manifest.permission
//                .CAMERA) != PackageManager.PERMISSION_GRANTED) {
//            permissionList.add(Manifest.permission.CAMERA);
////            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
////                    CAMERA);
//        }
////        if (ContextCompat.checkSelfPermission(RegisterActivity.this, Manifest.permission
////                .READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
////            permissionList.add(Manifest.permission.READ_PHONE_STATE);
//////            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE},
//////                    READ_PHONE_STATE);
////        }
//        if (ContextCompat.checkSelfPermission(RegisterActivity.this, Manifest.permission
//                .WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
////            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
////                    PERMISSION_WRITE_EXTERNAL_STORAGE);
//        }
//        if (ContextCompat.checkSelfPermission(RegisterActivity.this, Manifest.permission
//                .READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && Build.VERSION.SDK_INT > 15) {
//            permissionList.add(Manifest.permission.READ_EXTERNAL_STORAGE);
////            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
////                    PERMISSION_WRITE_EXTERNAL_STORAGE);
//        }
//        if (ContextCompat.checkSelfPermission(RegisterActivity.this, Manifest.permission
//                .RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
//            permissionList.add(Manifest.permission.RECORD_AUDIO);
////            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO},
////                    RECORD_AUDIO);
//        }
//        if (!permissionList.isEmpty()) {
//            String[] permissions = permissionList.toArray(new String[permissionList.size()]);
//            ActivityCompat.requestPermissions(RegisterActivity.this, permissions, 1);
//        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            ZbPermission.with(RegisterActivity.this)
                    .addRequestCode(REQUEST_LOCATION)
                    .permissions(android.Manifest.permission.CAMERA,
                            android.Manifest.permission.ACCESS_FINE_LOCATION,
                            android.Manifest.permission.ACCESS_COARSE_LOCATION,
                            android.Manifest.permission.RECORD_AUDIO,
                            android.Manifest.permission.READ_EXTERNAL_STORAGE,
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .request(
                            new ZbPermission.ZbPermissionCallback() {
                                @Override
                                public void permissionSuccess(int i) {

                                }

                                @Override
                                public void permissionFail(int requestCode) {
                                    Toast.makeText(RegisterActivity.this, "你禁用了其中一项权限，导致Fade不能正常运行" , Toast.LENGTH_LONG).show();
                                }
                            }
                    );
        }
        else {
            ZbPermission.with(RegisterActivity.this)
                    .addRequestCode(REQUEST_LOCATION)
                    .permissions(android.Manifest.permission.CAMERA,
                            android.Manifest.permission.ACCESS_FINE_LOCATION,
                            android.Manifest.permission.ACCESS_COARSE_LOCATION,
                            android.Manifest.permission.RECORD_AUDIO,
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .request(
                            new ZbPermission.ZbPermissionCallback() {
                                @Override
                                public void permissionSuccess(int i) {

                                }

                                @Override
                                public void permissionFail(int requestCode) {
                                    Toast.makeText(RegisterActivity.this, "你禁用了其中一项权限，导致Fade不能正常运行" , Toast.LENGTH_LONG).show();
                                }
                            }
                    );
        }
//        ZbPermission.with(RegisterActivity.this)
//                .addRequestCode(REQUEST_AUDIO)
//                .permissions(Permission.MICROPHONE)
//                .request(new ZbPermission.ZbPermissionCallback() {
//                    @Override
//                    public void permissionSuccess(int i) {
//
//                    }
//
//                    @Override
//                    public void permissionFail(int requestCode) {
//                        Toast.makeText(RegisterActivity.this, "你禁用了话筒权限，导致聊天不能进行话筒对话" , Toast.LENGTH_LONG).show();
//                    }
//                });
//
//        ZbPermission.with(RegisterActivity.this)
//                .addRequestCode(REQUEST_CAMERA)
//                .permissions(Permission.CAMERA)
//                .request(new ZbPermission.ZbPermissionCallback() {
//                    @Override
//                    public void permissionSuccess(int i) {
//
//                    }
//
//                    @Override
//                    public void permissionFail(int requestCode) {
//                        Toast.makeText(RegisterActivity.this, "你禁用了相机权限，导致不能正常打开相机" , Toast.LENGTH_LONG).show();
//                    }
//                });
    }

//    @ZbPermissionSuccess(requestCode = REQUEST_LOCATION)
//    public void permissionSuccess() {
//        Toast.makeText(RegisterActivity.this, "成功授予读写权限注解" , Toast.LENGTH_SHORT).show();
//    }





    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        ZbPermission.onRequestPermissionsResult(RegisterActivity.this, requestCode, permissions, grantResults);
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        switch (requestCode) {
//            case 1:
//                if (grantResults.length > 0) {
//                    for (int result : grantResults) {
//                        if (result != PackageManager.PERMISSION_GRANTED) {
//                            Toast.makeText(this, "您拒绝了其中一种权限，无法正常完整运行某些功能，请在后台权限管理打开此功能",
//                                    Toast.LENGTH_LONG).show();
////                            finish();
//                            return;
//                        }
//                    }
//                }
//                else {
//                    Toast.makeText(this, "发生未知错误",
//                            Toast.LENGTH_SHORT).show();
//                    finish();
//                }
//                break;
////            case ACCESS_FINE_LOCATION:
////                if (grantResults.length > 0) {
////                    for (int result : grantResults) {
////                        if (result != PackageManager.PERMISSION_GRANTED) {
////                            Toast.makeText(this, "您拒绝了定位权限，无法进行帖子定位，请在后台权限管理打开此功能",
////                                    Toast.LENGTH_SHORT).show();
//////                            finish();
////                            return;
////                        }
////                    }
////                }
////                else {
////                    Toast.makeText(this, "发生未知错误",
////                            Toast.LENGTH_SHORT).show();
////                    finish();
////                }
////                break;
////            case PERMISSION_WRITE_EXTERNAL_STORAGE:
////                if (grantResults.length > 0) {
////                    for (int result : grantResults) {
////                        if (result != PackageManager.PERMISSION_GRANTED) {
////                            Toast.makeText(this, "您拒绝了读写内存权限，无法访问内部相册，请在后台权限管理打开此功能",
////                                    Toast.LENGTH_SHORT).show();
//////                            finish();
////                            return;
////                        }
////                    }
////                }
////                else {
////                    Toast.makeText(this, "发生未知错误",
////                            Toast.LENGTH_SHORT).show();
////                    finish();
////                }
////                break;
////            case READ_PHONE_STATE:
////                if (grantResults.length > 0) {
////                    for (int result : grantResults) {
////                        if (result != PackageManager.PERMISSION_GRANTED) {
////                            Toast.makeText(this, "您拒绝了手机状态权限，无法读取手机状态，请在后台权限管理打开此功能",
////                                    Toast.LENGTH_SHORT).show();
//////                            finish();
////                            return;
////                        }
////                    }
////                }
////                else {
////                    Toast.makeText(this, "发生未知错误",
////                            Toast.LENGTH_SHORT).show();
////                    finish();
////                }
////                break;
////            case RECORD_AUDIO:
////                if (grantResults.length > 0) {
////                    for (int result : grantResults) {
////                        if (result != PackageManager.PERMISSION_GRANTED) {
////                            Toast.makeText(this, "您拒绝了访问麦克风权限，无法进行语音输入，请在后台权限管理打开此功能",
////                                    Toast.LENGTH_SHORT).show();
//////                            finish();
////                            return;
////                        }
////                    }
////                }
////                else {
////                    Toast.makeText(this, "发生未知错误",
////                            Toast.LENGTH_SHORT).show();
////                    finish();
////                }
////                break;
//        }
//    }
}
